package com.company.vansales.app.view.fragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.company.vansales.R
import com.company.vansales.app.NextActivity
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.activity.StartDayActivity
import com.company.vansales.app.activity.VisitsActivity
import com.company.vansales.app.datamodel.models.localmodels.ApplicationConfig
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.NumberRangeResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.Route
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TaxesRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.VanSalesConfigResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.VisitsBody
import com.company.vansales.app.domain.usecases.GetPriceConditionsUseCases
import com.company.vansales.app.domain.usecases.GetPricesUseCases
import com.company.vansales.app.domain.usecases.NumberRangeUseCases
import com.company.vansales.app.domain.usecases.TaxesUseCases
import com.company.vansales.app.domain.usecases.VanSalesConfigUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.START_DAY_ACTIVITY
import com.company.vansales.app.utils.Constants.STATUS_LOADED
import com.company.vansales.app.utils.Constants.USER_BLOCKED
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.view.entities.GetPriceConditionsViewState
import com.company.vansales.app.view.entities.GetPricesViewState
import com.company.vansales.app.view.entities.NumberRangeViewState
import com.company.vansales.app.view.entities.VanSalesConfigViewState
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.RoutesViewModel
import com.company.vansales.app.viewmodel.StartDayViewModel
import com.company.vansales.databinding.FragmentSyncBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SyncFragment : Fragment() , NumberRangeUseCases.View , VanSalesConfigUseCases.View,
    TaxesUseCases.View , GetPricesUseCases.View , GetPriceConditionsUseCases.View{

    private lateinit var binding: FragmentSyncBinding
    private val startDayViewModel: StartDayViewModel by viewModels()
    private val routesViewModel: RoutesViewModel by viewModels()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    private lateinit var body: BaseBody
    private var visitsBody: VisitsBody? = null
    private var selectedRoutes: List<Route>? = null
    var driverNumber: String? = null
    var lastProgressIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sync, container, false
        )
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(application, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initButtons()
        callBacks()
    }

    private var openedActivity: String = ""
    private fun initView() {
        openedActivity = arguments?.getString("activity").toString()

        driverNumber = AppUtils.getDriver(application)
        selectedRoutes = StartDayActivity.instance?.getSelectedRoutesBody()

        body = BaseBody(
            applicationSettingsViewModel.sharedPreferences.getSalesOrg().toString(),
            applicationSettingsViewModel.sharedPreferences.getDistChannel().toString(),
            "",
            driverNumber!!
        )
        visitsBody = VisitsBody(body, selectedRoutes)
        if (openedActivity == START_DAY_ACTIVITY) {
            routesViewModel.upsert((activity as StartDayActivity).getSelectedRoutes()!!)
        } else {
/*            body.cdate = applicationSettingsViewModel.sharedPreferences.getCurrentSyncDate()
            body.tim = applicationSettingsViewModel.sharedPreferences.getCurrentSyncTime()*/
            body.timestamp = applicationSettingsViewModel.sharedPreferences.getCurrentTimeStamp()
        }
        triggerStartDayScenario()
        initStartDayObservers()

        applicationSettingsViewModel.sharedPreferences.getSalesOrg()?.let {
            NumberRangeRequestModel(applicationSettingsViewModel.sharedPreferences.getSelectedRoute()?:"" ,
                it
            )
                ?.let { startDayViewModel.getNumberRange(it) }
        }
        startDayViewModel.viewStateNumberRange.observe(viewLifecycleOwner){ viewState->
            when(viewState){
                is NumberRangeViewState.Data ->{
                    renderNumberRange(viewState.data[0])
                }
                is NumberRangeViewState.Loading -> {

                }
                is NumberRangeViewState.NetworkFailure -> {

                }
            }
        }
        startDayViewModel.getVanSalesConfig(VanSalesConfigRequestModel())
        startDayViewModel.viewStateVanSalesConfig.observe(viewLifecycleOwner){ viewState ->
            when(viewState){
                is VanSalesConfigViewState.Data -> {
                    if(viewState.data.isNotEmpty()) {
                        renderVanSalesConfigSuccess(viewState.data)
                    }
                }
                is VanSalesConfigViewState.Loading -> {

                }
                is VanSalesConfigViewState.NetworkFailure -> {

                }
            }
        }
        applicationSettingsViewModel.sharedPreferences.setCurrentSyncDate(AppUtils.getStringCurrentDateDifferentFormat())
        applicationSettingsViewModel.sharedPreferences.setCurrentSyncTime(AppUtils.getStringCurrentTimeDifferentFormat())

    }

    private fun initButtons() {
/*        binding.syncingDetailsTV.setOnClickListener {
            if (binding.syncingDetailsEL.isExpanded) {
                binding.syncingDetailsEL.collapse()
            } else {
                binding.syncingDetailsEL.expand()
            }
        }*/
    }

    private fun callBacks() {
        startDayViewModel.progressMLD.observe(viewLifecycleOwner) { progress ->
            if (!progress.isNullOrEmpty()) {
                val lstValues: List<String> = progress.split(",")
                animateProgress(lstValues[0].toInt(), lstValues[1].toInt() - 1)
            }
        }
    }

    private fun proceedStartDay() {
        driverNumber?.let {
            startDayViewModel.customerRepository.getDriverMutableLiveData(it)
                .observe(viewLifecycleOwner) { driver ->
                    if (driver != null) {
                        if(driver.paymentTerm == USER_BLOCKED){
                            val blockedUserFragment = BlockedUserDialog(requireContext(),false)
                            blockedUserFragment.show();
                        }else {
                            body.salesOrg = driver.salesOrg
                            body.distChannel = driver.distChannel
                            body.customer = driver.customer
                            body.driver = driver.customer
                            visitsBody = VisitsBody(body, selectedRoutes)
                            startDayViewModel.deleteAllTaxes();
                            startDayViewModel.getMaterials(true, body, visitsBody)
                            startDayViewModel.getTaxes(
                                TaxesRequestModel(
                                    driver.salesOrg,
                                    driver.distChannel, driver.customer, driver.customer
                                )
                            )
                        }
                    } else {
                        visitsBody = VisitsBody(body, selectedRoutes)
                        startDayViewModel.getMaterials(true, body, visitsBody)
                    }
                }
        }
    }

    private fun initStartDayObservers() {

        lifecycleScope.launch {
            startDayViewModel.customerStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(binding.syncingCustomerStatusIV, binding.syncingCustomerPB)
                    proceedStartDay()
                } else {
                    body.cdate = null
                    body.tim = null
                    startDayViewModel.getMaterials(true, body, visitsBody)
                    retryView(binding.syncingCustomerStatusIV, binding.syncingCustomerPB)
                    binding.syncingCustomerStatusIV.setOnClickListener { retry(1) }
                    somethingWentWrongView()
                }
            }

            startDayViewModel.materialsStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(binding.syncingMaterialsStatusIV, binding.syncingMaterialsPB)
                    isDayStartedSuccessfully()
                } else {
                    body.cdate = null
                    body.tim = null

                    startDayViewModel.getMaterialsUnit(true, body, visitsBody)
                    retryView(binding.syncingMaterialsStatusIV, binding.syncingMaterialsPB)
                    binding.syncingMaterialsStatusIV.setOnClickListener { retry(2) }
                    somethingWentWrongView()
                }
            }

            startDayViewModel.materialsUnitStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(
                        binding.syncingMaterialsUnitStatusIV,
                        binding.syncingMaterialsUnitPB
                    )
                    isDayStartedSuccessfully()
                } else {
                    body.cdate = null
                    body.tim = null

                    startDayViewModel.getTruckContent(true, body, visitsBody)
                    retryView(
                        binding.syncingMaterialsUnitStatusIV,
                        binding.syncingMaterialsUnitPB
                    )
                    binding.syncingMaterialsUnitStatusIV.setOnClickListener { retry(3) }
                    somethingWentWrongView()
                }
            }

            startDayViewModel.truckContentStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(
                        binding.syncingTruckContentStatusIV,
                        binding.syncingTruckContentPB
                    )
                    isDayStartedSuccessfully()
                } else {
                    body.cdate = null
                    body.tim = null
                    startDayViewModel.getVisits(true, body, visitsBody)
                    retryView(
                        binding.syncingTruckContentStatusIV,
                        binding.syncingTruckContentPB
                    )
                    binding.syncingTruckContentStatusIV.setOnClickListener { retry(4) }
                    somethingWentWrongView()

                }

            }

            startDayViewModel.visitsStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(binding.syncingVisitsStatusIV, binding.syncingVisitsPB)
                    isDayStartedSuccessfully()
                } else {
/*                    body.cdate = applicationSettingsViewModel.sharedPreferences.getCurrentSyncDate()
                    body.tim = applicationSettingsViewModel.sharedPreferences.getCurrentSyncTime()*/
                    body.timestamp = applicationSettingsViewModel.sharedPreferences.getCurrentTimeStamp()
                    retryView(binding.syncingVisitsStatusIV, binding.syncingVisitsPB)
                    startDayViewModel.getPrices(true, body)
//                    startDayViewModel.getPrices(true, body)
                    binding.syncingVisitsStatusIV.setOnClickListener { retry(5) }
                    somethingWentWrongView()

                }
            }

            startDayViewModel.pricesStatusLiveData.observe(
                viewLifecycleOwner
            ) { status ->
                if (status == STATUS_LOADED) {
                    successView(binding.syncingPricesStatusIV, binding.syncingPricesPB)
                    isDayStartedSuccessfully()
                } else {
        /*            body.cdate = applicationSettingsViewModel.sharedPreferences.getCurrentSyncDate()
                    body.tim = applicationSettingsViewModel.sharedPreferences.getCurrentSyncTime()*/
                    body.timestamp = applicationSettingsViewModel.sharedPreferences.getCurrentTimeStamp()
                    startDayViewModel.getConditions(body)
                    retryView(binding.syncingPricesStatusIV, binding.syncingPricesPB)
                    binding.syncingPricesStatusIV.setOnClickListener { retry(6) }
                    somethingWentWrongView()
                }
            }

            startDayViewModel.conditionsStatusLiveData.observe(viewLifecycleOwner) { status ->
                if (status == STATUS_LOADED) {
                    successView(binding.syncingConditionsStatusIV, binding.syncingConditionsPB)
                    isDayStartedSuccessfully()
                } else {
       /*             body.cdate = applicationSettingsViewModel.sharedPreferences.getCurrentSyncDate()
                    body.tim = applicationSettingsViewModel.sharedPreferences.getCurrentSyncTime()*/
                    body.timestamp = applicationSettingsViewModel.sharedPreferences.getCurrentTimeStamp()
                    retryView(binding.syncingConditionsStatusIV, binding.syncingConditionsPB)
                    binding.syncingConditionsStatusIV.setOnClickListener { retry(7) }
                    somethingWentWrongView()
                }
            }
        }

        startDayViewModel.viewStatePrices.observe(viewLifecycleOwner){ viewState ->
            when (viewState) {
                is GetPricesViewState.Data -> {

                }
                is GetPricesViewState.Loading -> Log.d("getRenderAppr", "Loading")
                is GetPricesViewState.NetworkFailure -> Log.d(
                    "getRenderAppr",
                    "FAIL"
                )
                is GetPricesViewState.LoadingMessage -> renderPricesLoadedValue(viewState.data)
            }

        }
        startDayViewModel.viewStateConditions.observe(viewLifecycleOwner){ viewState ->
            when (viewState) {
                is GetPriceConditionsViewState.Data -> {

                }
                is GetPriceConditionsViewState.Loading -> Log.d("getRenderAppr", "Loading")
                is GetPriceConditionsViewState.NetworkFailure -> Log.d(
                    "getRenderAppr",
                    "FAIL"
                )
                is GetPriceConditionsViewState.LoadingMessage -> renderConditionLoadedValue(viewState.data)
            }

        }

    }


    private fun successView(imageView: ImageView, progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
        imageView.visibility = View.VISIBLE
        imageView.setBackgroundResource(R.drawable.ic_blue_done)
    }

    private fun retryView(imageView: ImageView, progressBar: ProgressBar) {
        progressBar.visibility = View.INVISIBLE
        imageView.visibility = View.VISIBLE
        imageView.setBackgroundResource(R.drawable.ic_retry)

    }

    private fun loadingView(imageView: ImageView, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE
        imageView.visibility = View.INVISIBLE
    }

    private fun loaded() {
        applicationSettingsViewModel.updateDayStatus(true)
        binding.syncingDoneBTN.visibility = View.VISIBLE
        binding.syncingStatusTV.visibility = View.INVISIBLE
//        binding.loadingSK.indicator.stop()
        binding.syncingDoneBTN.setOnClickListener {
            if (openedActivity == START_DAY_ACTIVITY) {
                applicationSettingsViewModel.sharedPreferences.setDayStarted(true)
                startActivity(Intent(requireActivity(), VisitsActivity::class.java))
                requireActivity().finishAffinity()
//                finishAffinity()
            } else
                fragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }

    private fun somethingWentWrongView() {
        AppUtils.showMessage(
            this.requireContext(),
            this.getString(R.string.something_went_wrong)
        )
     /*   if (!binding.syncingDetailsEL.isExpanded) {
            binding.syncingDetailsEL.expand()
        }*/
    }

    fun retry(id: Int) {
        if (isCustomersLoaded()) {
            when (id) {
                1 -> {
                    triggerStartDayScenario()
                }
                2 -> {
                    loadingView(binding.syncingMaterialsStatusIV, binding.syncingMaterialsPB)
                    startDayViewModel.getMaterials(false, body, visitsBody)
                }
                3 -> {
                    loadingView(
                        binding.syncingMaterialsUnitStatusIV,
                        binding.syncingMaterialsUnitPB
                    )
                    startDayViewModel.getMaterialsUnit(false, body, visitsBody)
                }
                4 -> {
                    loadingView(
                        binding.syncingTruckContentStatusIV,
                        binding.syncingTruckContentPB
                    )
                    startDayViewModel.getTruckContent(false, body, visitsBody)
                }

                5 -> {
                    loadingView(binding.syncingVisitsStatusIV, binding.syncingVisitsPB)
                    startDayViewModel.getVisits(false, body, visitsBody)

                }

                6 -> {
                    loadingView(binding.syncingPricesStatusIV, binding.syncingPricesPB)
                    startDayViewModel.getPrices(false, body )
//                    startDayViewModel.getPrices(false, body)
                }
                7 -> {
                    loadingView(binding.syncingConditionsStatusIV, binding.syncingConditionsPB)
                    startDayViewModel.getConditions(body)

                }
            }
        } else {
            triggerStartDayScenario()
        }

    }


    private fun isDayStartedSuccessfully() {
        if (startDayViewModel.customerStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.materialsStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.materialsUnitStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.truckContentStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.visitsStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.pricesStatusMutableLiveData.value == STATUS_LOADED &&
            startDayViewModel.conditionsStatusMutableLiveData.value == STATUS_LOADED
        ) {
            loaded()
        }
    }


    private fun animateProgress(currentProgress: Int, allProgress: Int) {
        if (currentProgress == allProgress) {
            binding.progressBarHorizantal.visibility = View.GONE
        } else {
            binding.progressBarHorizantal.visibility = View.VISIBLE
        }

        val progressPercent =
            try {
                (currentProgress * 100) / allProgress
            } catch (e: Exception) {
                0
            }
        val animation =
            ObjectAnimator.ofInt(
                binding.progressBarHorizantal,
                "progress",
                lastProgressIndex,
                progressPercent
            )
        animation.duration = 100 // 0.1 second
        animation.interpolator = LinearInterpolator()
        animation.start()
        lastProgressIndex = try {
            (currentProgress * 100) / allProgress
        } catch (e: Exception) {
            0
        }
    }


    private fun triggerStartDayScenario() {
        loadingView(binding.syncingCustomerStatusIV, binding.syncingCustomerPB)
        loadingView(binding.syncingMaterialsStatusIV, binding.syncingMaterialsPB)
        loadingView(binding.syncingMaterialsUnitStatusIV, binding.syncingMaterialsUnitPB)
        loadingView(binding.syncingTruckContentStatusIV, binding.syncingTruckContentPB)
        loadingView(binding.syncingVisitsStatusIV, binding.syncingVisitsPB)
        loadingView(binding.syncingPricesStatusIV, binding.syncingPricesPB)
        loadingView(binding.syncingConditionsStatusIV, binding.syncingConditionsPB)
        startDayViewModel.triggerStartDayScenario(this.requireActivity(), body, openedActivity)
    }

    private fun isCustomersLoaded(): Boolean {
        return startDayViewModel.customerStatusMutableLiveData.value == STATUS_LOADED
    }

    override fun renderNumberRange(data: NumberRangeResponseModel) {
        data.lastVlaue?.let { lastValue -> data.route?.let { route ->
            var i = 0
            while (i < lastValue.length && lastValue[i] == '0') {
                i++
            }
            val output = lastValue.substring(i)
            InvoiceNumber(output, route) } }?.let { invoice ->
            startDayViewModel.upsert(invoice)
            }
    }

    override fun renderVanSalesConfigSuccess(data: List<VanSalesConfigResponseModel>) {
        var vanSalesConfig = ArrayList<ApplicationConfig>()
        if(data.isNotEmpty()) {
            data.forEach {
                vanSalesConfig.add(ApplicationConfig(0, it.salesOrg, it.appParamter, it.value))
            }
            startDayViewModel.upsertAppSettings(vanSalesConfig)
        }
    }

    override fun renderTaxes(data: List<Taxes>) {

    }

    override fun renderPricesLoadedValue(data: String) {
        binding.syncingPricesValue.text = data
    }

    override fun renderConditionLoadedValue(data: String) {
        binding.syncingConditionValue.text = data
    }

    override fun renderLoading(show: Boolean) {
    }

    override fun renderNetworkFailure() {
    }

    override fun renderVanSalesConfigError() {
    }

    override fun renderNumberRangeFailure() {
    }
}