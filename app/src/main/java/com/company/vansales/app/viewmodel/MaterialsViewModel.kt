package com.company.vansales.app.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.activity.MaterialSelectionActivity
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.CustomizeCondition
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.MaterialGroup
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.datamodel.models.localmodels.ShowAddModel
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.CancelDocumentRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.PayersRequestModel
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import com.company.vansales.app.datamodel.repository.CancelDocumentRepositoryImpl
import com.company.vansales.app.datamodel.repository.CreateDocumentRepositoryImpl
import com.company.vansales.app.datamodel.repository.CustomerRepository
import com.company.vansales.app.datamodel.repository.ItemPriceConditionRepository
import com.company.vansales.app.datamodel.repository.MaterialsRepository
import com.company.vansales.app.datamodel.repository.PayersRepositoryImpl
import com.company.vansales.app.datamodel.repository.PricesRepository
import com.company.vansales.app.datamodel.repository.SalesDocRepository
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.ItemPriceConditionDAO
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.services.api.CancelDocumentGateway
import com.company.vansales.app.datamodel.services.api.CreateDocumentGateway
import com.company.vansales.app.datamodel.services.api.PayersGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.CancelDocumentUseCases
import com.company.vansales.app.domain.usecases.CreateDocumentUseCases
import com.company.vansales.app.domain.usecases.PayersUseCases
import com.company.vansales.app.framework.CancelDocumentUseCaseImpl
import com.company.vansales.app.framework.CreateDocumentUseCaseImpl
import com.company.vansales.app.framework.PayersUseCaseImpl
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.AppUtils.round
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.Constants.BATCH_RETURN_DAMAGED
import com.company.vansales.app.utils.Constants.BATCH_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_FREE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_DAMAGED
import com.company.vansales.app.utils.Constants.MATERIAL_LIST_ITEM_TYPE
import com.company.vansales.app.utils.Constants.MATERIAL_SELLABLE
import com.company.vansales.app.utils.Constants.conditionFOC
import com.company.vansales.app.utils.Constants.conditionFixedAmount
import com.company.vansales.app.utils.Constants.conditionPercentage
import com.company.vansales.app.utils.Constants.conditionQty
import com.company.vansales.app.utils.DocumentsConstants.CANCEL_INVOICE
import com.company.vansales.app.utils.DocumentsConstants.FOC_INVOICE
import com.company.vansales.app.utils.DocumentsConstants.Settlment_doc
import com.company.vansales.app.utils.PricingCalculations
import com.company.vansales.app.utils.PricingUtils
import com.company.vansales.app.utils.TaxationUtils
import com.company.vansales.app.view.entities.CancelDocumentViewState
import com.company.vansales.app.view.entities.CreateDocumentViewState
import com.company.vansales.app.view.entities.PayersViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class MaterialsViewModel @Inject constructor(
    val createDocumentGateway: CreateDocumentGateway,
    val createPayerGateway: PayersGateway,
    val cancelDocumentGateway: CancelDocumentGateway

    ) :
    ViewModel() {

    private val mRepository = MaterialsRepository(application)
    private val salesDocRepo = SalesDocRepository(application)
    private val applicationConfig = ApplicationSettingsRepository(application)
    val customerRepository: CustomerRepository = CustomerRepository(application)
    var onTruckMaterialsList = ArrayList<MaterialsTruckItem>()
    var selectedMaterialsList = ArrayList<MaterialsTruckItem>()
    var isUpdatePrice = false
    var freeMaterialsList = ArrayList<MaterialsTruckItem>()
    var selectedFreeMaterialsList = ArrayList<MaterialsTruckItem>()
    var showShowAddData: MutableLiveData<ShowAddModel> = MutableLiveData()
    var itemGroupList = ArrayList<String>()
    private var visitItemNo = ""
    private var visitListID = ""
    var totalMaterialDiscount = 0.0
    private var totalSoldDiscount = 0.0
    private var pricingCalculations = PricingCalculations()
    var selectedItemTotalPrice: BigDecimal = BigDecimal.ZERO
    var selectedFreeTotalPrice: BigDecimal = BigDecimal.ZERO
    var creditLimit: BigDecimal = BigDecimal.ZERO
    var totalFOC = 0.0
    var ifFOCAvailable = false
    var isInvoice = false
    var isOrder = false
    var isOffload = false
    var isCredit = false
    var isFOC = false
    var isExchange = false
    var isReturn = false
    var isLoad = false
    var isTopUp = false
    var isFree = false
    var isROffload = false
    var isStockCount = false
    var listFromMaterials = true
    var currentMaterialList = ""
    var exceedFOCMessage = ""
    var exceedSoldMessage = ""
    var exceedCreditMessage = ""
    val statusMessage = MutableLiveData<String>()
    var currentCustomer: Customer? = null
    var currentDriverId: String? = null
    lateinit var currentVisit: Visits
    lateinit var routes: Routes
    lateinit var truckManagementRepository: TruckManagementRepository
    lateinit var postResponse: MutableLiveData<PostDocumentResponse>
    var headerDiscount = 0.0.toBigDecimal()
    val viewStateCancelDocument: MutableLiveData<CancelDocumentViewState> = MutableLiveData()
    val viewStateCreateDocument: MutableLiveData<CreateDocumentViewState> = MutableLiveData()
    private val cancelDocumentUseCase = cancelDocumentUseCase()
    private val createDocumentUseCase = createDocumentUseCase()
    private val pricesRepo: PricesRepository = PricesRepository(application)
    var sharedPreference = GetSharedPreferences(application)
    val pricingUtils : PricingUtils
    val taxationUtils : TaxationUtils
    val itemPriceConditionDAO: ItemPriceConditionDAO
    val materialsDAO: MaterialsDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private val mRepository2: ItemPriceConditionRepository = ItemPriceConditionRepository(application)
    val viewStateCreatePayer: MutableLiveData<PayersViewState> = MutableLiveData()
    private val createPayerUseCase = createPayerUseCase()
    var exchangeTotalPriceAfterDiscount = 0.0.toBigDecimal()
    var soldDiscounts = "0.0".toBigDecimal()
    var listOfMaterialGroupItems: MutableMap<String, CustomizeCondition> = mutableMapOf()
    var totalPriceAfterHeaderDiscount = ""

    //Exchange Variables
    var totalReturns = "0.0".toBigDecimal()
    var totalSold = "0.0".toBigDecimal()
    var totalInvoiceExchange = "0.0".toBigDecimal()


    init {
        itemPriceConditionDAO = appDB.getItemPriceConditions()
        materialsDAO = appDB.getMaterials()
        pricingUtils = PricingUtils(application)
        taxationUtils = TaxationUtils(application)
    }

    val salesDocRepository: SalesDocRepository =
        SalesDocRepository(application)

    fun getMaterialNameByMaterialNo(materialNo: String): Materials {
        return mRepository.getMaterialByMaterialNo(materialNo)
    }

    fun getInvoiceHeader(ofr : String ) : InvoiceHeader {
        return salesDocRepo.getInvoiceHeader(ofr)
    }
    fun getInvoiceHeaderByErp(erpNumber : String ) : InvoiceHeader {
        return salesDocRepo.getInvoiceHeaderByErp(erpNumber)
    }

    fun addHeader(invoiceHeader : InvoiceHeader){
        salesDocRepo.addHeader(invoiceHeader)
    }

    fun addInvoiceNumber(invoiceNumber : String , route : String){
        salesDocRepo.addInvoiceNumber(invoiceNumber , route)

    }

    fun getTaxesByMaterialNo(customerNumber : String, materialNo : String
                             , distChannel : String) : Materials{
        Log.d("getMaterialTaxes"  , "$distChannel , $materialNo")
        return mRepository.getTaxesByCustomerAndMaterialNo(customerNumber = customerNumber
            , materialNo = materialNo,distChannel = distChannel)
    }

    fun getTaxesToCustomerNoAndMaterialNo(customerCode : String, materialCode : String
                                          ,condition : String) : Taxes {
        Log.d("getMaterialTaxes", "$customerCode, $materialCode, $condition")
        return mRepository.getTaxesToCustomerAndMaterialNo(customerCode = customerCode
            , materialCode = materialCode,condition = condition)
    }

    private fun cancelDocument(exInvoiceNumber: String, sapDoc : String,invoiceType:String? = null){
        salesDocRepository.cancelSalesDocHeaderStatus(exInvoiceNumber,sapDoc)
        if(invoiceType == FOC_INVOICE){
            salesDocRepository.cancelInvoiceHeader(exInvoiceNumber, FOC_INVOICE)
        }else {
            salesDocRepository.cancelInvoiceHeader(
                exInvoiceNumber,
                CANCEL_INVOICE
            )
        }
        }


    fun getDriverCreditLimit(): BigDecimal? {
        return currentDriverId?.let { customerRepository.getDriverCreditLimit(it) }
    }

    fun convertFromBase(materialNumber: String, ToUnit: String, Quantity: Double): Double {
        return mRepository.convertFromBase(materialNumber, ToUnit, Quantity)
    }

    fun convertToBase(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        Log.d("getConvertToBase" ,"$materialNumber , $FromUnit , $Quantity")
        return mRepository.convertToBase(materialNumber, FromUnit, Quantity)
    }

    fun convertToBaseWithSingleItemPrice(materialNumber: String, customer : Customer?,fromUnit: String , quantity : Double): Double {
        return mRepository.convertToBaseWithSingleItemPrice(materialNumber,customer, fromUnit , quantity)
    }

    fun convertToBaseWithSingleItemPriceNothingEquals(materialNumber: String, customer : Customer?,fromUnit: String , quantity : Double): Double {
        return mRepository.convertToBaseWithSingleItemPriceNothingEquals(materialNumber,customer, fromUnit , quantity)
    }

    fun getCustomer(): Customer? {
        return mRepository.getCustomer()
    }

    fun getDriver(): Customer? {
        return mRepository.getDriver()
    }

    fun getCurrentActiveVisit(): Visits? {
        val visit = mRepository.getCurrentActiveVisit()!!
        visitListID = visit.visitListID
        visitItemNo = visit.visitItemNo.toString()
        return visit
    }

    fun finishVisit() {
        mRepository.finishVisit(visitListID, visitItemNo)
    }

    fun getRoute(routeID: String): Routes {
        return mRepository.getRoute(routeID)
    }

    fun getMaterialsList(): LiveData<List<MaterialsTruckItem>> {
        return if (listFromMaterials)
            mRepository.getListFromMaterials()
        else
            mRepository.getListFromTruckItem()
    }

    fun createListFromMaterials(salesOrg: String, distChannel: String) {
        listFromMaterials = true
        mRepository.getAllMaterials(salesOrg, distChannel)
        mRepository.createListFromMaterials(currentCustomer?.customer)
    }

    fun createListFromTruckItem() {
        listFromMaterials = false
        if (isROffload)
            mRepository.createListFromTruckItem(
                currentCustomer?.customer,
                currentCustomer?.salesOrg,
                currentCustomer?.distChannel,
                MATERIAL_DAMAGED
            )
        else
            mRepository.createListFromTruckItem(
                currentCustomer?.customer,
                currentCustomer?.salesOrg,
                currentCustomer?.distChannel,
                MATERIAL_SELLABLE
            )
    }

    fun getMaterialTotalPrice(material : MaterialsTruckItem) : Double{
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        return pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            material.selectedUnit,material.selectedQuantity)
    }

    fun getMaterialBasePrice(material : MaterialsTruckItem) : Double{
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        return pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            material.baseUnit,1.0)
    }

    fun createMissingList() {
        var materialObj: MaterialsTruckItem
        var itemNo = 1
        selectedFreeMaterialsList.clear()
        for (material in onTruckMaterialsList) {
            if (material.selectedQuantity < material.available) {
                materialObj = MaterialsTruckItem()
                materialObj.cloneMaterialsTruckItem(material,material.available - material.selectedQuantity)
                materialObj.selectedQuantity = materialObj.available
                materialObj.itemNo = itemNo
                itemNo++
                selectedFreeMaterialsList.add(materialObj)
            }
        }}


    fun removeMaterial(material: MaterialsTruckItem) {
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            material.selectedUnit,material.selectedQuantity)
        Log.d("getRemove", "${onTruckMaterialsList[material.position].selectedQuantity} " +
                "${material.selectedQuantity} * $convertResult")
        onTruckMaterialsList[material.position].selectedQuantity -= material.selectedQuantity * convertResult
        onTruckMaterialsList[material.position].unitQuantityList[material.selectedUnit] = 0.0
        onTruckMaterialsList[material.position].unitQuantityList.forEach {
            Log.d("getOnTruckRemove", "${it.key} ${it.value}")
        }
        Log.d("getRemoveAfter", "${material.selectedQuantity} ," +
                " ${onTruckMaterialsList[material.position].selectedQuantity} , " +
                "${onTruckMaterialsList[material.position].unitQuantityList[material.selectedUnit]}")
        selectedMaterialsList.forEach {
            if(it.materialNo == material.materialNo && it.selectedUnit == material.selectedUnit){
                it.selectedQuantity = 0.0
                it.unitQuantityList[it.selectedUnit] = 0.0
            }
        }
    }

    fun removeFreeMaterial(material: MaterialsTruckItem) {
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            material.selectedUnit,material.selectedQuantity)
        onTruckMaterialsList[material.position].selectedFreeQuantity -= material.selectedQuantity * convertResult
        freeMaterialsList[material.freePosition].selectedQuantity -= material.selectedQuantity * convertResult
        freeMaterialsList[material.freePosition].unitQuantityList[material.selectedUnit] = 0.0

        selectedFreeMaterialsList.forEach {
            if(it.materialNo == material.materialNo && it.selectedUnit == material.selectedUnit){
                it.selectedQuantity = 0.0
                it.unitQuantityList[it.selectedUnit] = 0.0
            }
        }
    }

    fun getTotalInvoicePrice(): String {
        return convertToEnglish(round(selectedItemTotalPrice - totalMaterialDiscount.toBigDecimal()).toString()).toString()
    }

    fun getExchangePrice(headerReturnsDiscount: BigDecimal, isSold: Boolean): BigDecimal {
        var isNegative = false
        totalInvoiceExchange = "0.0".toBigDecimal()
        var totalInvoiceExchangeWithNoDiscounts = BigDecimal.ZERO
        if (isExchange) {
            if (selectedMaterialsList.size != 0 && !isSold) {
                totalReturns = selectedItemTotalPrice - (round(totalMaterialDiscount.toBigDecimal()) /*+ round(headerReturnsDiscount)*/)
                Log.d("totalReturns" , " $totalReturns  = $selectedItemTotalPrice - ${round(totalMaterialDiscount)}" +
                        " + ${round(headerReturnsDiscount)}")
            }
            if (selectedFreeMaterialsList.size != 0 && isSold) {
                totalSold = selectedFreeTotalPrice - /*(round(headerReturnsDiscount)+*/  round(totalSoldDiscount.toBigDecimal())
                Log.d("totalSold" , " $totalSold  = $selectedFreeTotalPrice - ${round(headerReturnsDiscount)}" +
                        " + ${round(totalSoldDiscount)}")
            }
            Log.d("getExchangeTotalPrice" , "$selectedFreeTotalPrice , $selectedItemTotalPrice $totalSold , $totalReturns , $headerReturnsDiscount  ,$totalMaterialDiscount")
            totalInvoiceExchange = totalSold - totalReturns
            totalInvoiceExchangeWithNoDiscounts =  selectedFreeTotalPrice - selectedItemTotalPrice
            Log.d("getExchangeFinalPrice" , "$totalInvoiceExchange : $totalSold = $selectedFreeTotalPrice - $totalSoldDiscount ," +
                    " $totalReturns = $selectedItemTotalPrice  - $totalMaterialDiscount")

        }
        if(totalSold > BigDecimal.ZERO && totalReturns > BigDecimal.ZERO) {
            if (totalInvoiceExchangeWithNoDiscounts < BigDecimal.ZERO) {
                totalInvoiceExchangeWithNoDiscounts *= -1.0.toBigDecimal()
                isNegative = true
            }
            applyDiscountForExchange(totalInvoiceExchangeWithNoDiscounts , isNegative)
        }else
            MaterialSelectionActivity.instance?.binding?.totalPrice = round(totalInvoiceExchange).toString()
        exchangeTotalPriceAfterDiscount = totalInvoiceExchange
        return totalInvoiceExchange - exchangeHeaderDiscount
    }

    fun getTotalPriceDialog(): String {
        var total = ""
        if (currentMaterialList == "Item")
            total = getTotalInvoicePrice()
        else if (currentMaterialList == "Free")
            total = if(isExchange)
                round(selectedFreeTotalPrice - totalSoldDiscount.toBigDecimal()).toString()
            else
                round(selectedFreeTotalPrice).toString()

        return convertToEnglish(total).toString()
    }

    @SuppressLint("SuspiciousIndentation")
    fun getTotalMaterialGroupPrice(): Double {
        var total = 0.0
        listOfMaterialGroupItems.forEach {
            if(it.value.materialByUnit.isNotEmpty())
            total += it.value.selectedPrice
        }
        return total
    }

    fun addSelectedMaterial(material: MaterialsTruckItem, materialGrp: String?) {
        if (currentMaterialList == "Item") {
            addSelectedMaterialList(material)
            addSelectedMaterialList(material)
            finishSelectedItemList()
        } else if (currentMaterialList == "Free") {
            addSelectedFreeMaterialList(material,materialGrp)
            addSelectedFreeMaterialList(material,materialGrp)
            finishSelectedFreeList(materialGrp)
        }
    }

    private fun addSelectedMaterialList(material: MaterialsTruckItem) {
        var isFound = false
        if (selectedMaterialsList.isEmpty()) {
            val selectedQuantity =
                convertFromBase(
                    material.materialNo,
                    material.selectedUnit,
                    material.selectedQuantity
                )
            val selectedMaterialObj = MaterialsTruckItem()
            selectedMaterialObj.cloneMaterialsTruckItem(material, selectedQuantity)
            selectedMaterialObj.selectedUnit = material.selectedUnit
            selectedMaterialObj.unitPrice = material.unitPrice
        }

        for (selectedItem in selectedMaterialsList) {
            if (material.materialNo == selectedItem.materialNo &&
                material.selectedUnit == selectedItem.selectedUnit
            ) {
                isFound = true
                editSelectedMaterial(material, selectedItem,null)
                break
            }
        }
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            material.selectedUnit,material.selectedQuantity)
        val price = convertResult.toBigDecimal()
        val materialTaxation = taxationUtils.getTaxes(currentCustomer!! ,
            material.materialNo , price)

    /*    var getMaterialTax = getTaxesByMaterialNo(currentCustomer?.customer?:"CMS1577"
            ,material.materialNo ,
            currentCustomer?.distChannel?:"")
        var getCustomerTax = getTaxesByCustomer(currentCustomer?.customer?:"")
        Log.d("convertToBaseFromDB", " addSelectedMaterialList ${material.selectedQuantity}")

        if(getCustomerTax.yexcTax == null || getCustomerTax.yexcTax!!.isEmpty())
            getCustomerTax.yexcTax = "0"
        if(getCustomerTax.yvatTax == null || getCustomerTax.yvatTax!!.isEmpty())
            getCustomerTax.yvatTax = "0"
        if(getMaterialTax.yvatTax == null || getMaterialTax.yvatTax!!.isEmpty())
            getMaterialTax.yvatTax = "0"
        if(getMaterialTax.yexcTax == null || getMaterialTax.yexcTax!!.isEmpty())
            getMaterialTax.yexcTax = "0"

        Log.d("getYVat" , "${getCustomerTax.yvatTax} , ${getMaterialTax.yvatTax}")
        Log.d("getYYEXC" , "${getCustomerTax.yexcTax} , ${getMaterialTax.yexcTax}")
        val yVat = getTaxesToCustomerNoAndMaterialNo(getCustomerTax.yvatTax?:"0"
            ,getMaterialTax.yvatTax?:"0"
            ,"YVAT")
        val yExc = getTaxesToCustomerNoAndMaterialNo(getCustomerTax.yexcTax?:"0"
            ,getMaterialTax.yexcTax?:"0"
            ,"YEXC")
        var yVatValue = 0.0
        var yExcValue = 0.0
        Log.d("getValueOfTaxes", "$getCustomerTax , $getMaterialTax")
        Log.d("getValueVat","$yExcValue , ${price} $yExc $yVat")
        if(yExc != null) {
            if (yExc.condUnit == Constants.conditionPercentage) {
                yExcValue = pricingCalculations.calculatePercentageValue(
                    yExc.condValue?.toDouble()!!,
                    price
                ).toDouble()
                Log.d("getValueExc", "$yExcValue , ${price}")
            }
        }
        if(yVat != null) {
            if (yVat.condUnit == Constants.conditionPercentage) {
                yVatValue = pricingCalculations.calculatePercentageValue(
                    yVat.condValue?.toDouble()!!,
                    price
                ).toDouble()
                Log.d("getValueVat", "$yVatValue , ${price}")
            }
        }*/
        material.yVatTaxes = materialTaxation.first.toDouble()
        material.yExcTaxes = materialTaxation.second.toDouble()
        material.totalTaxes = materialTaxation.first.toDouble() + materialTaxation.second.toDouble()
        val selectedQuantity =
            convertFromBase(material.materialNo, material.selectedUnit, material.selectedQuantity)
        val selectedMaterialObj = MaterialsTruckItem()
        selectedMaterialObj.cloneMaterialsTruckItem(material, selectedQuantity)
