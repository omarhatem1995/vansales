package com.company.vansales.app.activity

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.telecom.ConnectionService
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.company.vansales.BuildConfig
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Deliveries
import com.company.vansales.app.datamodel.models.mastermodels.DeliveriesBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.IMaterials
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.mastermodels.StorageLocationResponse
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import com.company.vansales.app.domain.usecases.CreateDocumentUseCases
import com.company.vansales.app.domain.usecases.StorageLocationUseCases
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.getNextDueDate
import com.company.vansales.app.utils.AppUtils.getStringCurrentDate
import com.company.vansales.app.utils.AppUtils.getStringCurrentTime
import com.company.vansales.app.utils.Constants.MATERIAL_DELIVERIES
import com.company.vansales.app.utils.DocumentsConstants.APPROVAL
import com.company.vansales.app.utils.DocumentsConstants.CONSIGNMENT_PICK_UP
import com.company.vansales.app.utils.DocumentsConstants.CONSIGNMENT_POST_ISSUE
import com.company.vansales.app.utils.DocumentsConstants.FOC_INVOICE
import com.company.vansales.app.utils.DocumentsConstants.RETURN_WITHOUT_REFERENCE
import com.company.vansales.app.utils.DocumentsConstants.PICK_UP_RETURN
import com.company.vansales.app.utils.InternetCheckUtils
import com.company.vansales.app.utils.LocaleHelper
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.utils.enums.DocumentsEnum
import com.company.vansales.app.utils.extensions.displayToast
import com.company.vansales.app.utils.extensions.stringText
import com.company.vansales.app.view.adapter.BatchScannerListAdapter
import com.company.vansales.app.view.entities.CreateDocumentViewState
import com.company.vansales.app.view.entities.GetStorageLocViewState
import com.company.vansales.app.viewmodel.ApplicationSettingsViewModel
import com.company.vansales.app.viewmodel.BatchScanViewModel
import com.company.vansales.app.viewmodel.FillUpViewModel
import com.company.vansales.app.viewmodel.MaterialsViewModel
import com.company.vansales.app.viewmodel.SalesDocViewModel
import com.company.vansales.app.viewmodel.TruckManagementViewModel
import com.company.vansales.databinding.BatchScanLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class BatchScanActivity : AppCompatActivity(), StorageLocationUseCases.View,
    CreateDocumentUseCases.View {

    private lateinit var binding: BatchScanLayoutBinding

    val allStorageLoc: HashMap<String, MutableList<String>> = hashMapOf()

    val allWerksData: MutableList<String> = mutableListOf()


    var igortData: MutableList<String> = mutableListOf()

    var selectedWerks: String? = null
    var selectedIgort: String? = null

    var exInvoiceNumber = ""
    var collectedAmount = "0"


    //ViewModels imported
    val batchScanViewModel: BatchScanViewModel by viewModels()
    val fillUpViewModel: FillUpViewModel by viewModels()
    val truckManagementViewModel: TruckManagementViewModel by viewModels()
    val salesDocViewModel: SalesDocViewModel by viewModels()
    val materialsViewModel: MaterialsViewModel by viewModels()
    val applicationSettingsViewModel: ApplicationSettingsViewModel by viewModels()

    private var selectedFillUpItem: Delivery? = null
    private var iSalesDocHeader: ISalesDocHeader? = null
    lateinit var itemAdapter: BatchScannerListAdapter
    var invoiceNumber = ""

    var isApproval = false
    var customerName = ""
    var payer = ""
    var payerName = ""

    var isDelivery = false

    companion object {
        var instance: BatchScanActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = DataBindingUtil.setContentView(this, R.layout.batch_scan_layout)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()?.toLowerCase() == "ar"){
            LocaleHelper.applyLocalizedContext(this, "ar")
            binding.root.layoutDirection = resources.configuration.layoutDirection
        }
        cameraPermission()
        initView()
        initButtons()
        initObservers()
        getUpdateLocation()
    }

    private fun getUpdateLocation() {
     /*   UpdateLocationService.locationResult.observe(this) {
            if (it.gpsEnable)
                acurateLocation = it.acurateLocation
        }*/
    }

    override fun onBackPressed() {
        if (binding.isLoading == true) areYouSureDialog()
        else super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        checkLocation()
    }

    private fun initView() {
        val versionName = BuildConfig.VERSION_NAME
        binding.versionNumber.text = "Version : $versionName"
        binding.autoAddBTN.tag = "blue"
        val batchIntent = intent.extras
        batchIntent?.let {
            if (batchIntent.getSerializable("deliveries") != null) {
                batchScanDelivery(batchIntent)
            } else if (batchIntent.getSerializable("materialsISalesDocHeader") != null) {
                batchScanMaterials(batchIntent)
            }
            Log.d("getDocType", "${iSalesDocHeader?.doctype}")
            if(iSalesDocHeader?.doctype == CONSIGNMENT_POST_ISSUE)
                batchScanViewModel.isFillUp = true

            customerName = batchIntent.getString("customerName").toString()
            Log.d("customerName", "$customerName")
            payer = batchIntent.getString("payer").toString()
            payerName = batchIntent.getString("payerName").toString()
            isApproval = batchIntent.getBoolean("isApproval")

            binding.type = batchIntent.getSerializable("type") as BatchScanTypesEnum
            if (binding.type == BatchScanTypesEnum.APPROVAL) {
                Log.d("asdlkasldkasd", " is called")
                batchScanViewModel.isApproved = true
                val invoiceHeader = InvoiceHeader(
                    iSalesDocHeader?.customer.toString(),
                    payer,
                    customerName,
                    materialsViewModel.getDriver()?.name1 ?: "",
                    materialsViewModel.sharedPreference.getDriverNumber().toString(),
                    iSalesDocHeader?.exdoc ?: "",
                    getStringCurrentTime(),
                    getStringCurrentDate(),
                    getNextDueDate(Date()),
                    invoiceNumber,
                    "7 days due net",
                    iSalesDocHeader?.route,
                    "",
                    payerName
                )
            }
            if (iSalesDocHeader?.doctype == RETURN_WITHOUT_REFERENCE) {
                batchScanViewModel.subtract.value = false
                autoCount()
                binding.type = BatchScanTypesEnum.RETURN
                batchScanViewModel.isReturn = true
                binding.autoAddBTN.visibility = View.GONE
                binding.autoAddBTN.isEnabled = false

            }
        }
        renderStorageLocFail()
    }

    private fun initButtons() {
        binding.autoAddBTN.setOnClickListener {
            if (binding.autoAddBTN.tag == "blue") {
                autoCount()
            } else {
                undoAutoCount()
            }
        }
        binding.batchScanPreviousButton.setOnClickListener {
            onBackPressed()
        }
        binding.hideCountedItemsS.setOnCheckedChangeListener { _, isChecked ->
            batchScanViewModel.hideCountedMLD.value = isChecked
        }
        binding.batchScanSearchET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                searchQuery: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (searchQuery.isNotEmpty()) {
                    initMaterialsList(
                        handleSearch(searchQuery.toString()),
                        headerBatches = batchScanViewModel.deliveriesBatchesMLD.value!!
                    )
                } else {
                    initMaterialsList(
                        headerItems = batchScanViewModel.deliveriesItemsMLD.value!!,
                        headerBatches = batchScanViewModel.deliveriesBatchesMLD.value!!
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.batchScanDoneButton.setOnClickListener {
            if (binding.type == BatchScanTypesEnum.STOCK_COUNT) {
                areYouSureYouWantToPostDialog(isApproval)
            } else {
                if (!batchScanViewModel.isAllItemsQuantityFullyLoad()) {
                    this.displayToast(R.string.qr_error_button_dismiss)
                } else {
                    areYouSureYouWantToPostDialog(isApproval)
                }
            }
        }
/*
        binding.scanBarcodeIV.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivityForResult(intent, Constants.camPermission)
        }*/
    }

    private fun initObservers() {
        batchScanViewModel.viewStateStorageLocView.observe(this) { viewState ->
            when (viewState) {
                is GetStorageLocViewState.NetworkFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                    renderStorageLocFail()
                }
                is GetStorageLocViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is GetStorageLocViewState.Data -> {
                    val data = viewState.data.data
                    renderStorageLocSuccess(data.toMutableList())
                    renderLoading(false)
                }
            }
        }
        batchScanViewModel.deliveriesItemsLiveData.observe(this) { items ->
            if (!items.isNullOrEmpty()) {
                initMaterialsList(
                    headerItems = batchScanViewModel.deliveriesItemsMLD.value!!,
                    headerBatches = batchScanViewModel.deliveriesBatchesMLD.value!!
                )
            }
        }

        batchScanViewModel.exceededAmountMLD.observe(this) { hasExceeded ->
            if (hasExceeded!!) {
                this.displayToast(
                    this.resources.getString(R.string.exceeding_batches)
                )
            }
        }
        batchScanViewModel.viewStateCreateDocument.observe(this) { viewState ->
            when (viewState) {
                is CreateDocumentViewState.Data -> {
                    renderLoading(false)
                    renderCreateDocument(viewState.data)
                }
                is CreateDocumentViewState.NetworkFailure -> {
                    renderLoading(false)
                    renderNetworkFailure()
                }
                is CreateDocumentViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is CreateDocumentViewState.EmptyResponse -> {
                    renderLoading(false)
                    renderEmptyResponse()
                }
                is CreateDocumentViewState.UnknownError -> {
                    renderLoading(false)
                    renderUnknownError()
                }
            }

        }
    }

    lateinit var deliveries: Deliveries
    private fun batchScanMaterials(batchIntent: Bundle?) {
        iSalesDocHeader =
            batchIntent!!.getSerializable("materialsISalesDocHeader") as ISalesDocHeader
        val headerItems: ArrayList<HeaderItems> =
            batchIntent.getSerializable("materialsHeaderItems") as ArrayList<HeaderItems>
        batchScanViewModel.mapMaterialsList(iSalesDocHeader!!, headerItems)
        collectedAmount = iSalesDocHeader?.customizeheader.toString()
    }

    private fun batchScanDelivery(batchIntent: Bundle?) {
        isDelivery = true
        selectedFillUpItem =
            batchIntent!!.getParcelable<Delivery>("selectedFillUpItem") as Delivery
        deliveries =
            batchIntent.getSerializable("deliveries") as Deliveries
        exInvoiceNumber = materialsViewModel.sharedPreference.getSelectedRoute()?.let {
            materialsViewModel.salesDocRepository
                .buildExInvoiceNumberNew(it)
        }.toString()

        iSalesDocHeader = batchScanViewModel.userPreference.getSalesOrg()?.let { salesOrg ->
            selectedFillUpItem!!.totalAmount?.let { totalAmount ->
                ISalesDocHeader(
                    salesOrg,
                    batchScanViewModel.userPreference.getDistChannel()!!,
                    batchScanViewModel.userPreference.getDivision()!!,
                    "",
                    "",
                    batchScanViewModel.userPreference.getSelectedRoute()!!,
                    batchScanViewModel.userPreference.getDriverNumber(),
                    batchScanViewModel.userPreference.getDriverNumber(),
                    CONSIGNMENT_POST_ISSUE,
                    exInvoiceNumber,
                    Date(),
                    Date(),
                    totalAmount.toBigDecimal(),
                    0.0.toBigDecimal(),
                    "",
                    "",
                    "",
                    "",
                    selectedFillUpItem!!.delivery,
                    ""
                )
            }
        }
        batchScanViewModel.setSelectedFillUpItem(selectedFillUpItem!!)
        batchScanViewModel.mapDeliveriesList(deliveries)
    }

    private fun initMaterialsList(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>
    ) {
        val materialNumbers: MutableList<IMaterials?> = mutableListOf()
        headerItems.forEach {
            materialNumbers.add(it.materialno?.let { it1 -> IMaterials(it1) })
        }
      /*  batchScanViewModel.getStorageLocation(
            StorageLocationBody(
                materialNumbers
            )
        )*/
        itemAdapter = BatchScannerListAdapter(
            headerItems,
            headerBatches.toMutableList(),
            batchScanViewModel,
            binding.type ?: BatchScanTypesEnum.CASH_MATERIALS
        )
        binding.itemsRV.adapter = itemAdapter
    }

    private fun autoCount() {
        if (batchScanViewModel.subtract.value!!) {
            batchScanViewModel.autoCountMaterialsAmount()
        } else {
            batchScanViewModel.autoCountDeliveryItemsAndBatches()
        }
        initMaterialsList(
            batchScanViewModel.deliveriesItemsMLD.value!!,
            batchScanViewModel.deliveriesBatchesMLD.value!!
        )
        binding.autoAddBTN.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        binding.autoAddBTN.text = this.resources.getString(R.string.undo)
        binding.autoAddBTN.tag = "red"
    }

    private fun undoAutoCount() {
        if (batchScanViewModel.subtract.value!!) {
            batchScanViewModel.undoAutoCountMaterialsAmount()
        } else {
            batchScanViewModel.undoAutoCountDeliveryItemsAndBatches()
        }
        initMaterialsList(
            batchScanViewModel.deliveriesItemsMLD.value!!,
            batchScanViewModel.deliveriesBatchesMLD.value!!
        )
        binding.autoAddBTN.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
        binding.autoAddBTN.text = this.resources.getString(R.string.auto)
        binding.autoAddBTN.tag = "blue"
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
     /*   if (requestCode == Constants.camPermission && resultCode == Activity.RESULT_OK && data != null) {
            val headerBatches = batchScanViewModel.deliveriesItemsMLD.value!!
            val scanBatch = data.getStringExtra("barcode").toString().toLong().toString()
            val filter = headerBatches.filter { batch ->
                batch.barcodeList?.any { barcode ->
                    barcode == scanBatch
                } == true
            }
            if (filter.isNotEmpty()) {
                val scanPos = headerBatches.indexOf(filter.first())
                itemAdapter.requestFocus(scanPos)
            }
        }*/
    }

    private fun areYouSureDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    dialog.dismiss()
                    super.onBackPressed()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.are_you_sure))
            .setPositiveButton(
                resources.getString(R.string.yes),
                dialogClickListener
            )
            .setNegativeButton(
                resources.getString(R.string.no),
                dialogClickListener
            ).show()
        builder.setCancelable(true)
    }

    private fun cameraPermission() {
   /*     val camPermission = CameraPermission(this)
        if (!camPermission.checkCameraPermission()) {
            camPermission.requestCameraPermission()
        }*/
    }

    private fun handleSearch(searchText: String): List<HeaderItems> {
        return batchScanViewModel.deliveriesItemsMLD.value!!.filter { p ->
            p.materialDescription!!.contains(searchText, true) ||
                    p.materialDescriptionArabic.contains(searchText, true) ||
                    p.materialno!!.contains(searchText, true) ||
                    p.barcodeList!!.any {
                        it.contains(searchText, true)
                    }
        }
    }

    private fun checkLocation() {
/*        val locationPermission = LocationPermission(this)
        if (!locationPermission.checkLocationPermission()) {
            locationPermission.requestLocationPermission()
        } else {
            val intent = Intent(this, UpdateLocationService::class.java)
            startService(intent)
        }*/
    }

    private fun areYouSureYouWantToPostDialog(isApproval: Boolean) {
        if (InternetCheckUtils.getConnectionType(this) != 0 || !isApproval) {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        if (isApproval) {
                            var alertDialog = android.app.AlertDialog.Builder(this)
                            alertDialog.setMessage(getString(R.string.send_approval))
                            alertDialog.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                                renderLoading(true)
                                doneBatchScan(isApproval)
                                alertDialog.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                            }
                            alertDialog.show()
                        } else {
                            doneBatchScan(isApproval)
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setMessage(resources.getString(R.string.are_you_sure_post))
                .setPositiveButton(
                    resources.getString(R.string.yes),
                    dialogClickListener
                )
                .setNegativeButton(
                    resources.getString(R.string.no),
                    dialogClickListener
                ).show()

            builder.setCancelable(true)
        } else {
            Toast.makeText(this, "Can't send offline Approval document", Toast.LENGTH_LONG)
                .show()
        }
    }
    private fun documentCreatedSuccessfully() {
        if (batchScanViewModel.subtract.value == true ||
            iSalesDocHeader?.doctype == RETURN_WITHOUT_REFERENCE) {
            truckManagementViewModel.mapSelectedMaterialsIntoTruckItemsAndBatches(
                batchScanViewModel.deliveriesItemsMLD.value!!,
                batchScanViewModel.deliveriesBatchesMLD.value!!,
                iSalesDocHeader!!
            )
        } else {
            truckManagementViewModel.mapDeliveriesIntoTruck(
                batchScanViewModel.deliveriesItemsMLD.value!!,
                batchScanViewModel.deliveriesBatchesMLD.value!!,
                selectedFillUpItem!!
            )
        }

        if(iSalesDocHeader?.paymentterm == APPROVAL && iSalesDocHeader?.doctype == FOC_INVOICE){
            val invoiceHeader = InvoiceHeader(
                iSalesDocHeader?.customer.toString(),
                payer ,
                customerName,
                materialsViewModel.getDriver()?.name1 ?: "",
                materialsViewModel.sharedPreference.getDriverNumber().toString(),
                iSalesDocHeader?.exdoc ?: "",
                getStringCurrentTime(),
                getStringCurrentDate(),
                getNextDueDate(Date()),
                invoiceNumber,
                "7 days due net",
                materialsViewModel.sharedPreference.getSelectedRoute(),
                "",
                payerName,
                invoiceType = iSalesDocHeader?.doctype
            )
            materialsViewModel.addHeader(invoiceHeader)

        }
    }
    fun showPrinterDialog() {
        if(invoiceNumber.split("-").isNotEmpty()){
            invoiceNumber = invoiceNumber.split("-")[0]
        }
        Log.d("printerDialogIsCalled", ": is Called $invoiceNumber $customerName")
        val invoiceHeader : InvoiceHeader
        if(binding.type == BatchScanTypesEnum.CASH_MATERIALS) {
             invoiceHeader = InvoiceHeader(
                iSalesDocHeader?.customer.toString(),
                payer,
                customerName,
                materialsViewModel.getDriver()?.name1 ?: "",
                materialsViewModel.sharedPreference.getDriverNumber().toString(),
                iSalesDocHeader?.exdoc ?: "",
                getStringCurrentTime(),
                getStringCurrentDate(),
                getNextDueDate(Date()),
                invoiceNumber,
                "7 days due net",
                materialsViewModel.sharedPreference.getSelectedRoute(),
                "",
                collectedAmount,
                invoiceType = iSalesDocHeader?.doctype
            )
        }
        else {
             invoiceHeader = InvoiceHeader(
                iSalesDocHeader?.customer.toString(),
                payer ?: "",
                customerName,
                materialsViewModel.getDriver()?.name1 ?: "",
                materialsViewModel.sharedPreference.getDriverNumber().toString(),
                iSalesDocHeader?.exdoc ?: "",
                getStringCurrentTime(),
                getStringCurrentDate(),
                getNextDueDate(Date()),
                invoiceNumber,
                "7 days due net",
                materialsViewModel.sharedPreference.getSelectedRoute(),
                "",
                payerName,
                invoiceType = iSalesDocHeader?.doctype
            )
        }
        var proceedTo: Activity = ActionsActivity()
        invoiceHeader.separated = iSalesDocHeader!!.secondExDoc?.isNotEmpty() == true
        if( iSalesDocHeader?.doctype != FOC_INVOICE) {
            materialsViewModel.addHeader(invoiceHeader)
        }else{
            materialsViewModel.updateHeader(invoiceHeader)
        }
        var itemsToPrint = mutableListOf<SalesDocItems>()
        if(binding.type == BatchScanTypesEnum.OFF_LOAD) {
            fillUpViewModel.getDeliveryCheckRemote(DeliveriesBody("${invoiceHeader.invoiceNumber}"))
            fillUpViewModel.deliveriesLiveData.observe(this) { deliveries ->
                deliveries?.let {
                    // Map DeliveryBatch to SalesDocItems
                    val batchItems = deliveries.deliveryBatch.map { batch ->
                        SalesDocItems(
                            exInvoice = batch.batch,
                            itemNum = batch.itemNo.toIntOrNull() ?: 0,
                            material = batch.materialNo,
                            description = null,
                            descriptionArabic = null,
                            quantity = batch.quantity,
                            price = null,
                            discount = null,
                            unit = batch.unit,
                            itemCategory = null,
                            totalPrice = batch.totalAmount,
                            currency = null,
                            mType = null,
                            storageLocation = batch.storageLocation,
                            plant = batch.plant
                        )
                    }
                    itemsToPrint = batchItems.toMutableList()

                    // Proceed with dependent logic
                    applicationSettingsViewModel.sharedPreferences.getPrinterAddress()?.let { printPath ->

                    }
                }
            }
        }else {
            iSalesDocHeader?.exdoc?.let {
                salesDocViewModel.getSalesDocItemsWithInvoiceNumber(it)
                    .observe(this) { items ->
                        var itemsToPrint = items.toMutableList()
                        applicationSettingsViewModel.sharedPreferences.getPrinterAddress()
                            ?.let { printPath ->
                                Log.d(
                                    "getiSalesDocHeadersecondExDoc",
                                    "${invoiceHeader.invoiceNumber}"
                                )

                            }
                    }
            }
        }
    }

    private fun doneBatchScan(isApproval: Boolean) {
//        if(batchScanViewModel.checkValidBatchesWithItemsQuantity() && batchScanViewModel.checkBatchesQuantityItems()) {
            if (binding.type != BatchScanTypesEnum.CASH_MATERIALS)
                iSalesDocHeader?.customizeheader = payer
            else {
                collectedAmount = iSalesDocHeader?.customizeheader.toString()
                Log.d("doneBatchScan", " ${iSalesDocHeader?.customizeheader} $collectedAmount")
            }
            if (InternetCheckUtils.getConnectionType(this) == 0) {
                if (!isApproval) {
                    if (batchScanViewModel.subtract.value == true) {
                        iSalesDocHeader?.plant = selectedWerks
                        iSalesDocHeader?.location = selectedIgort
                    }
                    driverIsOfflineDialog()
                } else {
                    Toast.makeText(
                        this, "Can't send request offline with Approval status", Toast.LENGTH_LONG
                    ).show()
                }
            }
            else {
                renderLoading(true)
                if (batchScanViewModel.subtract.value == true) {
                    iSalesDocHeader?.plant = selectedWerks
                    iSalesDocHeader?.location = selectedIgort
                    if (binding.type != BatchScanTypesEnum.OFF_LOAD) {
                        iSalesDocHeader?.location = ""
                    }else{
                        batchScanViewModel.isOffload = true
                    }
                    batchScanViewModel.mapMaterialsRequestBody(iSalesDocHeader!!)
                } else if (iSalesDocHeader?.doctype == RETURN_WITHOUT_REFERENCE) {
                    batchScanViewModel.mapMaterialsRequestBody(iSalesDocHeader!!)
                } else
                    batchScanViewModel.mapDeliveriesToTruckRequestBody(selectedWerks, selectedIgort)

            }
        /*}else{
            Toast.makeText(this,"Please Recheck the quantity of materials", Toast.LENGTH_LONG).show()
        }*/
    }

    private fun saveDocument(
        exInvoiceNumber: String,
        erpInvoiceNumber: String,
        status: DocumentsEnum
    ) {
        Log.d("enteredCreated" , "saved ${batchScanViewModel.deliveriesItemsMLD.value!!.size}")
        if(batchScanViewModel.isFillUp){
            /*salesDocViewModel.saveDocument(
                exInvoiceNumber = exInvoiceNumber,
                mCurrentLocation = null,
                iSalesDocHeader = iSalesDocHeader!!,
                headerItems = batchScanViewModel.deliveriesItemsMLD.value!!,
                headerBatches = batchScanViewModel.deliveriesBatchesMLD.value!!,
                erpInvoiceNumber = erpInvoiceNumber,
                status = status
            )*/
        }else
        salesDocViewModel.saveDocument(
            exInvoiceNumber = exInvoiceNumber,
            mCurrentLocation = null,
            iSalesDocHeader = iSalesDocHeader!!,
            headerItems = batchScanViewModel.deliveriesItemsMLD.value!!,
            headerBatches = batchScanViewModel.deliveriesBatchesMLD.value!!,
            erpInvoiceNumber = erpInvoiceNumber,
            status = status
        )
    }

    private fun driverIsOfflineDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    startService(Intent(this, ConnectionService::class.java))
                    saveDocument(iSalesDocHeader!!.exdoc, "", DocumentsEnum.NOT)
                    documentCreatedSuccessfully()
                    //we will not make print dialog unless we got erp invoice num
