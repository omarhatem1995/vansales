package com.company.vansales.app.activity

import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Truck
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.domain.usecases.BillToBillCheckUseCases
import com.company.vansales.app.domain.usecases.GetTruckContentsUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.PAYMENT_TERM_CASH
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.utils.ViewUtils
import com.company.vansales.app.view.entities.DriverDataViewState
import com.company.vansales.app.view.entities.GetTruckContentsViewState
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.BatchScanViewModel
import com.company.vansales.app.viewmodel.StartDayViewModel
import com.company.vansales.app.viewmodel.VisitsViewModel
import com.company.vansales.databinding.ActivityActionsBinding
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ActionsActivity : AppCompatActivity() , BillToBillCheckUseCases.View ,
GetTruckContentsUseCases.View{

    private lateinit var binding: ActivityActionsBinding
    lateinit var visitRepository: VisitsRepository
    private val visitViewModel: VisitsViewModel by viewModels()
    private val batchScanViewModel: BatchScanViewModel by viewModels()
    val startDayViewModel: StartDayViewModel by viewModels()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    private var isCash = false
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
            LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_actions)

        initView()
        initButtons()
        callBacks()
    }

    private fun updateCustomer() {
        applicationSettingsViewModel.driverData(applicationSettingsViewModel.currentCustomerForUpdate)
        applicationSettingsViewModel.viewStateDriverData.observe(this) { viewState ->
            when (viewState) {
                is DriverDataViewState.DriverDataSuccess -> {
                    startDayViewModel.updateCustomer(
                        Customer(
                            salesOrg = viewState.data.data[0].salesOrg,
                            distChannel = viewState.data.data[0].distChannel,
                            customer = applicationSettingsViewModel.currentCustomerForUpdate.customer,
                            address = viewState.data.data[0].address,
                            region = viewState.data.data[0].region,
                            currency = viewState.data.data[0].currency,
                            name1 = viewState.data.data[0].name1,
                            customizeField = viewState.data.data[0].customizeField,
                            printOption = viewState.data.data[0].printOption,
                            paymentTerm = viewState.data.data[0].paymentTerm,
                            latitude = viewState.data.data[0].latitude.toDouble(),
                            nameArabic = viewState.data.data[0].nameArabic,
                            longitude = viewState.data.data[0].longitude.toDouble(),
                            city = viewState.data.data[0].city,
                            credit = viewState.data.data[0].credit.toDouble(),
                            regionArabic = viewState.data.data[0].regionArabic,
                            addressArabic = viewState.data.data[0].addressArabic,
                            mobile = viewState.data.data[0].mobile,
                            telephone = viewState.data.data[0].telephone,
                            yexcTax = null,
                            yvatTax = null,
                            operation = null
                        )
                    )
                }
                is DriverDataViewState.DriverDataFailure -> {
                }
                is DriverDataViewState.Loading -> {
                }
                is DriverDataViewState.NetworkFailure -> {
                }
            }
        }

    }

    private fun initButtons() {
        binding.backActionsTV.setOnClickListener {
            handleBackPressed()
        }
        binding.actionCreditTV.setOnClickListener {
            if (isCash)
                toCashMaterialSelection()
            else
                toCreditMaterialSelection()
        }
        /*binding.actionBillToBillV.setOnClickListener {
            toBillToBillMaterialSelection()
        }
        binding.actionOrderTV.setOnClickListener { toOrderSelection() }
        binding.actionReturnTV.setOnClickListener { toReturnMaterialSelection() }
        binding.actionFreeTV.setOnClickListener { toFreeMaterialSelection() }
        binding.actionExchangeTV.setOnClickListener { toExchangeMaterialSelection() }
        binding.actionPaymentTV.setOnClickListener { toPaymentMaterialSelection() }
        */
//        binding.actionDocumentTV.setOnClickListener { toDocument() }
    }

    private fun handleBackPressed() {
        getUpdateLocation()
        batchScanViewModel.checkCreatedDocument(customerNo).observe(this) { salesDocHeader ->
            if (salesDocHeader != null) {
                var iSalesDocHeader = salesDocHeader.salesOrg?.let { it1 ->
                    salesDocHeader.distChannel?.let { it2 ->
                        salesDocHeader.docType?.let { it3 ->
                            salesDocHeader.paymentTerm?.let { it4 ->
                                salesDocHeader.docStatus?.let { it5 ->
                                    salesDocHeader.visitId?.let { it6 ->
                                        salesDocHeader.visitItem?.let { it7 ->
                                            ISalesDocHeader(
                                                salesorg = it1,
                                                distchannel = it2,
                                                division = salesDocHeader.distChannel!!,
                                                plant = salesDocHeader.plant,
                                                location = salesDocHeader.location,
                                                route = "",
                                                driver = salesDocHeader.driver,
                                                customer = salesDocHeader.customer,
                                                doctype = it3,
                                                exdoc = "",
                                                creationdate = Date(),
                                                creationtiem = Date(),
                                                totalvalue = "0.0".toBigDecimal(),
                                                discountValue = "0.0".toBigDecimal(),
                                                paymentterm = it4,
                                                docstatus = it5,
                                                sapDoc = "",
                                                customizeheader = "",
                                                visitid = it6,
                                                visititem = it7
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (iSalesDocHeader != null) {
                    addVisitAsSuccess(iSalesDocHeader)
                }
            } else
                doYouWantToFinishTheVisitDialog()
        }
    }

    private fun initView() {

        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber!!.text = "Version : $versionName"
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        visitViewModel.getTruckContents()

//        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        visitRepository = VisitsRepository(application)
//        ViewUtils.disableView(binding.actionApprovalsTV)
//        ViewUtils.disableView(binding.actionPaymentTV)

        /*if (isCash) {
           val exchangeVisibility = startDayViewModel.applicationSettingsRepository.getAppConfigValueByAppParamter("ACTIV_EXCHANGE_CASH")
            if (exchangeVisibility == "N")
                binding.actionExchangeTV.visibility = View.GONE
            val focVisibility =
                startDayViewModel.applicationSettingsRepository.getAppConfigValueByAppParamter("ACTIVATE_FOC_CASH")
            if (focVisibility == "N")
                binding.actionFreeTV.visibility = View.GONE

            val returnVisibility =
                startDayViewModel.applicationSettingsRepository.getAppConfigValueByAppParamter("ACTIVATE_RETURN_CASH")
            if (returnVisibility == "N")
                binding.actionReturnTV.visibility = View.GONE

        }else{
            val exchangeVisibility = startDayViewModel.applicationSettingsRepository.getAppConfigValueByAppParamter("ACTIV_EXCHANGE_CREDI")
            if (exchangeVisibility == "N")
                binding.actionExchangeTV.visibility = View.GONE
        }*/
    }

    var customerNo = ""
    var customerName = ""

    private fun callBacks() {
        visitViewModel.getCurrentActiveVisitLiveData().observe(
            this
        ) { visit ->
            if (visit != null) {
                val currentCustomer = getCostumer(
                    visit.customerNo!!,
                    visit.salesOrg!!,
                    visit.dist_channel!!
                )
                customerName = visit.customerName.toString()
                customerNo = visit.customerNo!!
                binding.customerInitializeTV.text = visit.customerName?.first().toString()
                binding.customerNameTV.text = AppUtils.getLanguageDependencyString(
                    visit.customerName,
                    visit.customerNameArabic,
                    this.application
                )
                applicationSettingsViewModel.getCurrentCustomer(
                    DriverDataRequestModel(
                        customer = visit.customerNo!!,
                        salesOrg = visit.salesOrg!!,
                        distChannel = visit.dist_channel!!,
                        division = visit.division!!,
                        driver = AppUtils.getDriver(application)
                    )
                )
                updateCustomer()
              /*  binding.actionApprovalsTV.setOnClickListener {
                    toApprovals(
                        OpenInvoicesRequestModel(
                            customer = visit.customerNo!!,
                            salesOrg = visit.salesOrg!!,
                            distChannel = visit.dist_channel!!,
                            division = visit.division!!
                        )
                    )
                }*/

              /*  binding.actionCollectionsTV.setOnClickListener {
                    toCollections(
                        OpenInvoicesRequestModel(
                            customer = visit.customerNo!!,
                            salesOrg = visit.salesOrg!!,
                            distChannel = visit.dist_channel!!,
                            division = visit.division!!
                        )
                    )
                }*/

                val paymentTerm = currentCustomer?.paymentTerm
                binding.customerTypePhoneTV.text = currentCustomer?.telephone + " | " + paymentTerm
                binding.customerAddressTV.text = currentCustomer?.address
                if (visitViewModel.getRoutesList().size > 1) {
                    binding.routeSelectionIV.visibility = View.VISIBLE
                    binding.routeSelectionIV.text = visitViewModel.getCurrentActiveVisit()!!.route
                    binding.routeSelectionIV.setOnClickListener { initSortByRoute() }
                }
                if (paymentTerm == PAYMENT_TERM_CASH) {
                    isCash = true
                    binding.actionCreditTV.text = resources.getString(R.string.cash)
//                    binding.actionBillToBillV.visibility = View.GONE
                    initView()
                }else{
//                    binding.actionExchangeTV.visibility = View.GONE
//                    binding.actionCollectionsTV.visibility = View.GONE
                }
            } else {
                AppUtils.proceedToActivity(this, this, VisitsActivity())
                this.finish()
            }
        }
        visitViewModel.viewStateTruckContents.observe(this) { viewState ->
            when (viewState) {
                is GetTruckContentsViewState.Loading -> {
                    renderLoading(viewState.show)
                    disableAll()
                }
                is GetTruckContentsViewState.NetworkFailure -> {
                    enableAll()
                }
                is GetTruckContentsViewState.Data -> {
                    renderTruckContents(viewState.data.data)
                }
            }
        }
    }


    private fun addVisitAsSuccess(iSalesDocHeader: ISalesDocHeader) {
        updateEndingVisit(null)
        batchScanViewModel.finishVisitSuccessfully(
            iSalesDocHeader
        )
        val intent = Intent(this, VisitsActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun doYouWantToFinishTheVisitDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    toVisitsWithAFailedVisit()
                    dialog.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.finish_visit))
            .setPositiveButton(resources.getString(R.string.yes), dialogClickListener)
            .setNegativeButton(resources.getString(R.string.no), dialogClickListener).show()

        builder.setCancelable(false)

    }

    private fun updateEndingVisit(failedReason : String?) {
        visitViewModel.updateEndVisitData(visitViewModel.getCurrentActiveVisit()?.visitListID?:"",
            visitViewModel.getCurrentActiveVisit()?.visitItemNo?:0,AppUtils.getFormatForEndDay(),
            AppUtils.getFormatForEndDay(),0.0,
            0.0,failedReason)
    }
//    private var acurateLocation: AcurateLocation? = null

    private fun toVisitsWithAFailedVisit() {
        if (visitRepository.getCurrentActiveVisit() != null) {
            Log.d("currentVisit", "not null")
          /*  if (this.isLocationEnabled()) {
                if (acurateLocation?.getLatLng() == null) {
                    Log.d("currentVisit", "location is  null")
                    this.displayToast(getString(R.string.wait_loc))
                } else {*/
                    /*if (isInsideLocation(
                            currLoc = acurateLocation?.getLatLng()!!,
                            destinationLoc = LatLng(acurateLocation?.latitude?:0.0,
                                acurateLocation?.longitude?:0.0)
                        )
                    ) {*/

                       /* ViewUtils.showVisitFailureReasonDialog(
                            true,
                            visitRepository,
                            visitRepository.getCurrentActiveVisit()!!,
                            this,
                            0.0,
                            0.0
                        )*/
//                        updateEndingVisit()
//                    }
               /* }
            }else{
                Toast.makeText(this,getString(R.string.please_open_gps),Toast.LENGTH_LONG).show()
            }*/
        } else {
            super.onBackPressed()
        }
    }

    private fun toCashMaterialSelection() {
        val intent = Intent(applicationContext, CashMaterialSelectionActivity::class.java)
        intent.putExtra("isCash", isCash)
        intent.putExtra("isFree", false)
        intent.putExtra("customerName", customerName)
        intent.putExtra("customerNumber", customerNo)
        startActivity(intent)
    }

    private fun toCreditMaterialSelection() {
        val intent = Intent(applicationContext, CreditMaterialSelectionActivity::class.java)
        intent.putExtra("customerName", customerName)
        intent.putExtra("customerNumber", customerNo)
        startActivity(intent)
    }

//    private fun toBillToBillMaterialSelection() {
//        visitViewModel.checkBillToBill(BillToBillCheckRequestModel(customerNo,DocumentsConstants.Bill_to_Bill))
//        visitViewModel.viewStateBillToBillCheck.observe(this){viewState ->
//            when(viewState){
//                is BillToBillCheckViewState.Data -> {
//                    renderBillToBillCheck(viewState.data)
//                }
//                is BillToBillCheckViewState.Loading -> {
//
//                }
//                is BillToBillCheckViewState.NetworkFailure -> {
//                    renderBillToBillCheckError()
//                }
//            }
//        }
//    }

//    private fun toReturnMaterialSelection() {
//        startActivity(Intent(applicationContext, ReturnMaterialSelectionActivity::class.java))
//    }

//    private fun toFreeMaterialSelection() {
//        val intent = Intent(applicationContext, FreeMaterialSelectionActivity::class.java)
//        intent.putExtra("isFree", true)
//        intent.putExtra("customerName", customerName)
//        intent.putExtra("customerNumber", customerNo)
//        startActivity(intent)
//    }

//    private fun toExchangeMaterialSelection() {
//        val intent = Intent(applicationContext, ExchangeMaterialSelectionActivity::class.java)
//        intent.putExtra("isCash", isCash)
//        intent.putExtra("isFree", false)
//        intent.putExtra("customerName", customerName)
//        intent.putExtra("customerNumber", customerNo)
//        this.startActivity(intent)
//    }

    private fun toPaymentMaterialSelection() {

    }

//    private fun toOrderSelection() {
//        val intent = Intent(applicationContext, OrderMaterialSelectionActivity()::class.java)
//        startActivity(intent)
//    }


//    private fun toApprovals(openInvoicesRequestModel: OpenInvoicesRequestModel) {
//        val intent = Intent(applicationContext, ApprovalsActivity()::class.java)
//        intent.putExtra("customer", openInvoicesRequestModel.customer)
//        intent.putExtra("customerName", customerName)
//        intent.putExtra("customerNumber", customerNo)
//        startActivity(intent)
//    }

//    private fun toCollections(openInvoicesRequestModel: OpenInvoicesRequestModel) {
//        val intent = Intent(applicationContext, CollectionActivity()::class.java)
//        intent.putExtra("customer", openInvoicesRequestModel)
//        intent.putExtra("number", customerNo)
//        intent.putExtra("name", customerName)
//        startActivity(intent)
//    }

//    private fun toDocument() {
//        startActivity(Intent(applicationContext, CustomerDocumentsActivity::class.java))
//    }

    private fun getCostumer(costumerNo: String, salesOrg: String, distChannel: String): Customer? {
        return visitRepository.getCustomerById(costumerNo, salesOrg, distChannel)
    }


    private fun initSortByRoute() {
        val routesPopUpMenu = PopupMenu(this, binding.routeSelectionIV)
        val routes = visitViewModel.getRoutesList()
        for (i in routes.indices) {
            if (routes[i] != "") {
                routesPopUpMenu.menu.add(Menu.NONE, i, Menu.NONE, routes[i])
            }
        }
        routesPopUpMenu.show()
        routesPopUpMenu.setOnMenuItemClickListener { route ->
            onRouteChange(
                this.application,
                route.toString(),
                visitViewModel.getCurrentActiveVisit()!!.customerNo!!,
                visitViewModel.getCurrentActiveVisit()!!.salesOrg!!,
                visitViewModel.getCurrentActiveVisit()!!.dist_channel!!
            )
            true
        }
    }

    private fun onRouteChange(
        application: Application,
        route: String,
        customerNo: String,
        salesOrg: String,
        distChannel: String
    ) {
        visitViewModel.onRouteChange(application, route, customerNo, salesOrg, distChannel)
    }


    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onRestart() {
        super.onRestart()
        initView()
    }

    override fun onPause() {
        super.onPause()
        this.initView()
    }
    var billToBillMax = 7
    override fun renderBillToBillCheck(data: BillToBillCheckResponseModel) {
        /*billToBillMax = visitViewModel.getAppConfig().toInt()
        if(data.data.orders.count() >= billToBillMax){
//            Toast.makeText(this,"You have reached the max value for bill to bill",Toast.LENGTH_LONG).show()
        }else{
            startActivity(Intent(applicationContext, BillToBillMaterialSelectionActivity::class.java))
        }*/
    }

    override fun renderTruckContents(data: Truck) {
        enableAll()
    }

    private fun enableAll() {
        binding.backActionsTV.isEnabled = true
//        binding.actionApprovalsTV.isEnabled = true
//        binding.actionDocumentTV.isEnabled = true
        binding.actionCreditTV.isEnabled = true
//        binding.actionBillToBillV.isEnabled = true
//        binding.actionExchangeTV.isEnabled = true
//        binding.actionCollectionsTV.isEnabled = true
//        binding.actionFreeTV.isEnabled = true
//        binding.actionOrderTV.isEnabled = true
//        binding.actionPaymentTV.isEnabled = true
    }
    private fun disableAll() {
        binding.backActionsTV.isEnabled = false
//        binding.actionApprovalsTV.isEnabled = false
//        binding.actionDocumentTV.isEnabled = false
        binding.actionCreditTV.isEnabled = false
//        binding.actionBillToBillV.isEnabled = false
//        binding.actionExchangeTV.isEnabled = false
//        binding.actionCollectionsTV.isEnabled = false
//        binding.actionFreeTV.isEnabled = false
//        binding.actionOrderTV.isEnabled = false
//        binding.actionPaymentTV.isEnabled = false
    }

    override fun renderLoading(show: Boolean) {

    }

    override fun renderNetworkFailure() {
        Toast.makeText(this,"Please check internet connection to activate bill to bill",Toast.LENGTH_LONG).show()
    }

    override fun renderBillToBillCheckError() {
        Toast.makeText(this,"Please check internet connection to activate bill to bill",Toast.LENGTH_LONG).show()
    }

/*    private fun isInsideLocation(currLoc: LatLng, destinationLoc: LatLng): Boolean {
        return !PolyUtil.isLocationOnPath(
            currLoc,
            mutableListOf(destinationLoc),
            true,
            Constants.TOLERANCE
        )
    }*/
    private fun getUpdateLocation() {
/*        UpdateLocationService.locationResult.observe(this) {
            if (it.gpsEnable)
                acurateLocation = it.acurateLocation
        }*/
    }
}