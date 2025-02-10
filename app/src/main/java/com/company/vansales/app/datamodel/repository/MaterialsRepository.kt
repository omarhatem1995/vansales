package com.company.vansales.app.datamodel.repository

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.company.vansales.app.datamodel.models.localmodels.MaterialsTruckItem
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.BaseResponse
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.ItemPriceCondition
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.MaterialsUnit
import com.company.vansales.app.datamodel.models.mastermodels.PostDocumentResponse
import com.company.vansales.app.datamodel.models.mastermodels.Prices
import com.company.vansales.app.datamodel.models.mastermodels.Routes
import com.company.vansales.app.datamodel.models.mastermodels.Taxes
import com.company.vansales.app.datamodel.models.mastermodels.TruckBatch
import com.company.vansales.app.datamodel.models.mastermodels.TruckItem
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.ItemPriceConditionDAO
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.MaterialsUnitDAO
import com.company.vansales.app.datamodel.room.PricesDAO
import com.company.vansales.app.datamodel.room.RoutesDAO
import com.company.vansales.app.datamodel.room.TaxesDAO
import com.company.vansales.app.datamodel.room.TruckBatchDAO
import com.company.vansales.app.datamodel.room.TruckItemDAO
import com.company.vansales.app.datamodel.room.VisitsDAO
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants.BATCH_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.FINISHED_VISIT
import com.company.vansales.app.utils.Constants.IN_PROGRESS_VISIT
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_FREE
import com.company.vansales.app.utils.Constants.MATERIAL_SELLABLE
import com.company.vansales.app.utils.Constants.NUMBER_OF_RETRIES
import com.company.vansales.app.utils.Constants.STATUS_ERROR
import com.company.vansales.app.utils.Constants.UNIT_ALLOW_DECIMAL
import com.company.vansales.app.utils.DocumentsConstants.RETURN_WITHOUT_REFERENCE
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import java.math.BigDecimal
import java.util.concurrent.Executors

class MaterialsRepository(application: Application) {
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    val materialsDAO: MaterialsDAO = appDB.getMaterials()
    private val materialUnitDAO: MaterialsUnitDAO
    private lateinit var truckItemDAO: TruckItemDAO
    private var priceDAO: PricesDAO
    private lateinit var visitsDAO: VisitsDAO
    private lateinit var routeDAO: RoutesDAO
    private var taxesDAO: TaxesDAO = appDB.getTaxes()
    private var itemPriceConditionDAO: ItemPriceConditionDAO = appDB.getItemPriceConditions()
    private var truckBatches: TruckBatchDAO = appDB.getTruckBatch()
    private val convertUnit: ConvertUnit
    lateinit var postResponse: MutableLiveData<PostDocumentResponse>
    lateinit var materialsListLocale: List<Materials>
    private var listFromMaterials = MutableLiveData<List<MaterialsTruckItem>>()
    private var listFromTruckItem = MutableLiveData<List<MaterialsTruckItem>>()
    private var clientService = ClientService.getClient(application)
    private var customerRepository = CustomerRepository(application)

    init {
        priceDAO = appDB.getPrices()
        materialUnitDAO = appDB.getMaterialsUnit()
        convertUnit = ConvertUnit(application)
    }

    fun upsert(materials: List<Materials>) {
        materialsDAO.upsert(materials)
    }

    fun update(materials: List<Materials>) {
        materialsDAO.updateMaterials(materials)
    }

    fun getTaxesByCustomerAndMaterialNo(customerNumber: String?, materialNo: String? , distChannel : String?) : Materials{
       return materialsDAO.getTaxesForMaterial(materialNo?:"", distChannel = distChannel?:"")
    }

    fun getTaxesToCustomerAndMaterialNo(customerCode: String?, materialCode: String? , condition : String?) : Taxes {
       return taxesDAO.getTaxesForCustomerAndMaterial(customerCode?:"",
           materialCode?:"",condition?:"")
    }

    fun deleteAllTaxes(){
        return taxesDAO.deleteAllTaxes()
    }

    fun getMaterialsRemote(baseBody: BaseBody): Observable<BaseResponse<Materials>> {
        return clientService.getMaterials(baseBody)
    }
    fun getAllMaterials(salesOrg: String, distChannel : String) {
        materialsListLocale = materialsDAO.getAllMaterials(salesOrg,distChannel)
    }