//                    AppUtils.proceedToActivity(this, this, ActionsActivity())
                    showPrinterDialog()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss()
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.offline_posting))
            .setPositiveButton(
                resources.getString(R.string.yes),
                dialogClickListener
            )
            .setNegativeButton(
                resources.getString(R.string.no),
                dialogClickListener
            ).show()

        builder.setCancelable(true)

    }

    override fun renderStorageLocSuccess(data: MutableList<StorageLocationResponse>) {
        allStorageLoc.clear()
        allWerksData.clear()
        data.forEach {
            if (!allWerksData.contains(it.werks)) {
                allWerksData.add(it.werks)
                allStorageLoc[it.werks] = mutableListOf(it.lgort)
            } else {
                val value = allStorageLoc[it.werks]
                value?.add(it.lgort)
                value?.let { _ ->
                    allStorageLoc.put(it.werks, value.toMutableList())
                }
            }
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, allWerksData
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWerks.adapter = adapter

        binding.spinnerWerks.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(view: AdapterView<*>?, p1: View?, i: Int, p3: Long) {
                selectedWerks = allWerksData[i]
                initSpinnerIgort(selectedWerks)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun renderStorageLocFail() {
        val applicationConfig = ApplicationSettingsRepository(application)
        val plant = applicationConfig.getAppConfigValueByAppParamter("RETURN_PLANT")
        val location = applicationConfig.getAppConfigValueByAppParamter("RETURN_LOCATION")
        selectedWerks = plant
        selectedIgort = location
    }

    private fun initSpinnerIgort(selectedWerks: String?) {
        selectedWerks?.let {
            igortData = allStorageLoc[it]!!
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                igortData
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerIgort.adapter = adapter

            binding.spinnerIgort.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        view: AdapterView<*>?,
                        p1: View?,
                        i: Int,
                        p3: Long
                    ) {
                        selectedIgort = igortData[i]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
        }
    }

    override fun renderCreateDocument(data: BaseResponse<PostDocumentResponse>) {
        if (data.data[0].messagetype == "S") {
            invoiceNumber = data.data[0].message1
            var secondInvoice = ""
            if (data.data[0].message2.isNotEmpty() && data.data[0].message2 != "")
                secondInvoice = "-${data.data[0].message2}"
            val toActionActivity = ActionsActivity()

            if(iSalesDocHeader?.doctype == PICK_UP_RETURN ||
                iSalesDocHeader?.doctype == CONSIGNMENT_PICK_UP){
                AppUtils.dialogWithOkButton(
                    message = this.stringText(R.string.document_created)
                            + " " + invoiceNumber + secondInvoice,
                    activity = this,
                    context = this,
                    finishActivity = false,
                    toActivity = toActionActivity,
                    isBatchScanActivity = true,
                    isApproval = isApproval
                )
                Log.d("enteredCreated", "1 $iSalesDocHeader , ${batchScanViewModel.isReturn}")
            }else {
                if (!isDelivery) {
                    Log.d("enteredCreated", "2 $isDelivery ${batchScanViewModel.isReturn}")
                    AppUtils.dialogWithOkButton(
                        message = this.stringText(R.string.document_created)
                                + " " + invoiceNumber + secondInvoice,
                        activity = this,
                        context = this,
                        finishActivity = false,
                        toActivity = toActionActivity,
                        isBatchScanActivity = true,
                        isApproval = isApproval
                    )
                } else {
                    if(batchScanViewModel.isFillUp){
                        fillUpViewModel.getDeliveryCheckRemote(DeliveriesBody("${data.data[0].exdoc}"))
                        invoiceNumber = data.data[0].exdoc
                        val observer = androidx.lifecycle.Observer<Deliveries> { deliveries ->
                            if (deliveries != null) {
                                val headerBatches = mutableListOf<HeaderBatches>()
                                val headerItems = mutableListOf<HeaderItems>()
                                for (i in deliveries.deliveryItem.indices) {
                                    val headerItem =
                                        HeaderItems(
                                            salesOrg = selectedFillUpItem!!.salesOrg!!,
                                            distChannel = batchScanViewModel.driver?.value!!.distChannel,
                                            exdoc = deliveries.deliveryItem[i].delivery,
                                            itemno = deliveries.deliveryItem[i].itemNo,
                                            materialno = deliveries.deliveryItem[i].materialNo,
                                            isSellable = "",
                                            requestedQuantity = deliveries.deliveryItem[i].quantity,
                                            countedQuantity = 0.0,
                                            materialDescription = batchScanViewModel.mRepository.getMaterialByMaterialNo(
                                                deliveries.deliveryItem[i].materialNo
                                            ).materialDescrption!!,
                                            materialDescriptionArabic = batchScanViewModel.mRepository.getMaterialByMaterialNo(
                                                deliveries.deliveryItem[i].materialNo
                                            ).materialArabic!!,
                                            uom = deliveries.deliveryItem[i].unit,
                                            totalvalue = 0.0,
                                            currency = "",
                                            customizeitem = "",
                                            returnreason = "",
                                            barcodeList = batchScanViewModel.convertUnit.getMaterialsBarcode(deliveries.deliveryItem[i].materialNo),
                                            price = 0.0,
                                            discount = 0.0,
                                            itemCategory = MATERIAL_DELIVERIES,
                                            totalPrice = 0.0,
                                            storageLocation = deliveries.deliveryItem[i].storageLocation,
                                            plant = deliveries.deliveryItem[i].plant
                                        )
                                    headerItems.add(headerItem)
                                }
                                for (i in deliveries.deliveryBatch.indices) {
                                    val headerBatch = HeaderBatches(
                                        exdoc = deliveries.deliveryBatch[i].delivery,
                                        itemno = deliveries.deliveryBatch[i].itemNo,
                                        batch = deliveries.deliveryBatch[i].batch,
                                        materialno = deliveries.deliveryBatch[i].materialNo,
                                        parentBaseRequestedQuantity = deliveries.deliveryBatch[i].quantity,
                                        baseRequestedQuantity = deliveries.deliveryBatch[i].quantity,
                                        baseUnit = deliveries.deliveryBatch[i].unit,
                                        baseCounted = deliveries.deliveryItem[i].quantity,
                                        requestedQuantity = deliveries.deliveryBatch[i].quantity,
                                        countedQuantity = 0.0,
                                        expiryDate = "",
                                        uom = deliveries.deliveryBatch[i].unit,
                                        bType = MATERIAL_DELIVERIES,
                                        customizebatch = "",
                                        storageLocation = deliveries.deliveryBatch[i].storageLocation,
                                        plant = deliveries.deliveryBatch[i].plant
                                    )
                                    headerBatches.add(headerBatch)
                                }
                                if (secondInvoice.isNotEmpty() && secondInvoice != "") {
                                    invoiceNumber += "$secondInvoice"
                                }
                                salesDocViewModel.saveDocument(
                                    exInvoiceNumber = exInvoiceNumber,
                                    mCurrentLocation = null,
                                    iSalesDocHeader = iSalesDocHeader!!,
                                    headerItems = headerItems,
                                    headerBatches = headerBatches,
                                    erpInvoiceNumber = invoiceNumber,
                                    status = DocumentsEnum.POSTED
                                )
                                    AppUtils.dialogWithOkButton(
                                        message = this.stringText(R.string.document_created)
                                                + " " + invoiceNumber + secondInvoice,
                                        activity = this,
                                        context = this,
                                        finishActivity = false,
                                        toActivity = toActionActivity,
                                        isBatchScanActivity = true,
                                        isApproval = isApproval
                                    )
                            }else{
                                fillUpViewModel.getDeliveryCheckRemote(DeliveriesBody("${iSalesDocHeader?.exdoc}"))
                            }
                        }
                        fillUpViewModel.deliveriesLiveData.observe(this, observer)

                    }else {
                        AppUtils.dialogWithOkButton(
                            message = this.stringText(R.string.document_created)
                                    + " " + invoiceNumber + secondInvoice,
                            activity = this,
                            context = this,
                            finishActivity = false,
                            toActivity = toActionActivity,
                            isBatchScanActivity = true,
                            isApproval = isApproval
                        )
                    }
                }
            }
            if (secondInvoice.isNotEmpty() && secondInvoice != "") {
                invoiceNumber += "$secondInvoice"
            }
            saveDocument(iSalesDocHeader!!.exdoc, invoiceNumber, DocumentsEnum.POSTED)
            documentCreatedSuccessfully()


        }
        else if (data.data[0].messagetype == "E") {
//            this.displayToast("${R.string.post_failed} : ${data.data[0].message1}")
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.error))
            if(data.data[0].message1.length < data.data[0].message2.length)
                builder.setMessage("${data.data[0].message2}")
            else
                builder.setMessage("${data.data[0].message1}")
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        else{
            Toast.makeText(this,"Failed to Post",Toast.LENGTH_LONG).show()
        }
    }

    override fun renderCreateDocument(
        salesDocBody: SalesDocBody,
        data: BaseResponse<PostDocumentResponse>, name : String, number : String
    ) {

    }

    override fun renderCreateDocument(
        data: BaseResponse<PostDocumentResponse>,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>
    ) {
    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {
        showOkDialog(this,"network error")
//        Toast.makeText(this,getString(R.string.network_error),Toast.LENGTH_LONG).show()
    }

    override fun renderUnknownError() {
        showOkDialog(this,"network error")
//        Toast.makeText(this,"Unknown Error",Toast.LENGTH_LONG).show()
    }

    override fun renderEmptyResponse() {
        showOkDialog(this,"network error")
//        Toast.makeText(this,"No Response",Toast.LENGTH_LONG).show()
    }

    override fun renderCreateDocumentError() {
        iSalesDocHeader?.let {
            startService(Intent(this, ConnectionService::class.java))
            saveDocument(it.exdoc, "", DocumentsEnum.NOT)
            documentCreatedSuccessfully()
        }
//        this.displayToast("Post Failed")

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(applicationSettingsViewModel.sharedPreferences.getLanguage()!=null)
            LocaleHelper.setLocale(this, applicationSettingsViewModel.sharedPreferences.getLanguage()!!.toLowerCase()) // the code for setting your local
    }

    private fun showOkDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Network failed")
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}