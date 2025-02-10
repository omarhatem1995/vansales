package com.company.vansales.app.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.telecom.ConnectionService
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.datamodel.models.localmodels.ShowAddModel
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.BillToBillCheckResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.DataPayers
import com.company.vansales.app.datamodel.models.mastermodels.DriverDataRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PayersRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PayersResponseModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.mastermodels.RequestStatusRequestModel
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.domain.usecases.CreateDocumentUseCases
import com.company.vansales.app.domain.usecases.PayersUseCases
import com.company.vansales.app.domain.usecases.RequestStatusUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.AppUtils.getStringCurrentDate
import com.company.vansales.app.utils.AppUtils.getStringCurrentTime
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.utils.enums.DocumentsEnum
import com.company.vansales.app.utils.enums.TypeVisitEnum
import com.company.vansales.app.view.adapter.MaterialsListAdapter
import com.company.vansales.app.view.adapter.ViewPagerAdapter
import com.company.vansales.app.view.entities.CreateDocumentViewState
import com.company.vansales.app.view.entities.DriverDataViewState
import com.company.vansales.app.view.entities.PayersViewState
import com.company.vansales.app.view.entities.RequestStatusViewState
import com.company.vansales.app.view.fragments.InvoiceDetailsDialog
import com.company.vansales.app.view.fragments.SelectedMaterialsListFragment
import com.company.vansales.app.view.fragments.ShowAddDialog
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.FillUpViewModel
import com.company.vansales.app.viewmodel.MaterialsViewModel
import com.company.vansales.app.viewmodel.RequestStatusViewModel
import com.company.vansales.app.viewmodel.SalesDocViewModel
import com.company.vansales.app.viewmodel.StartDayViewModel
import com.company.vansales.databinding.ActivityMaterialSelectionBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList


open class MaterialSelectionActivity : AppCompatActivity(), CreateDocumentUseCases.View,
    PayersUseCases.View , RequestStatusUseCases.View {

    lateinit var binding: ActivityMaterialSelectionBinding
    val materialsVM: MaterialsViewModel by viewModels()
    val fillUpViewModel: FillUpViewModel by viewModels()
    val salesDocViewModel: SalesDocViewModel by viewModels()
    val startDayViewModel: StartDayViewModel by viewModels()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()
    private val requestStatusViewModel: RequestStatusViewModel by viewModels()
    private var searchEditText: EditText? = null
    var adapterMaterialsList = ArrayList<MaterialsTruckItem>()
    var exInvoiceNumber = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mCurrentLocation: Location? = null

    var typeVisit: TypeVisitEnum? = null
    var printingBatchScanTypeEnum = BatchScanTypesEnum.CREDIT_MATERIAL

    var allPayers: MutableList<DataPayers> = mutableListOf()
    var allPayersName: MutableList<String> = mutableListOf()
    var selectedPayer = ""
    var selectedPayerID = ""

    var isCash = true
    var customerName = " "
    var customerNumber = " "

    var proceedToActivity : Activity = ActionsActivity()
    var iSalesDocHeader: ISalesDocHeader? = null
//    private var acurateLocation: AcurateLocation? = null

    companion object {
        var instance: MaterialSelectionActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_material_selection)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        binding.isRefresh = true
        initView()
        initButtons()
//        refreshCredit()
        getUpdateLocation()
    }
    private fun getUpdateLocation() {
      /*  UpdateLocationService.locationResult.observe(this) {
            if (it.gpsEnable) {
                acurateLocation = it.acurateLocation
                materialsVM.acurateLocation = it.acurateLocation
            }
        }*/
    }
    private fun initButtons() {

        binding.materialSelectionCustomerDetails.setOnClickListener {
//            showCustomerDetailsDialog()
        }

        binding.materialSelectionInvoiceDetails.setOnClickListener {
            showInvoiceDetailsDialog()
        }

        binding.refreshCredit.setOnClickListener { refreshCredit() }

        binding.materialSelectionNextButton.setOnClickListener { nextButtonAction() }

        binding.materialSelectionPreviousButton.setOnClickListener { previousButtonAction() }

        binding.materialSelectionFloatingAddItemButton.setOnClickListener { addItemDialogItem() }

        binding.materialSelectionFloatingAddFreeButton.setOnClickListener { addItemDialogFree() }

        binding.clickToAddItemTextView.setOnClickListener {
            if (binding.materialSelectionTabLayout.selectedTabPosition == 0)
                addItemDialogItem()
            else
                addItemDialogFree()
        }

        binding.materialSelectionViewPager.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP)
                binding.materialSelectionFloatingMenu.alpha = 1f
            false
        }

        binding.materialSelectionTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                showClickToAddItem()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
    fun refreshCredit() {
        if(!materialsVM.isCredit || materialsVM.isLoad || materialsVM.isTopUp) {
            binding.isLoading = true
            applicationSettingsViewModel.driverData(
                DriverDataRequestModel(
                    salesOrg = materialsVM.currentCustomer?.salesOrg!!,
                    distChannel = materialsVM.currentCustomer?.distChannel!!,
                    division = applicationSettingsViewModel.sharedPreferences.getDivision()!!,
                    driver = AppUtils.getDriver(application),
                    customer = materialsVM.currentCustomer?.customer!!
                )
            )
            var isCalled = false
            Log.d("callAPIinApp", " is Called refreshCredit")
            applicationSettingsViewModel.viewStateDriverData.observe(this) { viewState ->
                when (viewState) {
                    is DriverDataViewState.DriverDataSuccess -> {
                        Log.d("callAPIinApp", " is Called Driver Data Success")
                        if(!isCalled){
                            isCalled = true
                        startDayViewModel.updateCustomer(
                            Customer(
                                salesOrg = viewState.data.data[0].salesOrg,
                                distChannel = viewState.data.data[0].distChannel,
                                customer = materialsVM.currentCustomer?.customer!!,
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
                                operation = null,
                                )
                        )
                        binding.materialSelectionTotalCreditValueTextView.text =
                            convertToEnglish(viewState.data.data[0].credit)
                        if (materialsVM.isLoad) {
                            getRequestStatusUpdates(
                                viewState.data.data[0].credit,
                                viewState.data.data[0].customizeField.replace(" ", "")
                            )
                        }else{
                            binding.isLoading = false
                        }
                    }
                    }
                    is DriverDataViewState.DriverDataFailure -> {
                        binding.isLoading = false
                    }
                    is DriverDataViewState.Loading -> {
                        renderLoading(viewState.show)
                    }
                    is DriverDataViewState.NetworkFailure -> {
                        binding.isLoading = false
                    }
                }
            }
        }else{
            refreshPayers()
        }

    }
    private fun getRequestStatusUpdates(totalValueNormalCreditLimit:String,totalValueTopUpCreditLimit : String) {
        requestStatusViewModel.requestStatus(
            RequestStatusRequestModel(
            requestStatusViewModel.sharedPref.getSalesOrg()!!,
            requestStatusViewModel.sharedPref.getDistChannel()!!,
            requestStatusViewModel.sharedPref.getDivision()!!,
            requestStatusViewModel.sharedPref.getDriverNumber() ?: "",
            "SO"
        )
        )
        requestStatusViewModel.viewStateRequestStatus.observe(this) { viewState ->
            when (viewState) {
                is RequestStatusViewState.Data -> {
                    Log.d("totalValueNormalCreditLimit" , "$totalValueNormalCreditLimit , $totalValueTopUpCreditLimit")
                    renderRequestStatus(viewState.data ,totalValueNormalCreditLimit, totalValueTopUpCreditLimit)
                    renderLoading(false)
                }
                is RequestStatusViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is RequestStatusViewState.NetworkFailure -> {
                    renderNetworkFailure()
                }
            }
        }
    }
    private fun initView() {

        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber.text = "Version : $versionName"

   /*     val camPermission = CameraPermission(this)
        if (!camPermission.checkCameraPermission()) {
            camPermission.requestCameraPermission()
        }*/
        val isCashIntent = intent.extras
        isCashIntent?.let {
            if (isCashIntent.getBoolean("isCash") != null) {
                if(!isCashIntent.getBoolean("isFree")) {
                    isCash = isCashIntent.getBoolean("isCash")
                    customerName = isCashIntent.getString("customerName").toString()
                    customerNumber = isCashIntent.getString("customerNumber").toString()
                }else{
                    customerName = isCashIntent.getString("customerName").toString()
                    customerNumber = isCashIntent.getString("customerNumber").toString()
                }
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocation()

        materialsVM.currentDriverId = materialsVM.getDriver()?.customer
        materialsVM.exceedCreditMessage = resources.getString(R.string.credit_exceed_alert_dialog)
        materialsVM.exceedFOCMessage = resources.getString(R.string.FOC_exceed_alert_dialog)
        materialsVM.exceedSoldMessage = resources.getString(R.string.returns_more_than_sold)
        materialsVM.statusMessage.observe(this){
            if(it != null)
                Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        }
        binding.materialSelectionTabLayout.setupWithViewPager(binding.materialSelectionViewPager)
        for (i in 0..binding.materialSelectionTabLayout.tabCount) {
            val tv =
                LayoutInflater.from(applicationContext).inflate(R.layout.custom_tab_title, null)
            binding.materialSelectionTabLayout.getTabAt(i)?.customView = tv
        }
        /*if (this.isLocationEnabled()) {
            if (acurateLocation?.getLatLng() == null) {
//                this.displayToast(getString(R.string.wait_loc))
            } else {
                Log.d("getLatAndLong", "${acurateLocation?.latitude} , ${acurateLocation?.longitude}")
            }
        }*/
        if(!isCash)
            refreshPayers()

        if(materialsVM.isLoad || materialsVM.isFree || materialsVM.isExchange){
            binding.separateInvoice.visibility = View.INVISIBLE
        }
        observePayers()
        createTabs()
    }

    private fun refreshPayers() {
        materialsVM.getPayers(
            PayersRequestModel(
                applicationSettingsViewModel.sharedPreferences.getSalesOrg().toString(),
                applicationSettingsViewModel.sharedPreferences.getDistChannel().toString(),
                applicationSettingsViewModel.sharedPreferences.getDivision()!!,
                customerNumber
            )
        )
        observePayers()
    }

    private fun observePayers() {
        materialsVM.viewStateCreatePayer.observe(this) { viewState ->
            when (viewState) {
                is PayersViewState.Data -> {
                    renderPayersSuccess(viewState.data)
                }
                is PayersViewState.Loading -> {

                }
                is PayersViewState.NetworkFailure -> {
                    renderPayersFailure()
                }
            }
        }
    }

    internal open fun addItemDialogItem() {
        adapterMaterialsList = materialsVM.onTruckMaterialsList
        materialsVM.currentMaterialList = "Item"
        showAddItem(
            "item",
            resources.getString(R.string.add_item),
            R.drawable.ic_add_shopping_cart,
            resources.getString(R.string.total_foc),
            AppUtils.round(materialsVM.totalFOC).toString(),
            null,
            null
        )
        binding.materialSelectionFloatingMenu.close(true)
    }

    internal open fun addItemDialogFree() {
        adapterMaterialsList = materialsVM.freeMaterialsList
        var total = 0.0
        var materialGroupSelectedTotal = 0.0
        materialsVM.listOfMaterialGroupItems.forEach {
            total += it.value.totalPrice
            materialGroupSelectedTotal += it.value.selectedPrice
        }
        Log.d("getTotalValueFOCGroup" , " $materialGroupSelectedTotal , $total , ${materialsVM.totalFOC} ")
        materialsVM.currentMaterialList = "Free"
        showAddItem(
            "Free",
            resources.getString(R.string.add_free_item),
            R.drawable.ic_free_label,
            resources.getString(R.string.total_group_foc),
            AppUtils.round(materialsVM.totalFOC).toString(),
            AppUtils.round(total).toString(),
            AppUtils.round(materialGroupSelectedTotal).toString()
        )
        binding.materialSelectionFloatingMenu.close(true)
    }

    internal open fun proceed() {}

    private fun creditLimitIsAvailable() : Boolean{
        if(isCash||materialsVM.isReturn||materialsVM.isROffload)
            return true
        var credit = binding.materialSelectionTotalCreditValueTextView.text.toString().toDouble()
        var totalPrice = materialsVM.getTotalInvoicePrice().toDouble()
        return credit >= totalPrice
    }

    private fun creditLimitForTopUpIsAvailable() : Boolean{
        var credit = binding.materialSelectionTotalCreditValueTopUpTextView.text.toString().toDouble()
        var totalPrice = materialsVM.getTotalInvoicePrice().toDouble()
        Log.d("getCreditTopUp", "$credit , $totalPrice")
        return credit <= totalPrice
    }

    internal open fun nextButtonAction() {
        if (creditLimitIsAvailable() || materialsVM.isOffload) {
            if(materialsVM.isTopUp && creditLimitForTopUpIsAvailable()){
                Toast.makeText(this,getString(R.string.you_have_exceeded_top_up_limit),Toast.LENGTH_LONG).show()
            }else{
            if (checkFOCActivated()) {
                var focAlert = AlertDialog.Builder(this)
                focAlert.setMessage(getString(R.string.you_missed_to_add_foc_items))
                focAlert.setPositiveButton(resources.getString(R.string.ok)) { dialogFoc, _ ->
                    dialogFoc.dismiss()
                    var alertDialog = AlertDialog.Builder(this)
                    if (materialsVM.selectedMaterialsList.size > 0 || materialsVM.isROffload) {
                        alertDialog.setMessage(resources.getString(R.string.proceed_alert_dialog))
                        alertDialog.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                            dialog.dismiss()
                            val message = materialsVM.checkNextAction()
                            if (message == "true") {
                                exInvoiceNumber =
                                    materialsVM.salesDocRepository.buildExInvoiceNumberNew(materialsVM.routes.route)
                                proceed()
                            } else {
                                alertDialog = AlertDialog.Builder(this)
                                if (materialsVM.isCredit) {
                                    alertDialog.setMessage("$message ${getString(R.string.send_approval)}")
                                    alertDialog.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                                        typeVisit = TypeVisitEnum.APPROVAL
                                        exInvoiceNumber =
                                            materialsVM.salesDocRepository.buildExInvoiceNumberNew(
                                                materialsVM.routes.route
                                            )
                                        proceed()
                                    }
                                } else {
                                    alertDialog.setMessage(message)
                                    alertDialog.setPositiveButton(resources.getString(R.string.ok)) { dialog2, _ -> dialog2.dismiss() }

                                }
                                alertDialog.show()

                            }
                        }
                    } else {
                        alertDialog.setMessage(resources.getString(R.string.select_item_alert_dialog))
                        alertDialog.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                    }
                    alertDialog.show()
                }
                focAlert.setNegativeButton(resources.getString(R.string.no)) { dialogFoc, _ -> dialogFoc.dismiss() }
                focAlert.show()
            } else {
                var alertDialog = AlertDialog.Builder(this)
                if (materialsVM.selectedMaterialsList.size > 0 || materialsVM.isROffload) {
                    alertDialog.setMessage(resources.getString(R.string.proceed_alert_dialog))
                    alertDialog.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                    alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                        dialog.dismiss()
                        val message = materialsVM.checkNextAction()
                        if (message == "true") {
                            exInvoiceNumber =
                                materialsVM.salesDocRepository.buildExInvoiceNumberNew(materialsVM.routes.route)
                            proceed()
                            Log.d("exInvoiceNumber", "$exInvoiceNumber")
                        } else {
                            alertDialog = AlertDialog.Builder(this)
                            if (materialsVM.isCredit) {
                                alertDialog.setMessage("$message ${getString(R.string.send_approval)}")
                                alertDialog.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                                    typeVisit = TypeVisitEnum.APPROVAL
                                    exInvoiceNumber =
                                        materialsVM.salesDocRepository.buildExInvoiceNumberNew(
                                            materialsVM.routes.route
                                        )
                                    proceed()
                                }
                            } else {
                                alertDialog.setMessage(message)
                                alertDialog.setPositiveButton(resources.getString(R.string.ok)) { dialog2, _ -> dialog2.dismiss() }

                            }
                            alertDialog.show()

                        }
                    }
                } else {
                    alertDialog.setMessage(resources.getString(R.string.select_item_alert_dialog))
                    alertDialog.setPositiveButton(resources.getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                }
                alertDialog.show()
            }
            }
        }else{
            Toast.makeText(this,getString(R.string.credit_exceed_alert_dialog),Toast.LENGTH_LONG).show()
        }
    }

    private fun checkFOCActivated(): Boolean {
        return (focAvailable && !materialsVM.isFree && materialsVM.selectedFreeMaterialsList.isEmpty() && !materialsVM.isReturn && !materialsVM.isLoad)
    }

    private fun previousButtonAction() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(resources.getString(R.string.back_alert_dialog))
        alertDialog.setMessage(resources.getString(R.string.warning_alert_dialog))
        alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        alertDialog.setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        alertDialog.show()
    }

    var focAvailable = false

    internal open fun createTabs() {
        materialsVM.isUpdatePrice = false
        val currentTab = binding.materialSelectionViewPager.currentItem
        val tabsAdapter = ViewPagerAdapter(supportFragmentManager)
        var totalMaterialGroupFOC = 0.0
        materialsVM.totalFOC
        Log.d("getMatTotalFOC", "${materialsVM.totalItemFOC}")
        materialsVM.listOfMaterialGroupItems.forEach {
            totalMaterialGroupFOC += it.value.totalPrice
        }
        if (materialsVM.totalFOC > 0.0 || totalMaterialGroupFOC > 0.0 || materialsVM.totalItemFOC > 0.0) {
            tabsAdapter.addFragment(
                SelectedMaterialsListFragment(
                    materialsVM.selectedMaterialsList,
                    materialsVM, true
                ), resources.getString(R.string.items)
            )
            if (!materialsVM.isFree && !materialsVM.isReturn && !materialsVM.isLoad) {
                tabsAdapter.addFragment(
                    SelectedMaterialsListFragment(
                        materialsVM.selectedFreeMaterialsList,
                        materialsVM, true
                    ), resources.getString(R.string.free)
                )
                focAvailable = true
                binding.materialSelectionFloatingAddFreeButton.visibility = View.VISIBLE
            }
        } else {
            focAvailable = false
            tabsAdapter.addFragment(
                SelectedMaterialsListFragment(
                    materialsVM.selectedMaterialsList,
                    materialsVM, true
                ), resources.getString(R.string.items)
            )
            binding.materialSelectionFloatingAddFreeButton.visibility = View.GONE
            for (material in materialsVM.selectedFreeMaterialsList) {
                materialsVM.removeFreeMaterial(material)
                materialsVM.deleteFromMaterialGroupList(material)
            }
            materialsVM.finishSelectedFreeList(null)
            materialsVM.selectedFreeMaterialsList.clear()
            materialsVM.selectedFreeTotalPrice = BigDecimal.ZERO
        }

        binding.materialSelectionViewPager.adapter = tabsAdapter
        binding.materialSelectionViewPager.currentItem = currentTab
        val fragmentList = tabsAdapter.getFragmentList()
        for (index in fragmentList.indices) {
            val fragment = fragmentList[index] as SelectedMaterialsListFragment
            fragment.rvOnTouch = {
                if (it == "DOWN") {
                    binding.materialSelectionFloatingMenu.alpha = 0.3f
                    binding.materialSelectionFloatingMenu.close(true)
                } else if (it == "UP")
                    binding.materialSelectionFloatingMenu.alpha = 1f
            }
            fragment.adapter.itemDelete = {
                if (index == 0) {
                    materialsVM.removeMaterial(it)
                    materialsVM.applyDiscount()
                    materialsVM.finishSelectedItemList()
                } else if (index == 1) {
                    materialsVM.removeFreeMaterial(it)
                    materialsVM.deleteFromMaterialGroupList(it)
                    materialsVM.finishSelectedFreeList(null)
                }
                when {
                    materialsVM.isExchange && !materialsVM.isFree -> {
                    }
                    else -> {
                        binding.totalPrice =
                            String.format("%.3f", materialsVM.getTotalInvoicePrice().toBigDecimal())
                    }
                }
                showClickToAddItem()
                createTabs()

            }
        }
    }

    internal fun showClickToAddItem() {
        if (binding.materialSelectionTabLayout.selectedTabPosition == 0 && materialsVM.selectedMaterialsList.size == 0) {
            binding.clickToAddItemTextView.text =
                resources.getString(R.string.click_to_add_material)
            binding.clickToAddItemTextView.visibility = View.VISIBLE
        } else if (binding.materialSelectionTabLayout.selectedTabPosition == 1 &&
            materialsVM.selectedFreeMaterialsList.size == 0 && !materialsVM.isROffload
        ) {
            if (materialsVM.isExchange) {
                binding.clickToAddItemTextView.text =
                    resources.getString(R.string.add_Sold_item)
                binding.clickToAddItemTextView.visibility = View.VISIBLE

            } else {
                binding.clickToAddItemTextView.text =
                    resources.getString(R.string.click_to_add_free_material)
                binding.clickToAddItemTextView.visibility = View.VISIBLE
            }
        } else
            binding.clickToAddItemTextView.visibility = View.GONE
    }

    internal open fun showAddItem(type : String,header: String, image: Int, FOCText: String,
                                  FOCValue: String, total : String? , total2 : String?) {
        val showAddDialog = ShowAddDialog()
        showAddDialog.show(this.supportFragmentManager, "")
        if(type == "Sold")
            materialsVM.setGroupListFromFreeList()
        else if (type == "ReturnExchange")
            materialsVM.setGroupList()

        materialsVM.showShowAddData.postValue(
            ShowAddModel(
                type = type,
                image = image,
                header = header,
                FOCText = FOCText,
                FOCValue = FOCValue,
                materialGroupFOCValue = total,
                materialGroupFOCTotal = total2
            )
        )
    }

    private fun showInvoiceDetailsDialog() {
        val invoiceDetailsDialog = InvoiceDetailsDialog()
        invoiceDetailsDialog.show(this.supportFragmentManager, "")
    }

    open fun signInApproved() {

    }