    fun getAllMaterials(): List<Materials> {
        return materialsDAO.getMaterials()
    }

    fun getMaterialByMaterialNo(materialNo: String): Materials {
        return materialsDAO.getMaterialByMaterialNo(materialNo)
    }
    fun getMaterialByMaterialNumberAndDistChannel(materialNo: String , distChannel: String): Materials {
        return materialsDAO.getMaterialByMaterialNumberAndDistChannel(materialNo,distChannel)
    }

    fun getDriver(): Customer? {
        return customerRepository.getCurrentDriver()?.value
    }

    fun createListFromMaterials(customerNumber: String?) {
        listFromMaterials = MutableLiveData()
        var list: ArrayList<MaterialsTruckItem>
        Executors.newSingleThreadExecutor().execute {
            list = getFromMaterials(customerNumber)
            listFromMaterials.postValue(list)
        }
    }

    fun createListFromTruckItem(
        customerNumber: String?,
        salesOrg: String?,
        distChannel: String?,
        materialStatus: String?
    ) {
        listFromTruckItem = MutableLiveData()
        var list: ArrayList<MaterialsTruckItem>
        Executors.newSingleThreadExecutor().execute {
            list = getFromTruckItem(customerNumber, salesOrg, materialStatus, distChannel)
            listFromTruckItem.postValue(list)
        }
    }

    fun getFromTruckItem(
        customerNumber: String?,
        salesOrg: String?,
        materialStatus: String?,
        distChannel: String?
        ): ArrayList<MaterialsTruckItem> {
        truckItemDAO = appDB.getTruckItem()
        priceDAO = appDB.getPrices()
        var freePosition = 0
        val truckItemList: ArrayList<TruckItem> =
            truckItemDAO.getTruckItemsBySalesOrg(salesOrg, materialStatus) as ArrayList<TruckItem>
        var materialsTruckItemObject: MaterialsTruckItem
        var materialObject: ArrayList<Materials>
        var i = 0
        val list = ArrayList<MaterialsTruckItem>()

        for (item in truckItemList) {
            materialObject = materialsDAO.getMaterialsByPrimary(
                item.materialNo,
                item.salesOrg,
                distChannel
            ) as ArrayList<Materials>
            materialsTruckItemObject = MaterialsTruckItem()
            if (materialObject.isNotEmpty()) {
                materialsTruckItemObject =
                    fillMaterialsData(
                        freePosition,
                        materialObject[0],
                        materialsTruckItemObject,
                        customerNumber
                    )
                materialsTruckItemObject = fillTruckItemData(item, materialsTruckItemObject)
                if (materialsTruckItemObject.unitPrice > 0 && materialsTruckItemObject.available > 0) {
                    materialsTruckItemObject.position = i
                    if (materialsTruckItemObject.isFree == MATERIAL_CATEGORY_FREE)
                        freePosition++
                    list.add(materialsTruckItemObject)
                    i++
                }
            }
        }
        return list
    }

    private fun getFromMaterials(customerNumber: String?): ArrayList<MaterialsTruckItem> {
        priceDAO = appDB.getPrices()
        var freePosition = 0
        val list = ArrayList<MaterialsTruckItem>()
        var materialsTruckItemObject: MaterialsTruckItem
        var i = 0
        for (item in materialsListLocale) {
            materialsTruckItemObject = MaterialsTruckItem()
            materialsTruckItemObject =
                fillMaterialsData(freePosition, item, materialsTruckItemObject, customerNumber)
            if (materialsTruckItemObject.unitPrice > 0) {
                materialsTruckItemObject.position = i
                if (materialsTruckItemObject.isFree == MATERIAL_CATEGORY_FREE)
                    freePosition++
                list.add(materialsTruckItemObject)
                i++
            }
        }
        return list
    }

    fun getListFromMaterials(): LiveData<List<MaterialsTruckItem>> {
        Log.d("listOfMats" , " from materials $listFromMaterials")
        return listFromMaterials
    }

    fun getListFromTruckItem(): LiveData<List<MaterialsTruckItem>> {
        Log.d("listOfMats" , " from truck $listFromMaterials")
        return listFromTruckItem
    }