//        selectedMaterialObj.selectedQuantity = material.unitQuantityList[material.selectedUnit]!!
        material.unitQuantityList = selectedMaterialObj.unitQuantityList
        material.displayingValue = selectedMaterialObj.displayingValue
        selectedMaterialObj.selectedUnit = material.selectedUnit
//        selectedMaterialObj.totalTaxes = yVatValue + yExcValue
        selectedMaterialObj.position = selectedMaterialsList.size

        if (!isFound && selectedMaterialObj.selectedQuantity > 0) {
            selectedMaterialsList.add(selectedMaterialObj)
        }
        if (!isExchange && !isROffload)
            freeMaterialsList[material.freePosition].available =
                material.available - material.selectedQuantity
    }

    private fun addSelectedFreeMaterialList(material: MaterialsTruckItem,materialGrp : String?) {
        val selectedQuantity =
            convertFromBase(material.materialNo, material.selectedUnit, material.selectedQuantity)
        if (selectedFreeMaterialsList.isEmpty()) {
            val selectedMaterialObj = MaterialsTruckItem()
            selectedMaterialObj.cloneMaterialsTruckItem(material, selectedQuantity)
            selectedMaterialObj.selectedQuantity =
                material.unitQuantityList[material.selectedUnit]!!
            selectedMaterialObj.selectedUnit = material.selectedUnit
//            selectedMaterialObj.displayingValue = material.selectedQuantity
            selectedFreeMaterialsList.add(selectedMaterialObj)
            onTruckMaterialsList[material.position].selectedFreeQuantity = material.selectedQuantity
        }
        var isFound = false
        for (selectedItem in selectedFreeMaterialsList) {
            if (material.materialNo == selectedItem.materialNo &&
                material.selectedUnit == selectedItem.selectedUnit) {
                isFound = true
                editSelectedMaterial(material, selectedItem,materialGrp)
                break
            }
        }
        val selectedMaterialObj = MaterialsTruckItem()
        selectedMaterialObj.cloneMaterialsTruckItem(material, selectedQuantity)
/*        selectedMaterialObj.selectedQuantity =
            material.unitQuantityList[material.selectedUnit]!!*/
        selectedMaterialObj.selectedUnit = material.selectedUnit
        if (!isFound && selectedMaterialObj.selectedQuantity > 0)
            selectedFreeMaterialsList.add(selectedMaterialObj)
        onTruckMaterialsList[material.position].selectedFreeQuantity = material.selectedQuantity

    }

    private fun editSelectedMaterial(
        material: MaterialsTruckItem,
        selectedItem: MaterialsTruckItem , materialGrp : String?
    ) {
        if (material.unitQuantityList[material.selectedUnit]!! > 0) {
            if(listOfMaterialGroupItems[material.materialGrp]!=null) {
                if (listOfMaterialGroupItems[material.materialGrp]?.materialByUnit?.contains(
                        MaterialGroup(material.materialNo,material.selectedUnit)
                    ) == true) {
                    if(materialGrp != null) {
                        if (materialGrp != "All Groups")
                            deleteFromMaterialGroupList(selectedItem)
                    }else{
                        deleteFromMaterialGroupList(selectedItem)
                    }
                }
            }
            Log.d("selectedItemQuan21", "${selectedItem.selectedQuantity } , " +
                    "${            convertFromBase(selectedItem.materialNo,selectedItem.selectedUnit,selectedItem.selectedQuantity)
                    }")

            selectedItem.selectedQuantity = material.unitQuantityList[material.selectedUnit]!!
            selectedItem.unitQuantityList = material.unitQuantityList
            selectedItem.displayingValue = material.displayingValue
            Log.d("selectedItemQuan", "${selectedItem.selectedQuantity } , ${selectedItem.displayingValue}")
            Log.d("getValueOf", "${selectedItem.selectedQuantity } , ${selectedItem.unitQuantityList[material.selectedUnit]}")
            Log.d("getValueOf", "${material.selectedQuantity } , ${material.unitQuantityList[material.selectedUnit]}")
            Log.d("selectedItemQuan33",
                "${material.unitQuantityList[material.selectedUnit] } , ${material.selectedUnit} , ${material.selectedQuantity}")
            val selectedQuantity =
                convertFromBase(
                    material.materialNo,
                    material.selectedUnit,
                    material.selectedQuantity
                )
//            selectedItem.unitQuantityList[material.selectedUnit] = selectedQuantity
            selectedItem.selectedUnit = material.selectedUnit
            Log.d("selectedItemQuan", "${selectedItem.selectedQuantity } , ${selectedItem.displayingValue}")
        } else removeSelectedMaterial(selectedItem)

    }

    private fun removeSelectedMaterial(selectedItem: MaterialsTruckItem) {
        if (currentMaterialList == "Item")
            selectedMaterialsList.remove(selectedItem)
        else if (currentMaterialList == "Free") {
            selectedFreeMaterialsList.remove(selectedItem)
            deleteFromMaterialGroupList(selectedItem)
        }
    }

    fun setGroupList() {
        itemGroupList.clear()
        for (material in onTruckMaterialsList) {
            if (!(itemGroupList.contains(material.materialGrp))) {
                itemGroupList.add(material.materialGrp)
            }
        }
    }
    fun setGroupListFromFreeList() {
        itemGroupList.clear()
        for (material in freeMaterialsList) {
            if (!(itemGroupList.contains(material.materialGrp))) {
                itemGroupList.add(material.materialGrp)
            }
        }
    }

    fun setFreeMaterialsList() {
        var freeMaterial: MaterialsTruckItem
        freeMaterialsList.clear()
        for (material in onTruckMaterialsList) {
            if (material.isFree == MATERIAL_CATEGORY_FREE) {
                freeMaterial = MaterialsTruckItem()
                freeMaterial.cloneMaterialsTruckItem(material)
                freeMaterialsList.add(freeMaterial)
            }
        }
    }

    fun getMaterialPriceSelectedQuantity(materials: MaterialsTruckItem): BigDecimal {
        var isMaterialBaseUnitTheSame = currentCustomer?.let {
            mRepository.isMaterialBaseUnitTheSame(materials.materialNo, materials.selectedUnit ,
                it
            )
        }
        var convertResult : Double
        var totalValue : Double
        if(isMaterialBaseUnitTheSame == 1) {
            convertResult = convertToBaseWithSingleItemPrice(
                materials.materialNo,
                currentCustomer, materials.selectedUnit , materials.selectedQuantity
            )
            totalValue = materials.unitPrice * convertResult
        }else if (isMaterialBaseUnitTheSame == 2){
            convertResult = convertToBaseWithoutRounding(materials.materialNo, materials.selectedUnit, 1.0)
            totalValue = convertResult
        }else if(isMaterialBaseUnitTheSame == 4){
            convertResult = convertToBaseWithSingleItemPriceNothingEquals(
                materials.materialNo,
                currentCustomer, materials.selectedUnit , materials.selectedQuantity
            )
        } else{
            convertResult = convertToBaseWithSingleItemPrice(
                materials.materialNo,
                currentCustomer, materials.selectedUnit , materials.selectedQuantity
            )
        }
        Log.d("getFinalTotalPrice", "${(convertResult * materials.selectedQuantity).toBigDecimal()}")
        return (convertResult * materials.selectedQuantity).toBigDecimal()/*
        tmpQuantity = if(!checkBaseUnit(materials.materialNo,materials.selectedUnit))
            convertToBase(materials.materialNo, materials.selectedUnit, materials.selectedQuantity)
        else
            convertToBaseWithoutRounding(materials.materialNo, materials.selectedUnit, materials.selectedQuantity)*/
        Log.d("getSelectedUnit", "${materials.selectedUnit} , ${materials.selectedQuantity} , " +
                "${convertToBase(materials.materialNo, materials.selectedUnit, materials.selectedQuantity)} ," +
                "${materials.unitPrice} ,  ${round((materials.unitPrice ).toBigDecimal())}")
//        return round((materials.unitPrice ).toBigDecimal())
    }

    fun getMaterialPriceSelectedQuantity(materials: MaterialsTruckItem , selectedQuantity : Double): BigDecimal {
        if(convertMaterialUnit(materials.materialNo,materials.selectedUnit,0.0) != null)
            materials.selectedUnit = convertMaterialUnit(materials.materialNo,materials.selectedUnit,0.0)!!
        val tmpQuantity =
            convertToBase(materials.materialNo, materials.selectedUnit, materials.selectedQuantity)
        Log.d("getSelectedUnitIsTrue", "${materials.selectedUnit} , ${materials.selectedQuantity} , " +
                "${convertToBase(materials.materialNo, materials.selectedUnit, materials.selectedQuantity)} ," +
                "${materials.unitPrice} , $tmpQuantity ${round((materials.unitPrice * tmpQuantity).toBigDecimal())} , " +
                "${convertMaterialUnit(materials.materialNo,materials.selectedUnit,0.0)}")
        return round((materials.unitPrice * tmpQuantity).toBigDecimal())
    }
    fun convertMaterialUnit(materialNumber: String, toUnit: String, quantity: Double): String? {
        return  mRepository.convertMaterialUnit(materialNumber, toUnit , quantity)
    }

    fun getMaterialUnitPrice(material : MaterialsTruckItem, toUnit: String) : Double{
        val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,
            currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        return round(pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            toUnit,1.0))
    }
    // @return ([UnitPrice],[TotalSelectedPrice],[SelectedQuantity],[Available])
    fun getViewChangData(materials: MaterialsTruckItem, unit: String): List<BigDecimal> {
        val dataList = ArrayList<BigDecimal>()
        Log.d("onViewChangeData", "" +
                "${materials.materialNo} , ${materials.selectedUnit} , $unit  , ${materials.unitPrice} , " +
                "${materials.selectedQuantity}")
        val materialFromDB = materialsDAO.getMaterialByPrimary(materials.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
        var convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
            unit,materials.selectedQuantity)
        var convertedQuantity = 0.0
        var primaryQuantity = 0.0
        var convert = 0.0
   /*     materials.unitQuantityList.forEach { quantity ->

            convert += convertToBase(materials.materialNo , quantity.key, 1.0)
            if(quantity.value > 0.0){
                if(quantity.key != materials.baseUnit)
                convertedQuantity = convertToBaseWithoutRounding(
                    materials.materialNo,
                    quantity.key,
                    materials.baseUnit,
                    quantity.value
                )
                else
                    primaryQuantity = quantity.value
            }
        }*/
        var tmpQuantity = 0.0
        for ((unit, quantity) in materials.unitQuantityList) {
            Log.d(
                "calcSelectedQuantity", " ${materials.materialNo} , " +
                        "${materials.selectedUnit} , ${materials.unitPrice} , ${materials.selectedQuantity} , " +
                        "$unit $quantity , base Unit : ${materials.baseUnit}"
            )
            var convertedQuantity = 0.0
            if(quantity > 0.0)
                convertedQuantity = convertToBaseWithoutRounding(
                    materials.materialNo,
                    unit,
                    materials.baseUnit,
                    quantity
                )
            tmpQuantity += round(convertedQuantity)
        }
        Log.d("checkMatUnitPrice", "${materials.materialNo} , ${materials.unitPrice} , ${unit} " +
                "$convertResult ${materials.selectedQuantity} $primaryQuantity $convertedQuantity $convert")
  /*      if(!checkOnTruckMaterialsList(materials) && unit == "EA"){
            checkMaterialCondValue(materials)
            Log.d("checkMatUnitPrice", "${materials.unitPrice}")
        }*/
            //TODO check the each and pack selection Price is with KAR
        dataList.add(round((convertResult).toBigDecimal()))
        dataList.add(round((convertResult).toBigDecimal()))
        Log.d("getMaterialPriceIn", "${materials.materialNo}" +
                " ${materials.unitPrice} , ${materials.selectedQuantity}")
        convertResult = convertFromBase(materials.materialNo, unit, 1.0)
        Log.d("getMaterialPriceIn", "${materials.materialNo}" +
                " ${materials.unitPrice} , ${materials.selectedQuantity}")
        dataList.add(round((tmpQuantity * convertResult)).toBigDecimal())
        dataList.add(round(((materials.available - materials.selectedFreeQuantity) * convertResult).toBigDecimal()))
        return dataList
    }

    var totalItemFOC = 0.0
    fun finishSelectedItemList() {

        val tmpList = ArrayList<Pair<String, String>>()
        var i = 1
        totalMaterialDiscount = 0.0
        totalFOC = 0.0
        totalItemFOC = 0.0
        selectedItemTotalPrice = BigDecimal.ZERO
        ifFOCAvailable = false
        applyDiscount()
        for (material in selectedMaterialsList) {
            if (!tmpList.contains(Pair(material.materialNo , material.selectedUnit))) {
                setBenefitsList(material)
                totalMaterialDiscount += material.discountValue
                totalItemFOC += material.FOCValue
                material.discountValue = material.discountValue
                tmpList.add(Pair(material.materialNo , material.selectedUnit))
            }
            val materialFromDB = materialsDAO.getMaterialByPrimary(material.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
            val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                material.selectedUnit,material.selectedQuantity)
            selectedItemTotalPrice += (round(convertResult.toBigDecimal())/* + material.totalTaxes.toBigDecimal()*/)
            material.itemNo = i
            i++
            Log.d("getMatTotPrice" , "${material.materialNo} : ${selectedItemTotalPrice} , " +
                    " ${material.position} ${material.selectedQuantity} , Discount ${material.discountValue} " +
                    " , ${convertResult.toBigDecimal()}")
        }
        Log.d("availableFoc", " ${ifFOCAvailable} $totalMaterialDiscount" )
    }

    fun finishSelectedFreeList(materialGrp: String?) {
        Log.d("getExchangeTotalPric2", " finishSelected Free")
        totalSoldDiscount = 0.0
        selectedFreeTotalPrice = BigDecimal.ZERO
        if (!isExchange) {
            listOfMaterialGroupItems[materialGrp]?.selectedPrice = 0.0
            selectedFreeMaterialsList.forEach {
                val materialFromDB = materialsDAO.getMaterialByPrimary(it.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
                var convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                    it.selectedUnit,it.selectedQuantity)
                if (materialGrp == it.materialGrp && listOfMaterialGroupItems[materialGrp] != null &&
                    listOfMaterialGroupItems[it.materialGrp] != null &&
                    (round(listOfMaterialGroupItems[materialGrp]?.totalPrice!! -
                                round(listOfMaterialGroupItems[materialGrp]?.selectedPrice ?: 0.0))
                            >= convertResult)) {
                    it.selectedQuantity = it.unitQuantityList[it.selectedUnit]!!
                    listOfMaterialGroupItems[materialGrp]!!.amount += it.selectedQuantity
                    listOfMaterialGroupItems[materialGrp]!!.selectedPrice += convertResult
                    if(listOfMaterialGroupItems[materialGrp]!!.materialByUnit.contains(MaterialGroup(it.materialNo,it.selectedUnit))){

                    }else {
                        listOfMaterialGroupItems[materialGrp]!!.materialByUnit.add(MaterialGroup(it.materialNo,it.selectedUnit))
                    }
                    } else {
                    if (listOfMaterialGroupItems[it.materialGrp] == null ||
                        (listOfMaterialGroupItems[it.materialGrp]?.materialByUnit?.contains(MaterialGroup(it.materialNo,it.selectedUnit)) == false &&
                        round(listOfMaterialGroupItems[it.materialGrp]?.totalPrice!!.toBigDecimal() -
                        round(listOfMaterialGroupItems[it.materialGrp]?.selectedPrice?.toBigDecimal() ?: BigDecimal.ZERO)).toDouble()
                        <= convertResult)) {
                        setBenefitsList(it,"F")
                        ifFOCAvailable = true
                        it.selectedQuantity = it.unitQuantityList[it.selectedUnit]!!
                        val materialFromDB = materialsDAO.getMaterialByPrimary(it.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
                        val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                            it.selectedUnit,it.selectedQuantity)
                        selectedFreeTotalPrice += (round(convertResult.toBigDecimal()))
                        totalSoldDiscount += it.discountValue
                        applyDiscountForExchange(
                            selectedFreeTotalPrice,
                            selectedFreeMaterialsList,
                            totalSoldDiscount.toBigDecimal()
                        )
                    }
                }
            }
        } else {
                selectedFreeMaterialsList.forEach {
//                    ifFOCAvailable = false
                    setBenefitsList(it , "F")
                    val materialFromDB = materialsDAO.getMaterialByPrimary(it.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
                    val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                        it.selectedUnit,it.selectedQuantity)
                    selectedFreeTotalPrice += (round(convertResult.toBigDecimal()))
                    totalSoldDiscount += it.discountValue

                    Log.d("getSoldPrice" , "${it.materialNo} , $convertResult , " +
                            " , ${it.discountValue} , $selectedFreeTotalPrice ")
                }
                applyDiscountForExchange(
                    selectedFreeTotalPrice,
                    selectedFreeMaterialsList,
                    totalSoldDiscount.toBigDecimal()
                )
            }
    }

    fun finishMissingList() {
        if (isROffload) {
            selectedFreeTotalPrice = BigDecimal.ZERO
            createMissingList()
            for (missingMaterial in selectedFreeMaterialsList) {
                for (material in selectedMaterialsList) {
                    if (material.materialNo == missingMaterial.materialNo)
                        missingMaterial.selectedQuantity -= convertToBase(
                            material.materialNo,
                            material.selectedUnit,
                            material.selectedQuantity
                        )
                }
                val materialFromDB = materialsDAO.getMaterialByPrimary(missingMaterial.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
                val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                    missingMaterial.selectedUnit,round(missingMaterial.selectedQuantity.toBigDecimal()).toDouble())
                missingMaterial.displayingValue = round(missingMaterial.selectedQuantity.toBigDecimal()).toDouble()
                selectedFreeTotalPrice += (round(convertResult.toBigDecimal()))
            }
        }
    }

    private fun setBenefitsList(material: MaterialsTruckItem) {
        material.discountValue = 0.0
        material.FOCValue = 0.0
       /* val price =
            round((material.unitPrice * material.selectedQuantity).toBigDecimal())*/
        //i added filter by isHeader to make sure that not apply header every
        val conditionsList = mRepository.getItemPriceConditions(
            material.materialNo,
            currentCustomer?.customer,
            currentCustomer?.distChannel,
            currentCustomer?.salesOrg
        ).filter { it.isHeader?.isEmpty() == true }
/*        ifFOCAvailable = mRepository.getItemPriceConditionsHeader(
            currentCustomer?.customer,
            currentCustomer?.distChannel,
            currentCustomer?.salesOrg
        ).any { it.isHeader?.isNotEmpty() == true && (it.condUnit == Constants.conditionFOC)}*/
        val pricingUtils = PricingUtils(application)
        val price = round(getMaterialTotalPrice(material).toBigDecimal())
        val quantity = pricingUtils.convertToBaseWithoutRounding(material.materialNo,material.selectedUnit,
        material.baseUnit,material.selectedQuantity)
        Log.d("materialDiscount" , "$price , rounded price : ${round(price)}")
        material.benefitList =
            pricingCalculations.getBenefitList(
                price,
                conditionsList.toMutableList(),
                quantity
            )
        for (benefit in material.benefitList) {
            when (benefit.benefitType) {
                Constants.conditionFOC -> {
                    material.FOCValue += benefit.benefitValue.toDouble()
                    ifFOCAvailable = true
                }
                conditionPercentage, conditionFixedAmount -> {
                    material.discountValue += round(benefit.benefitValue).toDouble()
                    Log.d("benefit22" , "conditionPercentage : ${material.materialNo} ${benefit.benefitValue.toDouble()} , " +
                            "${round(benefit.benefitValue ,
                                3).toDouble()}")
                }
                conditionQty -> {
                    Log.d("benefit22" , "conditionQty : ${material.materialNo} , " +
                            "${material.selectedQuantity} , ${material.selectedUnit} ${benefit.benefitValue.toDouble()} , " +
                            "${round(benefit.benefitValue , 2).toDouble() } $quantity ")
                    material.discountValue += round(round(benefit.benefitValue) * round(quantity.toBigDecimal())).toDouble()
                }
            }
            material.discountValue = round(material.discountValue.toBigDecimal()).toDouble()
            Log.d("asdsajkdjsad", "${material.discountValue} , ${round(material.discountValue.toBigDecimal(),2).toDouble()}")
        }
    }

    private fun setBenefitsList(material: MaterialsTruckItem, free: String) {
        val pricingUtils = PricingUtils(application)
        val quantity = pricingUtils.convertToBaseWithoutRounding(material.materialNo,material.selectedUnit,
            material.baseUnit,material.selectedFreeQuantity)
        material.discountValue = 0.0
        material.FOCValue = 0.0
        var priceFree = getMaterialTotalPrice(material)
        if (free == "F") {
            priceFree =  round(priceFree.toBigDecimal()).toDouble()
        } else {
            material.selectedQuantity = material.selectedFreeQuantity
            priceFree =  round(priceFree.toBigDecimal()).toDouble()
        }
        //i added filter by isHeader to make sure that not apply header every
        val conditionsList = mRepository.getItemPriceConditions(
            material.materialNo,
            currentCustomer?.customer,
            currentCustomer?.distChannel,
            currentCustomer?.salesOrg
        ).filter { it.isHeader?.isEmpty() == true }

        material.benefitList =
            pricingCalculations.getBenefitList(
                priceFree.toBigDecimal(),
                conditionsList.toMutableList(),
                quantity
            )
        for (benefit in material.benefitList) {
            when (benefit.benefitType) {
                conditionFOC -> {
                    material.FOCValue += benefit.benefitValue.toDouble()
                    ifFOCAvailable = true
                }
                conditionPercentage , conditionFixedAmount, conditionQty -> {
                    material.discountValue += round(benefit.benefitValue).toDouble()
                    Log.d("benefitFree" , "conditionPercentage : ${material.materialNo} ${benefit.benefitValue.toDouble()} , " +
                            "${round(benefit.benefitValue ,
                                3).toDouble()}")
                }
                conditionQty -> {
                    Log.d("benefitFree" , "conditionQty : ${material.materialNo} , " +
                            "${material.selectedQuantity} , ${material.selectedUnit} ${benefit.benefitValue.toDouble()} , " +
                            "${round(benefit.benefitValue , 2).toDouble() } $quantity ")
                    material.discountValue += round(round(benefit.benefitValue) * round(quantity.toBigDecimal())).toDouble()
                }
            }
            material.discountValue = round(material.discountValue.toBigDecimal()).toDouble()
        }
    }

    fun getBenefitsListHeader(): List<ItemPriceCondition> {
        return mRepository.getItemPriceConditionsHeader(
            currentCustomer?.customer,
            currentCustomer?.distChannel,
            currentCustomer?.salesOrg
        ).filter {
            it.isHeader?.isNotEmpty() == true
        }
    }

    fun checkNextAction(): String {
        if (!checkCredit())
            return exceedCreditMessage
        if (checkExchange() == 2)
            return exceedSoldMessage
        if (!checkFOC())
            return exceedFOCMessage
        return "true"
    }

    fun getApproval(totalValue: BigDecimal, isSellable: Boolean): String {
        val valueApproval: String = if (isSellable)
            applicationConfig.getAppConfigValueByAppParamter("EXCHANGE_SELLABLE")
        else
            applicationConfig.getAppConfigValueByAppParamter("EXCHANGE_NONSELLABLE")
        return if (totalValue > valueApproval.toBigDecimal())
            "A"
        else ""
    }

    fun getApproval(totalValue: BigDecimal): Boolean {
        val valueApproval: String =
            applicationConfig.getAppConfigValueByAppParamter("RETURN_APPROVAL")

        return totalValue > valueApproval.toBigDecimal()
    }

    private fun checkCredit(): Boolean {
        Log.d("checkIsReturn", "$isReturn")
        return if (isCredit) {
            if (isExchange)
                round(selectedFreeTotalPrice - totalSoldDiscount.toBigDecimal()) <= creditLimit
            else
                round(selectedItemTotalPrice - totalMaterialDiscount.toBigDecimal()) <= creditLimit
        } else true
    }

    fun checkFOC(): Boolean {
        if (currentMaterialList == "Free" && selectedFreeTotalPrice > totalFOC.toBigDecimal() && isFOC)
            return false
        return true
    }
    fun checkGroupFOC(): Boolean {
        var totalPrice = 0.0
        var selectedGroupValue = 0.0
        if (currentMaterialList == "Free" && isFOC) {
            listOfMaterialGroupItems.forEach {
                totalPrice += it.value.totalPrice
                selectedGroupValue += it.value.selectedPrice
            }
            return selectedGroupValue <= totalPrice
        }
        return true
    }

    private val exchangeTolerance = getAppConfigByAppParamter("EXCHANGE_TOLERANCE").toBigDecimal()

    fun getAppConfigByAppParamter(appParamter: String): String {
        return applicationConfig.getAppConfigValueByAppParamter(appParamter)
    }

    fun checkExchange(): Int {

            if (round(totalSold) - round(totalReturns) > exchangeTolerance && isExchange)
            return 1 //Exceeds Tolerance
        else if (totalReturns > totalSold && isExchange)
            return 2 //Can't Perform
        return 3 //Success
    }

    fun setISalesDocHeader(docType: String, exDoc: String): ISalesDocHeader {
        var totalPrice: BigDecimal = if (totalPriceAfterHeaderDiscount == "") {
            when (docType) {
                Settlment_doc -> selectedFreeTotalPrice
                else -> selectedItemTotalPrice - totalMaterialDiscount.toBigDecimal()
            }
        } else {
            when (docType) {
                Settlment_doc -> selectedFreeTotalPrice
                else -> convertToEnglish(totalPriceAfterHeaderDiscount)!!.toBigDecimal()
            }
        }

        return ISalesDocHeader(
            routes.salesOrg!!,
            routes.distChannel!!,
            routes.division!!,
            routes.plant!!,
            "",
            routes.route,
            currentDriverId,
            currentCustomer?.customer,
            docType,
            exDoc,
            null,
            null,
            totalPrice,
            headerDiscount,
            "",
            "",
            "",
            visitListID,
            "",
            visitItemNo ,
            latitude = 0.0.toString(),
            longitude = 0.0.toString()
        )
    }

    fun setAllHeaderItems(
        exDoc: String,
        itemCategory1: String,
        itemCategory2: String
    ): ArrayList<HeaderItems> {

        val itSDItems = ArrayList<HeaderItems>()
        itSDItems.addAll(setHeaderItems(exDoc, itemCategory1, selectedMaterialsList))
        itSDItems.addAll(setHeaderItems(exDoc, itemCategory2, selectedFreeMaterialsList))
        refreshItemNumber(itSDItems)
        return itSDItems
    }

    fun setHeaderItems(
        exDoc: String,
        itemCategory: String,
        selectedMaterialsList: ArrayList<MaterialsTruckItem>
    ): ArrayList<HeaderItems> {
        val itSDItems = ArrayList<HeaderItems>()
        for (material in selectedMaterialsList) {
            val materialFromDB = materialsDAO.getMaterialByPrimary(
                material.materialNo,
                currentCustomer!!.salesOrg,
                currentCustomer!!.distChannel
            )
            val convertResult = pricingUtils.calculatePrice(
                materialFromDB, currentCustomer!!,
                material.selectedUnit, material.selectedQuantity
            )
            var materialUnitPrice = material.unitPrice
            materialUnitPrice = try {
                round(convertResult / material.selectedQuantity)
            } catch (exception: NumberFormatException) {
                Log.d(
                    "numberException", "$exception , " +
                            "${material.unitQuantityList[material.selectedUnit]!!}"
                )
                material.unitPrice

            }
            var totalItemPrice = convertResult
            if (material.mtype == "F") {
            } else {
                totalItemPrice = convertResult - material.discountValue/* + material.totalTaxes*/
            }
            Log.d("materialItem", " ${material.displayingValue} ${material.selectedQuantity}")
            itSDItems.add(
                HeaderItems(
                    material.salesOrg,
                    material.distChannel,
                    exDoc,
                    material.itemNo.toString(),
                    material.materialNo,
                    "",
                    material.selectedQuantity,
                    0.0,
                    material.materialDescrption,
                    material.materialArabic,
                    material.selectedUnit,
                    0.0,
                    currentCustomer?.currency,
                    "",
                    "",
                    material.barcodeList,
                    materialUnitPrice,
                    material.discountValue,
                    itemCategory,
                    round(totalItemPrice.toBigDecimal()).toDouble()
                )
            )
        }
        return itSDItems
    }

    fun setHeaderBatches(
        exDoc: String,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>
    ): ArrayList<HeaderBatches> {
        return when {
            isReturn -> setNewHeaderBatches(exDoc, headerItems)
            isROffload -> setHeaderBatchesAuto(iSalesDocHeader, headerItems)
            else -> setEmptyHeaderBatches(exDoc)
        }
    }

    private fun setNewHeaderBatches(
        exDoc: String,
        headerItems: ArrayList<HeaderItems>
    ): ArrayList<HeaderBatches> {
        val returnHeaderBatches: MutableList<HeaderBatches> = ArrayList()
        for (i in headerItems.indices) {
            val batchNumber: String =
                if (headerItems[i].itemCategory == MATERIAL_CATEGORY_RETURN_SELLABLE) {
                    BATCH_RETURN_SELLABLE
                } else {
                    BATCH_RETURN_DAMAGED
                }
            val headerBatch = HeaderBatches(
                exdoc = exDoc,
                itemno = headerItems[i].itemno,
                batch = batchNumber,
                materialno = headerItems[i].materialno,
                parentBaseRequestedQuantity = headerItems[i].requestedQuantity,
                baseRequestedQuantity = headerItems[i].requestedQuantity,
                baseUnit = headerItems[i].uom,
                baseCounted = headerItems[i].requestedQuantity,
                requestedQuantity = headerItems[i].requestedQuantity,
                countedQuantity = headerItems[i].requestedQuantity,
                expiryDate = AppUtils.expiryDateChecker(""),
                uom = headerItems[i].uom,
                bType = headerItems[i].itemCategory!!,
                customizebatch = "",
                storageLocation = headerItems[i].storageLocation,
                plant = headerItems[i].plant
            )

            returnHeaderBatches.add(headerBatch)

        }

        return returnHeaderBatches as ArrayList
    }

    private fun setHeaderBatchesAuto(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>
    ): ArrayList<HeaderBatches> {
        return truckManagementRepository.initDamagedTruckBatches(iSalesDocHeader, headerItems)!!
    }

    private fun setEmptyHeaderBatches(exDoc: String): ArrayList<HeaderBatches> {
        val itSDBatch = ArrayList<HeaderBatches>()
        itSDBatch.add(
            HeaderBatches(
                exDoc,
                "0",
                "",
                "",
                0.0,
                0.0,
                "",
                0.0,
                0.0,
                0.0,
                "",
                "",
                "",
                "",
                "",
                ""
            )
        )
        return itSDBatch
    }

    fun setSalesDocBody(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>
    ): SalesDocBody {
        val itSDItems = ArrayList<ExternalHeaderItems>()
        val itSDBatch = ArrayList<ExternalHeaderBatches>()

        for (item in headerItems) {
            itSDItems.add(
                ExternalHeaderItems(
                    item.exdoc!!,
                    item.itemno!!,
                    AppUtils.checkCategory(item.itemCategory!!),
                    item.materialno!!,
                    item.requestedQuantity!!,
                    item.uom!!,
                    item.totalvalue!!,
                    item.currency!!,
                    item.customizeitem!!,
                    item.returnreason!!
                )
            )
        }
        for (batch in headerBatches) {
            itSDBatch.add(
                ExternalHeaderBatches(
                    batch.exdoc!!,
                    batch.itemno!!,
                    batch.batch!!,
                    batch.materialno!!,
                    batch.countedQuantity!!,
                    batch.uom!!,
                    batch.customizebatch!!,
                    batch.storageLocation?:"",
                    batch.plant?:""
                )
            )
        }
        return SalesDocBody(iSalesDocHeader, itSDItems, itSDBatch)
    }

    fun setSalesDocBodyForOrderInvoice(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>
    ): SalesDocBody {
        val itSDItems = ArrayList<ExternalHeaderItems>()
        val itSDBatch = ArrayList<ExternalHeaderBatches>()

        for (item in headerItems) {
            itSDItems.add(
                ExternalHeaderItems(
                    item.exdoc!!,
                    item.itemno!!,
                    item.itemCategory!!,
                    item.materialno!!,
                    item.requestedQuantity!!,
                    item.uom!!,
                    item.totalvalue!!,
                    item.currency!!,
                    item.customizeitem!!,
                    item.returnreason!!
                )
            )
        }
        for (batch in headerBatches) {
            itSDBatch.add(
                ExternalHeaderBatches(
                    batch.exdoc!!,
                    batch.itemno!!,
                    batch.batch!!,
                    batch.materialno!!,
                    batch.countedQuantity!!,
                    batch.uom!!,
                    batch.customizebatch!!,
                    batch.storageLocation?:"",
                    batch.plant?:""
                )
            )


        }
        return SalesDocBody(iSalesDocHeader, itSDItems, itSDBatch)
    }

    fun post(mySalesDocBody: SalesDocBody) {
        mRepository.post(mySalesDocBody)
        postResponse = mRepository.postResponse
    }

    fun updateMaterialTruck(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>,
        headerBatches: ArrayList<HeaderBatches>
    ) {
        truckManagementRepository.addItemsToTruck(headerItems, headerBatches, iSalesDocHeader)
    }

    fun refreshItemNumber(headerItems: ArrayList<HeaderItems>) {
        for (i in headerItems.indices) {
            headerItems[i].itemno = (i + 1).toString()
        }
    }


    fun getItemCondition(materialTruckItem : MaterialsTruckItem , materialGroup : String) : BigDecimal{
        val headerList = getBenefitsListHeader().sortedByDescending { it.condUnit }
        var materialDiscountFOC = BigDecimal.ZERO
        Log.d("asdkjsadkjsakdaj","${headerList.size}")
        headerList.forEach {
            /*          Log.d("aksjdaskdjaskdjasdjk", "${it.condUnit} , ${it.scaleUnit} " +
                              ", ${it.condValue} , ${it.condition} , ${it.isHeader} , ${it.condCustiomize}")
                      Log.d("getMaterialSelectedPrice", " ${getMaterialPriceSelectedQuantity(materialTruckItem)}")
                     */
            val materialFromDB = materialsDAO.getMaterialByPrimary(materialTruckItem.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
            var convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                materialTruckItem.selectedUnit,materialTruckItem.selectedQuantity)
            val materialGrp = it.condCustiomize!!.split("/").last()
            if (materialGroup == materialGrp &&
                pricingCalculations.scaleAmount(
                    it,
                    convertResult.toBigDecimal())) {
                materialDiscountFOC = pricingCalculations.calculateFOCValue(
                    it.condValue!!,
                    convertResult.toBigDecimal())
            }
        }
        return materialDiscountFOC
    }

    fun getMaterialFromSelectedItem(material:MaterialsTruckItem ,
                                    currentMaterialList : String? = null) : MaterialsTruckItem {
        if(currentMaterialList != null) {
            if (currentMaterialList == MATERIAL_LIST_ITEM_TYPE) {
                selectedMaterialsList.forEach {
                    return if (material.materialNo == it.materialNo && material.selectedUnit == it.selectedUnit) {
                        it
                    } else {
                        material
                    }
                }
            } else {
                selectedFreeMaterialsList.forEach {
                    return if (material.materialNo == it.materialNo && material.selectedUnit == it.selectedUnit) {
                        it
                    } else {
                        material
                    }
                }
            }
        }else{
            selectedMaterialsList.forEach {
                return if (material.materialNo == it.materialNo && material.selectedUnit == it.selectedUnit) {
                    it
                } else {
                    material
                }
            }
        }
        return material
    }

    private fun applyDiscountForExchange(
        selectedSoldTotalPrice: BigDecimal, selectedMaterials: ArrayList<MaterialsTruckItem>,
        totalSoldDiscount: BigDecimal) {
        val pricingCalculations = PricingCalculations()

        val headerList = getBenefitsListHeader().sortedByDescending {
            it.condUnit
        }
        var selectedQuantity = 0.0
        var totalPriceBeforeDiscount = 0.0
        var totalPriceAfterDiscount = 0.0
        selectedMaterials.forEach {
            val materialFromDB = materialsDAO.getMaterialByPrimary(it.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
            val convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                it.selectedUnit,it.selectedQuantity)
            Log.d("getTotalPriceSold", "${it.materialNo} " +
                    " , ${it.selectedQuantity} , " +
                    " , $convertResult")
            selectedQuantity = it.selectedQuantity
            totalPriceBeforeDiscount += convertResult
            totalPriceAfterDiscount += (totalPriceBeforeDiscount - it.discountValue)
        }
        val totalPriceBeforeDisc = round(totalPriceBeforeDiscount.toBigDecimal())
        var discountValue = BigDecimal.ZERO

        headerList.forEach {
            when (it.condUnit) {
               /* Constants.conditionFOC -> {
                    var discountFoc = BigDecimal.ZERO
                    if (it.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                it,
                                totalPriceBeforeDiscount.toBigDecimal()
                            )
                        ) {
                            discountFoc = pricingCalculations.calculateFOCValue(
                                it.condValue!!,
                                totalPriceBeforeDiscount.toBigDecimal()
                            )
                        }
                    } else if (it.scaleUnit == Constants.scaleUnitQty) {
                        if (pricingCalculations.scaleQuantityAmount(it, selectedQuantity)) {
                            discountFoc = pricingCalculations.calculateFOCValue(
                                it.condValue!!,
                                totalPriceBeforeDiscount.toBigDecimal()
                            )
                        }
                    }
                }*/
                Constants.conditionPercentage -> {
                    var discount = BigDecimal.ZERO
                    if (it.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                it,
                                totalPriceBeforeDisc
                            )
                        ) {
                            discount = pricingCalculations.calculatePercentageValue(
                                it.condValue!!,
                                totalPriceBeforeDisc
                            )
                            discountValue = discount
                        }
                    }
                    else if (it.scaleUnit == Constants.scaleUnitQty) {
                        if (pricingCalculations.scaleQuantityAmount(it, selectedQuantity)) {
                            discount = pricingCalculations.calculatePercentageValue(
                                it.condValue!!,
                                totalPriceBeforeDisc
                            )
                            discountValue = discount
                        }
                    }
                }
                Constants.conditionQty -> {
                    var discount = BigDecimal.ZERO
                    if (it.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                it,
                                totalPriceBeforeDisc
                            )
                        ) {
                            discount = pricingCalculations.benefitQuantity(it)
                            discountValue = discount
                        }
                    } else if (it.scaleUnit == Constants.scaleUnitQty) {
                        discount = pricingCalculations.benefitQuantity(it)
                        discountValue = discount
                    }
                }
            }
        }
        totalSold = (round(totalPriceAfterDiscount.toBigDecimal()) - round(discountValue))
        totalPriceAfterDiscount = (totalSold.minus(totalSoldDiscount)).toDouble()
        getExchangePrice(discountValue, true)
        Log.d("getExchangeTotalPrice" , " is called from headerDiscount $totalSold $totalPriceBeforeDisc $discountValue $totalPriceAfterDiscount")
    }

 private fun applyDiscountForExchange(finalValue : BigDecimal ,
                                      isNegative : Boolean ) {
        val pricingCalculations = PricingCalculations()

        val headerList = getBenefitsListHeader().sortedByDescending {
            it.condUnit
        }
        var selectedQuantity = 0.0
        val totalPriceBeforeDisc = round(finalValue)
        var discountValue = BigDecimal.ZERO

        headerList.forEach {
            when (it.condUnit) {
                Constants.conditionPercentage -> {
                    var discount = BigDecimal.ZERO
                    if (it.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                it,
                                totalPriceBeforeDisc
                            )
                        ) {
                            discount = pricingCalculations.calculatePercentageValue(
                                it.condValue!!,
                                totalPriceBeforeDisc
                            )
                            discountValue = discount
                        }
                    }
                    else if (it.scaleUnit == Constants.scaleUnitQty) {
                        if (pricingCalculations.scaleQuantityAmount(it, selectedQuantity)) {
                            discount = pricingCalculations.calculatePercentageValue(
                                it.condValue!!,
                                totalPriceBeforeDisc
                            )
                            discountValue = discount
                        }
                    }
                }
                Constants.conditionQty -> {
                    var discount = BigDecimal.ZERO
                    if (it.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                it,
                                totalPriceBeforeDisc
                            )
                        ) {
                            discount = pricingCalculations.benefitQuantity(it)
                            discountValue = discount
                        }
                    } else if (it.scaleUnit == Constants.scaleUnitQty) {
                        discount = pricingCalculations.benefitQuantity(it)
                        discountValue = discount
                    }
                }
            }
        }
        exchangeHeaderDiscount = round(discountValue)

     finalResultExchange = if(isNegative) {
         round(totalInvoiceExchange + exchangeHeaderDiscount)
     }else {
         round(totalInvoiceExchange - exchangeHeaderDiscount)
     }
     Log.d("finalExchangeValue" , "$finalResultExchange")
     MaterialSelectionActivity.instance?.binding?.totalPrice = finalResultExchange.toString()
    }

    var finalResultExchange = BigDecimal.ZERO
    var exchangeHeaderDiscount = BigDecimal.ZERO

    fun applyDiscount() {
        val pricingCalculations = PricingCalculations()
        /*
             we will get descending value by unit type so we will get T first then A
             and we will reverse them to make discount happened first on total value
             then we apply foc value
         */
        val headerList = getBenefitsListHeader().sortedByDescending {
            it.condUnit
        }
        var totalPriceBeforeDiscount = 0.0
        var getMatTotal = BigDecimal.ZERO
        var selectedQuantity = 0.0
        var materialGroup = ""
        listOfMaterialGroupItems.clear()
            headerList.forEach {
                if(it.condUnit == Constants.conditionMaterialGroup) {
                    var materialGrp = it.condCustiomize!!.split("/").last()
                    var arrayListMaterialNo = ArrayList<String>()
                    var arrayListMaterialGroup = ArrayList<MaterialGroup>()
                    listOfMaterialGroupItems[materialGrp] = CustomizeCondition(
                        it.condCustiomize.toString(),
                        0.0,
                        it.scaleValue?.toDouble() ?: 0.0,
                        it.scaleUnit.toString(),
                        it.condSign.toString(),
                        it.condValue ?: 0.0,
                        0.0,
                        0.0,
                        0.0,
                        arrayListMaterialNo,
                        arrayListMaterialGroup)
                }
            }
        selectedMaterialsList.forEach { materialTruckItem ->
            selectedQuantity = materialTruckItem.selectedQuantity
            getMatTotal += round(getMaterialTotalPrice(materialTruckItem).toBigDecimal())
            totalPriceBeforeDiscount = getMatTotal.toDouble()
            materialGroup = materialTruckItem.materialGrp
            Log.d("totalBEfore", "$materialGroup , ${materialTruckItem.selectedUnit} $totalPriceBeforeDiscount " +
                    ", ${getMatTotal}")
            if(selectedQuantity != 0.0) {
                val itemDiscount = getItemCondition(materialTruckItem , materialGroup)
                listOfMaterialGroupItems[materialGroup]?.totalPrice =
                    listOfMaterialGroupItems[materialGroup]?.totalPrice?.plus(
                        itemDiscount.toDouble()
                    )!!
                Log.d("asdkjGetItemDiscount", " $itemDiscount")
                ifFOCAvailable = true
            }
            Log.d("getItemDisc", " mat Grp : ${materialGroup} :${listOfMaterialGroupItems[materialGroup]?.totalPrice}")
            Log.d("getItemDisc", " $ifFOCAvailable")
        }
        //each time the Fragment is called will add the totalFOC
        totalFOC = 0.0
        Log.d("totalPriceBeforeDisc", " mat Grp : ${totalPriceBeforeDiscount} :${round(totalPriceBeforeDiscount.toBigDecimal()).toDouble()}")
        val totalPriceBeforeDisc = round(totalPriceBeforeDiscount.toBigDecimal()).toDouble()
        var discountValue = BigDecimal.ZERO
        headerList.forEach { itemPriceCondition ->
            Log.d("getItemPriceCond", " ${itemPriceCondition.materialNo} " +
                    ", ${itemPriceCondition.condUnit} , " +
                    "${itemPriceCondition.scaleUnit} , ${itemPriceCondition.condValue}")
            when (itemPriceCondition.condUnit) {
                Constants.conditionFOC -> {
                    var discountFoc = BigDecimal.ZERO
                    if (itemPriceCondition.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                itemPriceCondition,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                        ) {
                            discountFoc = pricingCalculations.calculateFOCValue(
                                itemPriceCondition.condValue!!,
                                totalPriceBeforeDisc.toBigDecimal()
                            )

                            totalFOC = discountFoc.toDouble()
                        }
                    } else if (itemPriceCondition.scaleUnit == Constants.scaleUnitQty) {
                        if (pricingCalculations.scaleQuantityAmount(itemPriceCondition, selectedQuantity)) {
                            discountFoc = pricingCalculations.calculateFOCValue(
                                itemPriceCondition.condValue!!,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                            totalFOC = discountFoc.toDouble()
                        }
                    }
                }
                Constants.conditionPercentage -> {
                    var discount = BigDecimal.ZERO
                    if (itemPriceCondition.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                itemPriceCondition,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                        ) {
                            discount = pricingCalculations.calculatePercentageValue(
                                itemPriceCondition.condValue!!,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                            discountValue = round(discount)
                            Log.d("getConditionPercent" , "Amount : ${itemPriceCondition.condValue} $discountValue $discount")
                        }
                    } else if (itemPriceCondition.scaleUnit == Constants.scaleUnitQty) {
                        if (pricingCalculations.scaleQuantityAmount(itemPriceCondition, selectedQuantity)) {
                            discount = pricingCalculations.calculatePercentageValue(
                                itemPriceCondition.condValue!!,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                            discountValue = round(discount)
                            Log.d("getConditionPercent" , "Qty : ${itemPriceCondition.condValue} ")
                        }
                    }
                }
                Constants.conditionQty -> {
                    var discount = BigDecimal.ZERO
                    Log.d("getConditionQty" , " : ${itemPriceCondition.scaleUnit}")
                    if (itemPriceCondition.scaleUnit == Constants.scaleUnitAmount) {
                        if (pricingCalculations.scaleAmount(
                                itemPriceCondition,
                                totalPriceBeforeDisc.toBigDecimal()
                            )
                        ) {
                            discount = pricingCalculations.benefitQuantity(itemPriceCondition)
                            discountValue = round(discount)
                            Log.d("getConditionQty" , " Amount : ${itemPriceCondition.condValue} ")
                        }
                    } else if (itemPriceCondition.scaleUnit == Constants.scaleUnitQty) {
                        discount = pricingCalculations.benefitQuantity(itemPriceCondition)
                        discountValue = round(discount)
                    }
                    Log.d("getConditionQty" , " Scale : ${itemPriceCondition.condValue} ")

                }
            }
            Log.d("getTotalDiscountHeader", "Discount :$totalPriceBeforeDiscount $discountValue $totalPriceBeforeDisc")
        }
        totalFOC += totalItemFOC
        if (!isExchange) {
            totalPriceAfterHeaderDiscount = convertToEnglish(String.format(
                "%.3f",
                round(getTotalInvoicePrice().toBigDecimal() - discountValue)
            )).toString()
            headerDiscount = discountValue
            MaterialSelectionActivity.instance?.binding?.totalPrice = totalPriceAfterHeaderDiscount
        } else {
            getExchangePrice(discountValue, false)
            Log.d("getExchangeTotalPrice" , " is called from headerExchangeDiscount $discountValue")
        }
    }

    fun sendDocument(salesDocBody: SalesDocBody) = viewModelScope.launch {
        viewStateCreateDocument.postValue(CreateDocumentViewState.Loading(true))
        when (val response = createDocumentUseCase.invoke(salesDocBody)) {
            is ApiResponse.Success -> {
                viewStateCreateDocument.postValue(
                    CreateDocumentViewState.Data(response.body)
                )
            }
            is ApiResponse.NetworkError -> viewStateCreateDocument.postValue(
                CreateDocumentViewState.NetworkFailure
            )
            else -> viewStateCreateDocument.postValue(CreateDocumentViewState.Loading(false))
        }
    }
    fun cancelDocument(cancelDocumentRequestModel: CancelDocumentRequestModel, exInvoice : String, invoiceType:String?=null)
    = viewModelScope.launch {
        viewStateCancelDocument.postValue(CancelDocumentViewState.Loading(true))
        when (val response = cancelDocumentUseCase.invoke(cancelDocumentRequestModel)) {
            is ApiResponse.Success -> {
                if(response.body.data[0].message1 == "Document does not exist"){
                    viewStateCancelDocument.postValue(CancelDocumentViewState.NetworkFailure)
                    }else {
                        if(response.body.data[0].messagetype == "S") {
                            if (response.body.data[0].message1 == "Success" ||
                                response.body.data[0].message1 == "Billing document is already cancelled") {
                                cancelDocument(exInvoice, response.body.data[0].exdoc,invoiceType)
                            } else {
                                cancelDocument(exInvoice, response.body.data[0].message1,invoiceType)
                            }
                        }
                    viewStateCancelDocument.postValue(
                        CancelDocumentViewState.Data(response.body)
                    )
                }
            }
            is ApiResponse.NetworkError -> viewStateCancelDocument.postValue(
                CancelDocumentViewState.NetworkFailure
            )
            else -> viewStateCancelDocument.postValue(CancelDocumentViewState.Loading(false))
        }
    }


    fun getPayers(payersRequestModel: PayersRequestModel) = viewModelScope.launch {
        viewStateCreatePayer.postValue(PayersViewState.Loading(true))
        when (val response = createPayerUseCase.invoke(payersRequestModel)) {
            is ApiResponse.Success -> {
                viewStateCreatePayer.postValue(
                    PayersViewState.Data(response.body)
                )
            }
            is ApiResponse.NetworkError -> viewStateCreatePayer.postValue(
                PayersViewState.NetworkFailure
            )
            else -> viewStateCreatePayer.postValue(PayersViewState.Loading(false))
        }
    }

    private fun createDocumentUseCase(): CreateDocumentUseCases.CreateDocumentInvoke {
        return CreateDocumentUseCaseImpl(CreateDocumentRepositoryImpl(createDocumentGateway))
    }
    private fun cancelDocumentUseCase(): CancelDocumentUseCases.CancelDocumentInvoke {
        return CancelDocumentUseCaseImpl(CancelDocumentRepositoryImpl(cancelDocumentGateway))
    }

    private fun createPayerUseCase(): PayersUseCases.GetPayersInvoke {
        return PayersUseCaseImpl(PayersRepositoryImpl(createPayerGateway))
    }

    fun deleteFromMaterialGroupList(materialTruckItem: MaterialsTruckItem?) {
        var deducePrice = materialTruckItem?.selectedQuantity?.times(materialTruckItem.unitPrice)
        if (materialTruckItem != null) {
//            ifFOCAvailable = false
            setBenefitsList(materialTruckItem, "F")
            val materialFromDB = materialsDAO.getMaterialByPrimary(materialTruckItem.materialNo,currentCustomer!!.salesOrg,currentCustomer!!.distChannel)
            var convertResult = pricingUtils.calculatePrice(materialFromDB,currentCustomer!!,
                materialTruckItem.selectedUnit,materialTruckItem.selectedQuantity)
            deducePrice = convertResult
        }
        if (deducePrice != null && deducePrice != 0.0) {
                if(listOfMaterialGroupItems[materialTruckItem?.materialGrp]?.materialByUnit?.contains(
                        materialTruckItem?.materialNo?.let { MaterialGroup(it,materialTruckItem.selectedUnit) }) == true) {
                listOfMaterialGroupItems[materialTruckItem?.materialGrp]?.selectedPrice =
                    listOfMaterialGroupItems[materialTruckItem?.materialGrp]?.selectedPrice?.minus(
                        deducePrice
                    )!!
                listOfMaterialGroupItems[materialTruckItem?.materialGrp]?.materialByUnit?.remove(
                    materialTruckItem?.materialNo?.let { MaterialGroup(it,materialTruckItem.selectedUnit) }
                )
            }
        }
    }
    fun convertToBaseWithoutRounding(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        return mRepository.convertToBaseWithoutRounding(materialNumber, FromUnit, Quantity)
    }

    fun convertToBaseWithoutRounding(materialNumber: String, FromUnit: String,toUnit :String , Quantity: Double): Double {
        return pricingUtils.convertToBaseWithoutRounding(materialNumber, FromUnit, toUnit,Quantity)
    }

    fun getMaterialBaseUnit(materialNumber: String, FromUnit: String): String {
        return mRepository.getMaterialBaseUnit(materialNumber, FromUnit)
    }

    fun updateHeader(invoiceHeader: InvoiceHeader) {
        salesDocRepo.updateHeader(invoiceHeader)
    }
}