/*    override fun onBackPressed() {
        previousButtonAction()
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
/*        if (requestCode == Constants.camPermission && resultCode == Activity.RESULT_OK && data != null) {
            searchEditText!!.setText(
                data.getStringExtra("barcode").toString(),
                TextView.BufferType.EDITABLE
            )
        }*/
    }

    open fun driverIsOnline(
        docType: String,
        itemCategory1: String,
        itemCategory2: String,
        isApproval : Boolean?
    ) {

        val iSalesDocHeader =
            materialsVM.setISalesDocHeader(docType, exInvoiceNumber)
        val headerItems =
            materialsVM.setAllHeaderItems(exInvoiceNumber, itemCategory1, itemCategory2)
        val headerBatches =
            materialsVM.setHeaderBatches(exInvoiceNumber, iSalesDocHeader, headerItems)
        val mySalesDocBody =
            materialsVM.setSalesDocBody(iSalesDocHeader, headerItems, headerBatches)
        iSalesDocHeader.discountValue = materialsVM.totalMaterialDiscount.toBigDecimal()

        if(materialsVM.isReturn) {
/*
            iSalesDocHeader.returnReason = instanceReturnMaterialSelection?.selectedReturnReasonFieldValue
            if(binding.payAmount.text.toString().trim().isNotEmpty())
            iSalesDocHeader.customizeheader = binding.payAmount.text.toString()
*/

        }

        val progressDialog = Dialog(this)
//        AppUtils.showProgressDialog(progressDialog, resources.getString(R.string.posting))
        materialsVM.post(mySalesDocBody)
        materialsVM.postResponse.observe(this) {
            progressDialog.dismiss()
            when {
                it!!.messagetype == "S" -> {
                    saveDoc(
                        it.message1,
                        DocumentsEnum.POSTED,
                        iSalesDocHeader,
                        headerItems,
                        headerBatches,
                        Constants.IS_ONLINE,
                        isApproval
                    )
                }
                it.messagetype == Constants.STATUS_ERROR -> {
                    AppUtils.dialogWithOkButton(
                        resources.getString(R.string.something_went_wrong),
                        this,
                        this,
                        false,
                        MaterialSelectionActivity()
                    )
                }
                else -> {
                    if(it.message1 < it.message2)
                    AppUtils.dialogWithOkButton(
                        it.message2,
                        this,
                        this,
                        false,
                        MaterialSelectionActivity()
                    )
                    else
                    AppUtils.dialogWithOkButton(
                        it.message1,
                        this,
                        this,
                        false,
                        MaterialSelectionActivity()
                    )
                }
            }
        }
    }

    open fun postOnlineOrderInvoice(
        docType: String,
        itemCategory1: String,
        itemCategory2: String,
        secondInvoice : String? = null
    ) {
        exInvoiceNumber = materialsVM.sharedPreference.getSelectedRoute()?.let {
            materialsVM.salesDocRepository
                .buildExInvoiceNumberNew(it)
        }.toString()
        val iSalesDocHeader =
            materialsVM.setISalesDocHeader(docType, exInvoiceNumber)
        iSalesDocHeader.secondExDoc = secondInvoice
        iSalesDocHeader.discountValue = materialsVM.totalMaterialDiscount.toBigDecimal()
        val headerItems =
            materialsVM.setAllHeaderItems(exInvoiceNumber, itemCategory1, itemCategory2)
        val headerBatches =
            materialsVM.setHeaderBatches(exInvoiceNumber, iSalesDocHeader, headerItems)
        val mySalesDocBody =
            materialsVM.setSalesDocBodyForOrderInvoice(iSalesDocHeader, headerItems, headerBatches)


        materialsVM.sendDocument(mySalesDocBody)
        materialsVM.viewStateCreateDocument.observe(this) { viewState ->
            when (viewState) {
                is CreateDocumentViewState.Data -> {
                    if (viewState.data.data[0].messagetype == "S")
                        renderCreateDocument(
                            viewState.data,
                            iSalesDocHeader,
                            headerItems,
                            headerBatches
                        )
                    else if (viewState.data.data[0].messagetype == "E"){
                        Toast.makeText(this,viewState.data.data[0].message2,Toast.LENGTH_LONG).show()
                    }
//                        renderCreateDocumentError()
                }
                is CreateDocumentViewState.NetworkFailure -> {
                    renderNetworkFailure()
                }
                is CreateDocumentViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is CreateDocumentViewState.EmptyResponse -> {
                    renderEmptyResponse()
                }
                is CreateDocumentViewState.UnknownError -> {
                    renderUnknownError()
                }
            }
        }
    }

    fun driverIsOfflineDialog(
        docType: String,
        itemCategory1: String,
        itemCategory2: String
    ) {
        val iSalesDocHeader =
            materialsVM.setISalesDocHeader(docType, exInvoiceNumber)
        val headerItems =
            materialsVM.setAllHeaderItems(exInvoiceNumber, itemCategory1, itemCategory2)
        val headerBatches =
            materialsVM.setHeaderBatches(exInvoiceNumber, iSalesDocHeader, headerItems)
        iSalesDocHeader.discountValue = materialsVM.totalMaterialDiscount.toBigDecimal()

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(resources.getString(R.string.offline_posting))
        alertDialog.setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            startService(Intent(this, ConnectionService::class.java))
            saveDoc(
                erpNumber = "",
                status = DocumentsEnum.NOT,
                iSalesDocHeader = iSalesDocHeader,
                headerItems = headerItems,
                headerBatches = headerBatches,
                Constants.IS_OFFLINE,
                false
            )
            AppUtils.proceedToActivity(this, this, ActionsActivity())
        }
        alertDialog.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    fun saveDoc(
        erpNumber: String,
        status: DocumentsEnum,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>,
        isOffline: String,
        isApproval: Boolean?
    ) {
        materialsVM.salesDocRepository.saveDocument(
            exInvoiceNumber,
            mCurrentLocation,
            iSalesDocHeader,
            headerItems,
            headerBatches,
            erpNumber,
            status
        )
        if (materialsVM.isOffload) {
//            materialsVM.truckManagementRepository = TruckManagementRepository(application)
//            materialsVM.truckManagementRepository.deleteDamagedItemsAndBatches()
//            AppUtils.proceedToActivity(this, this, EndOfDayActivity())
        }
    }


    private fun checkLocation() {
/*        val locationPermission = LocationPermission(this)
        if (!locationPermission.checkLocationPermission()) {
            locationPermission.requestLocationPermission()
        } else {
            initLocationService()
        }*/
    }

    private fun initLocationService() {
  /*      mFusedLocationClient.lastLocation
            .addOnCompleteListener { taskLocation ->
                if (taskLocation.isSuccessful && taskLocation.result != null) {
                    mCurrentLocation = taskLocation.result
                }
            }*/
    }

    open fun setMaterialsTruckItemListAdapter(
        materialsList: ArrayList<MaterialsTruckItem>,
        rv: RecyclerView,
        priceTextView: TextView
    ) {
        val adapter = MaterialsListAdapter(materialsList, materialsVM)
        rv.adapter = adapter
        rv.adapter?.notifyDataSetChanged()
        adapter.itemSelected = {
            materialsVM.addSelectedMaterial(it,null)
            priceTextView.text = materialsVM.getTotalPriceDialog()
        }
    }

    override fun renderCreateDocument(data: BaseResponse<PostDocumentResponse>) {

    }

    override fun renderCreateDocument(
        salesDocBody: SalesDocBody,
        data: BaseResponse<PostDocumentResponse>, name : String, number : String
    ) {

    }
    override fun renderUnknownError() {
        Toast.makeText(this,"Unknown Error",Toast.LENGTH_LONG).show()
    }

    override fun renderEmptyResponse() {
        Toast.makeText(this,"No Response",Toast.LENGTH_LONG).show()
    }
    override fun renderCreateDocument(
        data: BaseResponse<PostDocumentResponse>,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>
    ) {
        this@MaterialSelectionActivity.iSalesDocHeader = iSalesDocHeader
        saveDoc(
            data.data[0].message1,
            DocumentsEnum.POSTED,
            iSalesDocHeader,
            headerItems,
            headerBatches,
            Constants.IS_ONLINE,
            false
        )
        AppUtils.dialogWithOkButton(
            "${getString(R.string.document_created)} ${data.data[0].message1}" , this, this, false, VisitsActivity(),
            false, isOrderMaterial = true
        )

//        AppUtils.proceedToActivity(this, this, ActionsActivity())
    }


    override fun renderPayersSuccess(data: PayersResponseModel) {
        if (materialsVM.isCredit) {
            binding.loadingPayersTextView.visibility = View.GONE
            allPayers.clear()
            allPayersName.clear()
//            allPayers = data.data
            data.data.forEach {
                Log.d("hwaEHELhabalDah", "$it")
                allPayers.add(it)
                allPayersName.add(it.name1)
            }
            if(data.data.isNotEmpty()) {
                selectedPayerID = data.data[0].payer.toString()
                selectedPayer = data.data[0].name1
            }
            /***** ForTesting ****

            allPayers.add(DataPayers("","","","","","","Name1","","",1212.3
                ,"","","","","",0.0,0.0,""))
            allPayersName.add("Name 2")

             */
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                allPayersName
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.payerSpinner.adapter = adapter

            binding.payerSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        view: AdapterView<*>?,
                        p1: View?,
                        i: Int,
                        p3: Long
                    ) {
//                        selectedIgort = igortData[i]
                        selectedPayer = allPayers[i].name1
                        selectedPayerID = allPayers[i].payer.toString()
                        binding.materialSelectionTotalCreditValueTextView.text = allPayers[i].credit.toString()
                        credit = allPayers[i].credit.toString().toDouble()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
        }
    }
    var credit = 0.0
    override fun renderRequestStatus(data: BillToBillCheckResponseModel, totalValueNormalCreditLimit: String, totalValueTopUpCreditLimit: String) {
        Log.d("totalRequestStatus", " fill up $totalValueNormalCreditLimit , $totalValueTopUpCreditLimit" )
        var totalRequestStatus = 0.0
        data.data.items.forEach {
            totalRequestStatus += it.totalvalue

        }
        isLoadedRequestStatus = false
        Log.d("totalRequestStatus23", "bas $totalRequestStatus , ${data.data.items.size}")

        fillUpViewModel.getDeliveriesRemote(
            BaseBody(requestStatusViewModel.sharedPref.getSalesOrg()!!,
            requestStatusViewModel.sharedPref.getDistChannel()!!,
            requestStatusViewModel.sharedPref.getDivision()!!,
            requestStatusViewModel.sharedPref.getDriverNumber() ?: "")
        )

        fillUpViewModel.deliveriesStatusLiveData.observe(this){
            status -> when (status){
            Constants.STATUS_LOADED -> {
                binding.isLoading = false
                getTotalCreditLimit(totalRequestStatus,totalValueNormalCreditLimit,totalValueTopUpCreditLimit)
            }
            Constants.STATUS_EMPTY -> {
                binding.isLoading = false
                getTotalCreditLimit(totalRequestStatus,totalValueNormalCreditLimit,totalValueTopUpCreditLimit)
            }
            else -> binding.isLoading = false

            }

        }
//        Log.d("totalRequestStatus44", " total Req $totalRequestStatus")

    }
    var isLoadedRequestStatus = false
    private fun getTotalCreditLimit(totalRequestStatus : Double, totalValueNormalCreditLimit : String,
                                    totalValueTopUpCreditLimit : String){
        var totalRequestStatusValue = totalRequestStatus
        Log.d("totalRequestStatus21", " Total Req $totalRequestStatusValue")
        if(!isLoadedRequestStatus) {
            fillUpViewModel.fillUpList?.forEach {
                totalRequestStatusValue += it.totalAmount!!
                Log.d("totalRequestStatus1", it.totalAmount.toString())
            }
            isLoadedRequestStatus = true
            Log.d("totalRequestStatus0" , " ${fillUpViewModel.fillUpList}")
            Log.d("totalRequestStatus22", " Total Req $totalRequestStatusValue")
            val finalNormalValue = totalValueNormalCreditLimit.toDouble() - totalRequestStatusValue
            val finalTopUpValue = totalValueTopUpCreditLimit.toDouble() - totalRequestStatusValue
            Log.d("askdjsakdjsakd","${totalValueNormalCreditLimit} , ${totalValueTopUpCreditLimit} ,  ${totalRequestStatusValue} , " +
                    "$finalNormalValue , $finalTopUpValue")
            binding.materialSelectionTotalCreditValueTextView.text = convertToEnglish(String.format("%.3f",finalNormalValue))
            binding.materialSelectionTotalCreditValueTopUpTextView.text = convertToEnglish(String.format("%.3f",finalTopUpValue))

        }

    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {
        Toast.makeText(this,"Network Error",Toast.LENGTH_LONG).show()
    }

    override fun renderRequestStatusError() {
        Toast.makeText(this,"Request Error",Toast.LENGTH_LONG).show()
    }

    override fun renderPayersFailure() {
            Log.d("RenderPayersIs" , " FAILED To Render ")
    }

    override fun renderCreateDocumentError() {
      /*  AppUtils.dialogWithOkButton(
            resources.getString(R.string.something_went_wrong),
            this,
            this,
            false,
            MaterialSelectionActivity()
        )*/

    }
    var invoiceNumber = ""

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
            LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }
}