    private fun fillMaterialsData(
        freePosition: Int,
        material: Materials,
        materialsTruckItemObject: MaterialsTruckItem,
        customerNumber: String?
    ): MaterialsTruckItem {
        materialsTruckItemObject.salesOrg = material.salesOrg
        materialsTruckItemObject.distChannel = material.distChannel
        materialsTruckItemObject.baseUnit = material.baseUnit!!
        materialsTruckItemObject.selectedUnit = material.baseUnit!!
        materialsTruckItemObject.materialNo = material.materialNo
        materialsTruckItemObject.materialDescrption = material.materialDescrption!!
        materialsTruckItemObject.materialGrp = material.materialGrp!!
        materialsTruckItemObject.materialType = material.materialType!!
        materialsTruckItemObject.minShelf = material.minShelf!!
        materialsTruckItemObject.salesUnit = material.salesUnit!!
        materialsTruckItemObject.grpDesc = material.grpDesc!!
        materialsTruckItemObject.oldMat = material.oldMat!!
        materialsTruckItemObject.totalShelf = material.totalShelf!!
        materialsTruckItemObject.materialArabic = material.materialArabic!!
        materialsTruckItemObject.grpArabic = material.grpArabic!!

        val pricesList =
            priceDAO.getPricesByPrimary(material.materialNo, customerNumber, material.distChannel)
        if (pricesList.isNotEmpty()) {
            materialsTruckItemObject.unitPrice =
                getMaterialUnitPrice(material.materialNo, material.baseUnit!!, pricesList[0])

        }

        materialsTruckItemObject.isFree = MATERIAL_CATEGORY_FREE
        materialsTruckItemObject.freePosition = freePosition

        if (material.expiryDate != null)
            materialsTruckItemObject.expiryDate = material.expiryDate!!

        if(material.materialNo == "1EVA23")
        {
            val x = 1
            var y = x + x

        }
        val materialsUnitList: List<MaterialsUnit> =
            convertUnit.getMaterialUnits(material.materialNo)

        var decimalUnitNotFound=true

        for (materialUnit in materialsUnitList) {
            materialsTruckItemObject.barcodeList.add(materialUnit.barcode!!)
            materialsTruckItemObject.unitQuantityList[materialUnit.unit] = 0.0
            if(materialUnit.unit == UNIT_ALLOW_DECIMAL)
                decimalUnitNotFound=false
        }

        //TODO assigning unit to allow decimals
/*
        if(decimalUnitNotFound)
            materialsTruckItemObject.unitQuantityList[UNIT_ALLOW_DECIMAL]=0.0
*/
        return materialsTruckItemObject
    }

    private fun getMaterialUnitPrice(materialNo: String, baseUnit: String, price: Prices): Double {
        val convertResult = convertToBase(materialNo, baseUnit, 1.0)
        return price.condValue!! * convertResult
    }

    private fun fillTruckItemData(
        truckItem: TruckItem,
        materialsTruckItemObject: MaterialsTruckItem
    ): MaterialsTruckItem {
        materialsTruckItemObject.isSellable = truckItem.isSellable!!
        materialsTruckItemObject.customizeField = truckItem.customizeField!!
        materialsTruckItemObject.plant = truckItem.plant!!
        materialsTruckItemObject.mtype = truckItem.mtype
        materialsTruckItemObject.total = truckItem.total!!
        materialsTruckItemObject.available = AppUtils.round(truckItem.available!!.toBigDecimal()).toDouble()
        return materialsTruckItemObject
    }

    fun convertFromBase(materialNumber: String, ToUnit: String, Quantity: Double): Double {
        return convertUnit.convertFromBase(materialNumber, ToUnit, Quantity)
    }

    fun convertToBase(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        return convertUnit.convertToBase(materialNumber, FromUnit, Quantity)
    }
    fun convertToBaseWithSingleItemPrice(materialNumber: String, customer : Customer?,fromUnit : String , quantity : Double): Double {
        return convertUnit.getSingleItemPrice(materialNumber, customer , fromUnit , quantity)
    }
    fun convertToBaseWithSingleItemPriceNothingEquals(materialNumber: String, customer : Customer?,fromUnit : String , quantity : Double): Double {
        return convertUnit.getSingleItemPriceNothingEquals(materialNumber, customer , fromUnit , quantity)
    }
    fun convertMaterialUnit(materialNumber: String, toUnit: String, quantity: Double): String? {
        return  convertUnit.convertMaterialUnit(materialNumber, toUnit , quantity)
    }
    fun convertToBaseWithoutRounding(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        return convertUnit.convertToBaseWithoutRounding(materialNumber, FromUnit, Quantity)
    }
    fun getMaterialBaseUnit(materialNumber: String, FromUnit: String): String {
        return convertUnit.getMaterialBaseUnit(materialNumber, FromUnit)
    }

    fun isMaterialBaseUnitTheSame(materialNumber: String , selectedUnit : String, customer : Customer) : Int {
       val price = priceDAO.getMaterialPriceByPrimary(materialNumber,customer.customer , customer.distChannel)
        Log.d("getPriceBaseUnit", "$materialNumber : ${price.condUnit}")
       val materialUnits = materialUnitDAO.getMaterialUnitByPrimary(materialNumber,selectedUnit)
        Log.d("getMatUnit", "$materialNumber : ${materialUnits.unit} , ${materialUnits.baseUnit}")
       //price.condUnit (Sales Unit) != materialUnits.baseUnit
        if( selectedUnit != materialUnits.baseUnit)
            if(selectedUnit != price.condUnit)
                return 4
        if (price.condUnit == materialUnits.baseUnit)
        return 1
            else if (price.condUnit != selectedUnit)
                return 1
        else return 3
    }

    fun getCustomer(): Customer? {
        return if(getCurrentActiveVisit() != null){
            customerRepository.getCustomerByID(
                getCurrentActiveVisit()!!.customerNo!!,
                getCurrentActiveVisit()!!.salesOrg!!,
                getCurrentActiveVisit()!!.dist_channel!!
            )
        } else{
            null
        }
    }

    fun getCurrentActiveVisit(): Visits? {
        visitsDAO = appDB.getVisits()
        return visitsDAO.getCurrentActiveVisit(IN_PROGRESS_VISIT)
    }

    fun finishVisit(visitId:String,visitItemNo:String)
    {
        visitsDAO.finishVisit(visitId,visitItemNo.toInt(),FINISHED_VISIT)
    }

    fun getRoute(routeID: String): Routes {
        routeDAO = appDB.getRoutes()
        return routeDAO.getRouteDetailsById(routeID)
    }

    private fun checkExceededReturnLimit(returnValue : BigDecimal) : Boolean{
        if(returnValue > "100.000".toBigDecimal()){
            return true
        }
        return false
    }

    @SuppressLint("CheckResult")
    fun post(mySalesDocBody: SalesDocBody) {
        postResponse = MutableLiveData()
        if(mySalesDocBody.iHeader.doctype == RETURN_WITHOUT_REFERENCE &&
                checkExceededReturnLimit(mySalesDocBody.iHeader.totalvalue))
            mySalesDocBody.iHeader.paymentterm = "A"

        clientService.createDocument(mySalesDocBody)
            .subscribeOn(Schedulers.io())
            .doOnNext {  }
            .retry(NUMBER_OF_RETRIES)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    postResponse.value = it.data[0]
                }, {
                    val response = PostDocumentResponse("", STATUS_ERROR, "", "")
                    postResponse.value = response
                })
    }

    fun getItemPriceConditions(
        materialNumber: String,
        customerId: String?,
        distChannel: String?,
        salesOrg: String?
    ): List<ItemPriceCondition> {
        return itemPriceConditionDAO.getItemPriceConditionsByPrimary(
            materialNumber,
            customerId,
            distChannel,
            salesOrg
        )
    }

    fun getItemPriceConditionsHeader(
        customerId: String?,
        distChannel: String?,
        salesOrg: String?
    ): List<ItemPriceCondition> {
        return itemPriceConditionDAO.getItemPriceConditionsHeader(
            customerId,
            distChannel,
            salesOrg
        )
    }
    fun getSelectedBatchExpiration(headerItem: HeaderItems, salesOrg: String?): List<TruckBatch> {
        val batches = truckBatches.getBatchesByMaterialNo(headerItem.materialno!!, MATERIAL_SELLABLE, salesOrg ?: "10")

        return batches.sortedWith(compareByDescending<TruckBatch> {
            it.batch == BATCH_RETURN_SELLABLE
        }.thenBy { it.expiryDate })
    }

}
