package com.company.vansales.app.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.vansales.app.SAPWizardApplication.Companion.application
import com.company.vansales.app.datamodel.models.base.ApiResponse
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems
import com.company.vansales.app.datamodel.models.mastermodels.Deliveries
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.TruckBatch
import com.company.vansales.app.datamodel.repository.ConvertUnit
import com.company.vansales.app.datamodel.repository.CreateDocumentRepositoryImpl
import com.company.vansales.app.datamodel.repository.CustomerRepository
import com.company.vansales.app.datamodel.repository.MaterialsRepository
import com.company.vansales.app.datamodel.repository.SalesDocRepository
import com.company.vansales.app.datamodel.repository.StorageLocationRepositoryImpl
import com.company.vansales.app.datamodel.repository.TruckManagementRepository
import com.company.vansales.app.datamodel.repository.VisitsRepository
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.datamodel.services.api.CreateDocumentGateway
import com.company.vansales.app.datamodel.services.api.StorageLocGateway
import com.company.vansales.app.datamodel.sharedpref.GetSharedPreferences
import com.company.vansales.app.domain.usecases.CreateDocumentUseCases
import com.company.vansales.app.domain.usecases.StorageLocationUseCases
import com.company.vansales.app.framework.CreateDocumentUseCaseImpl
import com.company.vansales.app.framework.StorageLocUseCaseImpl
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.round
import com.company.vansales.app.utils.Constants.BATCH_RETURN_DAMAGED
import com.company.vansales.app.utils.Constants.BATCH_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_DELIVERIES
import com.company.vansales.app.utils.DocumentsConstants.CONSIGNMENT_POST_ISSUE
import com.company.vansales.app.utils.enums.BatchScanTypesEnum
import com.company.vansales.app.view.entities.CreateDocumentViewState
import com.company.vansales.app.view.entities.GetStorageLocViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatchScanViewModel @Inject constructor(private val storageLocGateway: StorageLocGateway,
                                             val createDocumentGateway: CreateDocumentGateway
) :
    ViewModel() {

    var isReturn = false
    var isFillUp = false
    var isOffload = false
    private val getStorageLocationUseCase = createStorageLocUseCase()
    private val createDocumentUseCase = createDocumentUseCase()
    var deliveries: Deliveries? = null
    private var selectedFillUpItem: Delivery? = null
    var mRepository =  MaterialsRepository(application)
    private lateinit var truckManagementRepository: TruckManagementRepository
    private lateinit var visitsRepository: VisitsRepository
    private var customerRepository = CustomerRepository(application)
    private var salesDocRepository = SalesDocRepository(application)
//    var docNumber: ((String) -> Unit)? = null
    var subtract = MutableLiveData<Boolean>()
    var clientService = ClientService.getClient(application)
//    var compositeDisposable = CompositeDisposable()
    val driver = customerRepository.getCurrentDriver()
    var convertUnit = ConvertUnit(application)
    var isApproved = false
    //Post Results Status
    var userPreference = GetSharedPreferences(application)
/*
    var createDocumentStatusMLD = MutableLiveData<String>()
    val createDocumentStatusLD: LiveData<String>
        get() = createDocumentStatusMLD
*/

    // Header Components
    val deliveriesItemsMLD = MutableLiveData<List<HeaderItems>>()
    val deliveriesItemsLiveData: LiveData<List<HeaderItems>>
        get() = deliveriesItemsMLD

    val deliveriesBatchesMLD = MutableLiveData<List<HeaderBatches>>()

    //exceeding counted amount
    val exceededAmountMLD = MutableLiveData<Boolean>()

    //hide counted materials
    val hideCountedMLD = MutableLiveData<Boolean>()

    //close keyboard
    val closeKeyboard = MutableLiveData<Boolean>()
    val viewStateStorageLocView: MutableLiveData<GetStorageLocViewState> = MutableLiveData()

    val viewStateCreateDocument: MutableLiveData<CreateDocumentViewState> = MutableLiveData()


    //Mapping Data
    fun mapDeliveriesList(deliveries: Deliveries) {
        this.deliveries = deliveries
        subtract.value = false
        val deliveryItemList = mutableListOf<HeaderItems>()
        val deliveryBatchesList = mutableListOf<HeaderBatches>()
        for (i in deliveries.deliveryItem.indices) {
            val headerItems =
                HeaderItems(
                    salesOrg = selectedFillUpItem!!.salesOrg!!,
                    distChannel = driver?.value!!.distChannel,
                    exdoc = deliveries.deliveryItem[i].delivery,
                    itemno = deliveries.deliveryItem[i].itemNo,
                    materialno = deliveries.deliveryItem[i].materialNo,
                    isSellable = "",
                    requestedQuantity = deliveries.deliveryItem[i].quantity,
                    countedQuantity = 0.0,
                    materialDescription = mRepository.getMaterialByMaterialNo(deliveries.deliveryItem[i].materialNo).materialDescrption!!,
                    materialDescriptionArabic = mRepository.getMaterialByMaterialNo(deliveries.deliveryItem[i].materialNo).materialArabic!!,
                    uom = deliveries.deliveryItem[i].unit,
                    totalvalue = 0.0,
                    currency = "",
                    customizeitem = "",
                    returnreason = "",
                    barcodeList = convertUnit.getMaterialsBarcode(deliveries.deliveryItem[i].materialNo),
                    price = 0.0,
                    discount = 0.0,
                    itemCategory = MATERIAL_DELIVERIES,
                    totalPrice = 0.0,
                    storageLocation = deliveries.deliveryItem[i].storageLocation,
                    plant =  deliveries.deliveryItem[i].plant
                )

            deliveryItemList.add(headerItems)
        }

        for (i in deliveries.deliveryBatch.indices) {
            val headerBatches =
                HeaderBatches(
                    exdoc = deliveries.deliveryBatch[i].delivery,
                    itemno = deliveries.deliveryBatch[i].itemNo,
                    batch = deliveries.deliveryBatch[i].batch,
                    materialno = deliveries.deliveryBatch[i].materialNo,
                    parentBaseRequestedQuantity = deliveries.deliveryBatch[i].quantity,
                    baseRequestedQuantity = deliveries.deliveryBatch[i].quantity,
                    baseUnit = deliveries.deliveryBatch[i].unit,
                    baseCounted = 0.0,
                    requestedQuantity = deliveries.deliveryBatch[i].quantity,
                    countedQuantity = 0.0,
                    expiryDate = "",
                    uom = deliveries.deliveryBatch[i].unit,
                    bType = MATERIAL_DELIVERIES,
                    customizebatch = "",
                    storageLocation = deliveries.deliveryBatch[i].storageLocation,
                    plant = deliveries.deliveryBatch[i].plant
                )
            deliveryBatchesList.add(headerBatches)
        }
        alterExchangeReturnItems(deliveryItemList as ArrayList<HeaderItems>)
        Log.d("deliveriesLog", " ${deliveryItemList.size} , ${deliveryBatchesList.size}")
        deliveriesItemsMLD.value = deliveryItemList
        deliveriesBatchesMLD.value = deliveryBatchesList
    }

    fun mapMaterialsList(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>
    ) {
        subtract.value = true
        truckManagementRepository = TruckManagementRepository(application)
        val materialsBatchesList = mutableListOf<HeaderBatches>()
        for (i in headerItems.indices) {
            if (headerItems[i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                || headerItems[i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
            ) {
                if (headerItems[i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE &&
                    headerItems[i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                ) {
                    Log.d("headerItems" , "${headerItems[i].materialno}")
                    val truckBatches =
                        truckManagementRepository.getSellableBatchesAndDeliveriesByMaterialNo(
                            headerItems[i].materialno!!,
                            headerItems[i].salesOrg
                        )
                    truckBatches.forEach {
                        Log.d("getlaskdlsakd", " lo l ${it.materialNo} , ${it.batch}")

                    }
                    Log.d("getlaskdlsakd", "${truckBatches.size}")
                    for (j in truckBatches.indices) {
                        val truckBatch = truckBatches[j]
                        val headerBatch = HeaderBatches(
                            exdoc = iSalesDocHeader.exdoc,
                            itemno = headerItems[i].itemno,
                            batch = truckBatch.batch,
                            materialno = headerItems[i].materialno,
                            parentBaseRequestedQuantity = convertUnit.convertUnit(
                                headerItems[i].materialno!!,
                                truckBatch.uom!!,
                                headerItems[i].uom!!,
                                truckBatch.available!!
                            ),
                            baseRequestedQuantity = convertUnit.convertUnit(
                                headerItems[i].materialno!!,
                                truckBatch.uom!!,
                                headerItems[i].uom!!,
                                truckBatch.available!!
                            ),
                            baseUnit = headerItems[i].uom,
                            baseCounted = 0.0,
                            requestedQuantity = convertUnit.convertUnit(
                                headerItems[i].materialno!!,
                                truckBatch.uom!!,
                                headerItems[i].uom!!,
                                truckBatch.available!!
                            ),
                            countedQuantity = 0.0,
                            expiryDate = truckBatch.expiryDate,
                            uom = headerItems[i].uom,
                            bType = truckBatch.mtype,
                            customizebatch = "",
                            storageLocation = headerItems[i].storageLocation,
                            plant = headerItems[i].plant
                        )
                        materialsBatchesList.add(headerBatch)
                    }
                }
            }
        }
        alterExchangeReturnItems(headerItems)
        if (!alterExchangeReturnBatches(headerItems, iSalesDocHeader).isNullOrEmpty()) {
            materialsBatchesList.addAll(alterExchangeReturnBatches(headerItems, iSalesDocHeader))
        }
        deliveriesBatchesMLD.value = materialsBatchesList
        deliveriesItemsMLD.value = headerItems
    }

    private fun alterExchangeReturnItems(headerItems: ArrayList<HeaderItems>) {
        for (i in headerItems.indices) {
            if (headerItems[i].itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                || headerItems[i].itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                headerItems[i].countedQuantity = headerItems[i].requestedQuantity
            }
        }

    }

    private fun alterExchangeReturnBatches(
        headerItems: ArrayList<HeaderItems>,
        iSalesDocHeader: ISalesDocHeader
    ): MutableList<HeaderBatches> {
        val returnHeaderBatches: MutableList<HeaderBatches> = ArrayList()
        for (i in headerItems.indices) {
            if (headerItems[i].itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                || headerItems[i].itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
            ) {
                val batchNumber: String =
                    if (headerItems[i].itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE) {
                        BATCH_RETURN_SELLABLE
                    } else {
                        BATCH_RETURN_DAMAGED
                    }
                val headerBatch = HeaderBatches(
                    exdoc = iSalesDocHeader.exdoc,
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

        }

        return returnHeaderBatches as ArrayList


    }

    fun setSelectedFillUpItem(selectedFillUpItem: Delivery) {
        this.selectedFillUpItem = selectedFillUpItem
    }


    //Validating & Mathematical Operations upon amount
    fun autoCountDeliveryItemsAndBatches() {
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                && deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
            ) {
                deliveriesItemsMLD.value!![i].countedQuantity =
                    deliveriesItemsMLD.value!![i].requestedQuantity
            }
        }

        for (i in deliveriesBatchesMLD.value!!.indices) {
            deliveriesBatchesMLD.value!![i].countedQuantity =
                deliveriesBatchesMLD.value!![i].requestedQuantity
        }

    }

    fun undoAutoCountDeliveryItemsAndBatches() {
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                && deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                deliveriesItemsMLD.value!![i].countedQuantity = 0.0
            }
        }

        for (i in deliveriesBatchesMLD.value!!.indices) {
            deliveriesBatchesMLD.value!![i].countedQuantity = 0.0
        }

    }

    fun addAmountInBatchAndItem(
        givenQuantity: Double,
        batchNo: String,
        itemNo: String,
        materialNo: String,
        baseUnit : String
    ) {
        var countedQuantity: Double
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].batch == batchNo &&
                deliveriesBatchesMLD.value!![i].materialno == materialNo &&
                deliveriesBatchesMLD.value!![i].itemno == itemNo
            ) {
                countedQuantity = givenQuantity
                Log.d("isAddedAmountValid", "$countedQuantity  , $batchNo   , $itemNo")
                if (isAddedAmountValid(countedQuantity, batchNo, itemNo)) {
                    deliveriesBatchesMLD.value!![i].baseCounted = countedQuantity
                    deliveriesBatchesMLD.value!![i].countedQuantity = countedQuantity
                    if (subtract.value!!) {
                        subtractBatchAddedAmount(
                            deliveriesBatchesMLD.value!![i].batch!!,
                            deliveriesBatchesMLD.value!![i].materialno!!
                        )
                    }
                } else {
                    exceededAmountMLD.value = true
                }
            }
        }
        updateHeaderItemsList(itemNo)
    }

    fun addAmountInBatchAndItemStockCount(
        givenQuantity: Double,
        batchNo: String,
        itemNo: String,
        materialNo: String
    ) {

        var countedQuantity: Double
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].materialno == materialNo &&
                deliveriesBatchesMLD.value!![i].itemno == itemNo
            ) {
                countedQuantity = convertUnit.convertUnit(
                    materialNo,
                    deliveriesBatchesMLD.value!![i].uom!!,
                    deliveriesBatchesMLD.value!![i].baseUnit!!,
                    givenQuantity
                )
                if (isAddedAmountValid(countedQuantity, batchNo, itemNo)) {
                    deliveriesBatchesMLD.value!![i].baseCounted = countedQuantity
                    deliveriesBatchesMLD.value!![i].countedQuantity = countedQuantity
                    if (subtract.value!!) {
                        subtractBatchAddedAmount(
                            deliveriesBatchesMLD.value!![i].batch!!,
                            deliveriesBatchesMLD.value!![i].materialno!!
                        )
                    }
                } else {
                    exceededAmountMLD.value = true
                }
            }
        }
        updateHeaderItemsList(itemNo)
    }

    private fun isAddedAmountValid(
        givenQuantity: Double,
        batchNo: String,
        itemNo: String
    ): Boolean {
        var amount = givenQuantity
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].itemno == itemNo &&
                deliveriesBatchesMLD.value!![i].batch != batchNo
            ) {
                amount += deliveriesBatchesMLD.value!![i].countedQuantity!!
            }
        }
        Log.d("asldksaldaskdlkd", "${AppUtils.round(amount)} , ${getMaterialRequestedQuantity(itemNo)!!.requestedQuantity!!}")
//        return AppUtils.round(amount) <= getMaterialRequestedQuantity(itemNo)!!.requestedQuantity!!
        return true
    }

    fun deleteAmountInBatchAndItem(batchNo: String, itemNo: String, type: String) {
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].batch == batchNo &&
                deliveriesBatchesMLD.value!![i].itemno == itemNo &&
                deliveriesBatchesMLD.value!![i].bType == type
            ) {
                deliveriesBatchesMLD.value!![i].countedQuantity = 0.0
                deliveriesBatchesMLD.value!![i].baseCounted = 0.0
                if (subtract.value!!) {
                    deleteSubtractedAmount(batchNo, deliveriesBatchesMLD.value!![i].materialno!!)
                }
            }
        }
        updateHeaderItemsList(itemNo)
    }

    private fun calculateCountedAmountOfSpecificBatch(batchNo: String, materialNo: String): Double {
        var batchTotalCountedQuantity = 0.0
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].batch == batchNo && deliveriesBatchesMLD.value!![i].materialno == materialNo) {
                batchTotalCountedQuantity += convertUnit.convertToBase(
                    deliveriesBatchesMLD.value!![i].materialno!!,
                    deliveriesBatchesMLD.value!![i].baseUnit!!,
                    deliveriesBatchesMLD.value!![i].baseCounted!!
                )
            }
        }
        return batchTotalCountedQuantity

    }

    private fun updateHeaderItemsList(itemNo: String) {
        var newValue = 0.0
        for (j in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![j].itemno == itemNo) {
                newValue += deliveriesBatchesMLD.value!![j].countedQuantity!!
                deliveriesBatchesMLD.value!![j].requestedQuantity =
                    deliveriesBatchesMLD.value!![j].baseRequestedQuantity
                deliveriesBatchesMLD.value!![j].uom = deliveriesBatchesMLD.value!![j].baseUnit!!
            }
        }
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].itemno == itemNo &&
                deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE &&
                deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                deliveriesItemsMLD.value!![i].countedQuantity = round(newValue)
            }
        }
    }

    private fun getMaterialRequestedQuantity(itemNo: String): HeaderItems? {
        var material: HeaderItems? = null
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].itemno == itemNo &&
                deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE &&
                deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                material = deliveriesItemsMLD.value!![i]
            }
        }
        Log.d("getItemNo02", "value , $itemNo , ${material?.requestedQuantity}")
        return material
    }

    fun isAllItemsQuantityFullyLoad(): Boolean {
        var isFullyLoaded = true
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].countedQuantity != deliveriesItemsMLD.value!![i].requestedQuantity
                && deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                && deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                isFullyLoaded = false
                break
            }
        }
        return isFullyLoaded
    }

    private fun subtractBatchAddedAmount(batchNo: String, materialNo: String) {
        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].batch == batchNo &&
                deliveriesBatchesMLD.value!![i].materialno == materialNo
            ) {
                val requiredBatch = deliveriesBatchesMLD.value!![i]

                val batchBaseUnitQuantity = convertUnit.convertToBase(
                    requiredBatch.materialno!!,
                    requiredBatch.baseUnit!!,
                    requiredBatch.parentBaseRequestedQuantity!!
                )
                val subtractionResult =
                    batchBaseUnitQuantity -
                            calculateCountedAmountOfSpecificBatch(
                                requiredBatch.batch!!,
                                deliveriesBatchesMLD.value!![i].materialno!!
                            )

                requiredBatch.baseRequestedQuantity =
                    convertUnit.convertFromBase(
                        requiredBatch.materialno!!,
                        requiredBatch.baseUnit!!,
                        subtractionResult
                    )

                requiredBatch.requestedQuantity =
                    convertUnit.convertFromBase(
                        requiredBatch.materialno!!,
                        requiredBatch.uom!!,
                        subtractionResult
                    )


            }
        }

    }

    private fun deleteSubtractedAmount(batchNo: String, materialNo: String) {
        subtractBatchAddedAmount(batchNo, materialNo)
    }
    fun autoCountMaterialsAmountNewVersion() {
        undoAutoCountMaterialsAmount()

        deliveriesItemsMLD.value?.forEach { item ->
            val batchesForItem = deliveriesBatchesMLD.value!!
                .filter { it.itemno == item.itemno && it.materialno == item.materialno }
                .sortedWith(compareByDescending<HeaderBatches> {
                    it.batch == BATCH_RETURN_SELLABLE
                }.thenBy { it.expiryDate })

            processSelectedBatches(item, batchesForItem)
        }
    }

    private fun processSelectedBatches(item: HeaderItems, batches: List<HeaderBatches>) {
        item.requestedQuantity = round(item.requestedQuantity!!)
        item.countedQuantity = round(item.countedQuantity!!)
        var remainingQuantity = item.requestedQuantity!! - item.countedQuantity!!

        for (batch in batches) {
            if (remainingQuantity > 0) {
                val quantityToTake = minOf(remainingQuantity, batch.baseRequestedQuantity ?: 0.0)

                item.countedQuantity = round(item.countedQuantity!! + quantityToTake)
                batch.baseCounted = quantityToTake
                remainingQuantity -= quantityToTake

                Log.d("getHeaderBatches234", "${batch.batch} ${item.countedQuantity} ${batch.requestedQuantity} " +
                        "${batch.baseCounted} $remainingQuantity")

                // Your logic to subtract quantities from batches, update UI, etc.
                subtractBatchAddedAmount(batch.batch!!, batch.materialno!!)
            }
        }
    }


    private fun getBatchesForItem(item: HeaderItems): List<TruckBatch> {
        return mRepository.getSelectedBatchExpiration(item, item.salesOrg)
    }

    private fun isBatchValid(item: HeaderItems, batch: HeaderBatches): Boolean {
        return item.itemno == batch.itemno
                && item.materialno == batch.materialno
                && item.itemCategory !in listOf(
            MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE,
            MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
        )
                && batch.bType !in listOf(
            MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE,
            MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
        )
                && item.countedQuantity!! < item.requestedQuantity!!
    }



    fun autoCountMaterialsAmount() {
        autoCountMaterialsAmountNewVersion()
//        undoAutoCountMaterialsAmount()
        /*for (i in deliveriesItemsMLD.value!!.indices) {
            for (j in deliveriesBatchesMLD.value!!.indices) {
                val item = deliveriesItemsMLD.value!![i]
                val itemBatch = deliveriesBatchesMLD.value!![j]
                if (item.itemno == itemBatch.itemno
                    && item.materialno == itemBatch.materialno
                    && item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                    && item.itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                    && itemBatch.bType != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                    && itemBatch.bType != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
                ) {
                    val itemRemainingRequested = item.requestedQuantity!! - item.countedQuantity!!
                    Log.d("alsdksaldksaldsadkasdl" , "True 3 ,${itemBatch.batch} ${item.requestedQuantity} , " +
                            "${item.countedQuantity} ${itemBatch.countedQuantity} ${itemBatch.baseCounted} " +
                            "${itemBatch.baseRequestedQuantity} ${itemBatch.parentBaseRequestedQuantity} " +
                            "$itemRemainingRequested")
                    if (item.countedQuantity != item.requestedQuantity) {
                        if (itemRemainingRequested != 0.0) {
                            if (itemBatch.baseRequestedQuantity!! != 0.0) {
                                if (itemBatch.parentBaseRequestedQuantity!! <= itemRemainingRequested) {
                                    item.countedQuantity =
                                        item.countedQuantity!! + itemBatch.parentBaseRequestedQuantity!!
                                    itemBatch.baseCounted =
                                        itemBatch.parentBaseRequestedQuantity!!
                                    itemBatch.baseRequestedQuantity =
                                        itemBatch.parentBaseRequestedQuantity!! - itemBatch.baseCounted!!
                                    itemBatch.requestedQuantity = itemBatch.baseRequestedQuantity
                                    itemBatch.countedQuantity = itemBatch.baseCounted
                                    subtractBatchAddedAmount(
                                        itemBatch.batch!!,
                                        itemBatch.materialno!!
                                    )
                                } else {
                                    item.countedQuantity =
                                        item.countedQuantity!! + itemRemainingRequested
                                    itemBatch.baseCounted = itemRemainingRequested
                                    itemBatch.baseRequestedQuantity =
                                        itemBatch.parentBaseRequestedQuantity!! - itemBatch.baseCounted!!
                                    itemBatch.requestedQuantity = itemBatch.baseRequestedQuantity
                                    itemBatch.countedQuantity = itemBatch.baseCounted
                                    subtractBatchAddedAmount(
                                        itemBatch.batch!!,
                                        itemBatch.materialno!!
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

    fun undoAutoCountMaterialsAmount() {
        for (i in deliveriesItemsMLD.value!!.indices) {
            if (deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                && deliveriesItemsMLD.value!![i].itemCategory != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                deliveriesItemsMLD.value!![i].countedQuantity = 0.0
            }
        }

        for (i in deliveriesBatchesMLD.value!!.indices) {
            if (deliveriesBatchesMLD.value!![i].bType != MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
                && deliveriesBatchesMLD.value!![i].bType != MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
            ) {
                deliveriesBatchesMLD.value!![i].countedQuantity = 0.0
                deliveriesBatchesMLD.value!![i].baseCounted = 0.0
                deliveriesBatchesMLD.value!![i].uom =
                    deliveriesBatchesMLD.value!![i].baseUnit
                deliveriesBatchesMLD.value!![i].requestedQuantity =
                    deliveriesBatchesMLD.value!![i].parentBaseRequestedQuantity!!
                deliveriesBatchesMLD.value!![i].baseRequestedQuantity =
                    deliveriesBatchesMLD.value!![i].parentBaseRequestedQuantity!!
            }
        }
    }

    //Posting Data and Creating Document for TopUp Request
    fun mapDeliveriesToTruckRequestBody(werks: String?, igort: String?) {
        val driver = customerRepository.getCurrentDriver()
        if (selectedFillUpItem != null) {
            val iSalesDocHeader =
                ISalesDocHeader(
                    salesorg = selectedFillUpItem!!.salesOrg!!,
                    distchannel = driver?.value!!.distChannel,
                    division = userPreference.getDivision()!!,
                    plant = werks ?: "",
                    location = igort ?: "",
                    route = "",
                    driver = selectedFillUpItem!!.driver!!,
                    customer = selectedFillUpItem!!.driver!!,
                    doctype = CONSIGNMENT_POST_ISSUE,
                    exdoc = selectedFillUpItem!!.delivery!!,
                    creationdate = null,
                    creationtiem = null,
                    totalvalue = selectedFillUpItem!!.totalAmount!!.toBigDecimal(),
                    discountValue = "0.0".toBigDecimal(),
                    paymentterm = "",
                    docstatus = "",
                    sapDoc = "",
                    customizeheader = "",
                    visitid = "",
                    visititem = ""
                )
            val salesDocBody =
                SalesDocBody(
                    iSalesDocHeader,
                    mapToExternalHeaderItems(),
                    mapToExternalHeaderBatches()
                )
//            postResults(salesDocBody)
            createDocument(salesDocBody)
        }
    }

    fun mapMaterialsRequestBody(iSalesDocHeader: ISalesDocHeader) {
        val salesDocBody =
            SalesDocBody(
                iSalesDocHeader,
                mapToExternalHeaderItems(),
                mapToExternalHeaderBatches()
            )

//        postResults(salesDocBody)
        createDocument(salesDocBody)
    }

    private fun postResults(salesDocBody: SalesDocBody) {
        if(salesDocBody.iHeader.paymentterm == "A" && isApproved) {
            salesDocBody.iHeader.paymentterm = "C"
        }
      /*  val createDocumentDisposable =
            clientService.createDocument(salesDocBody)
                .subscribeOn(Schedulers.io())
                .retry(NUMBER_OF_RETRIES)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.data[0].messagetype == "S") {
                        createDocumentStatusMLD.value = STATUS_LOADED
                        docNumber!!.invoke(response.data[0].message1)
                        Log.d("mapMaterial", " document ${response.data[0].message1}")
                    } else {
                        createDocumentStatusMLD.value = response.data[0].message2
                    }
                }, {
                    createDocumentStatusMLD.value = STATUS_ERROR
                })

        compositeDisposable.add(createDocumentDisposable)
*/
    }
    fun checkBatchesQuantityItems() : Boolean{
        /*var totalOfBatches: Double
        deliveriesItemsMLD.value!!.forEach { headerItems->
            totalOfBatches = 0.0
                deliveriesBatchesMLD.value!!.forEach { headerBatches->
                if(headerBatches.itemno == headerItems.itemno)
                    if(headerBatches.uom == headerItems.uom) {
                        totalOfBatches += headerBatches.baseCounted?:0.0

                    }
            }
                if (headerItems.requestedQuantity != round(totalOfBatches))
                    return false
        }*/
        return true
    }
    fun checkValidBatchesWithItemsQuantity() : Boolean{
        return true
    }
    private fun mapToExternalHeaderItems(): MutableList<ExternalHeaderItems> {
        val headerItems = mutableListOf<ExternalHeaderItems>()
        for (i in deliveriesItemsMLD.value!!.indices) {
            val deliveryItem = deliveriesItemsMLD.value!![i]
            val selectedQuantity = convertToBase(deliveryItem.materialno!!,
                deliveryItem.uom!!,deliveryItem.countedQuantity!!)
            Log.d("getHeaderItems", "$deliveryItem , $selectedQuantity")
            /*val headerItem = ExternalHeaderItems(
                exdoc = deliveryItem.exdoc!!,
                itemno = deliveryItem.itemno!!,
                itemcategory = deliveryItem.itemCategory!!,
                materialno = deliveryItem.materialno!!,
                quantity =  convertFromBase(deliveryItem.materialno!!,
                    deliveryItem.uom!!,selectedQuantity),
                uom = deliveryItem.uom!!,
                totalvalue = 0.0,
                currency = "",
                customizeitem = "",
                returnreason = ""
            )*/
            //TODO Testing..
            val headerItem = ExternalHeaderItems(
                exdoc = deliveryItem.exdoc!!,
                itemno = deliveryItem.itemno!!,
                itemcategory = deliveryItem.itemCategory!!,
                materialno = deliveryItem.materialno!!,
                quantity =  deliveryItem.requestedQuantity!!,
                uom = deliveryItem.uom!!,
                totalvalue = 0.0,
                currency = "",
                customizeitem = "",
                returnreason = "",
                storageLocation = deliveryItem.storageLocation,
                plant = deliveryItem.plant

            )
            headerItems.add(headerItem)
        }

        return headerItems

    }

    private fun mapToExternalHeaderBatches(): MutableList<ExternalHeaderBatches> {
        val headerBatches = mutableListOf<ExternalHeaderBatches>()
        for (i in deliveriesBatchesMLD.value!!.indices) {
            val deliveryBatch = deliveriesBatchesMLD.value!![i]
            Log.d("getBatchScan", " $deliveryBatch")
            if(deliveryBatch.bType == MATERIAL_DELIVERIES && !isOffload){
                deliveryBatch.baseCounted = deliveryBatch.baseRequestedQuantity
            }
            /*val selectedQuantity = convertToBase(deliveryBatch.materialno!!,
                deliveryBatch.uom!!,deliveryBatch.baseCounted!!)
            */
            //TODO Testing....
            val selectedQuantity = convertToBaseWithoutRounding(deliveryBatch.materialno!!,
                deliveryBatch.uom!!,deliveryBatch.baseCounted!!)

            val materialBaseUnit = getMaterialBaseUnit(deliveryBatch.materialno!!,deliveryBatch.uom!!)
       /*     Log.d("getLogcat", " ${convertToBase(deliveryBatch.materialno!!,
                deliveryBatch.uom!!,deliveryBatch.baseCounted!!)} , " +"" +
                    " ${convertFromBase(deliveryBatch.materialno!!,
                deliveryBatch.uom!!,selectedQuantity)} , " +
                    "${deliveryBatch.baseRequestedQuantity} , " +
                    "${deliveryBatch.countedQuantity} , " +
                    "${deliveryBatch.parentBaseRequestedQuantity} . " +
                    "${deliveryBatch.requestedQuantity}")*/
            /*val headerBatch = ExternalHeaderBatches(
                exdoc = deliveryBatch.exdoc!!,
                itemno = deliveryBatch.itemno!!,
                batch = deliveryBatch.batch!!,
                materialno = deliveryBatch.materialno!!,
                quantity = convertFromBase(deliveryBatch.materialno!!,
                    deliveryBatch.uom!!,selectedQuantity),
                uom = deliveryBatch.baseUnit!!,
                customizebatch = ""
            )*/
            // TODO Under testing.....
            val headerBatch = ExternalHeaderBatches(
                exdoc = deliveryBatch.exdoc!!,
                itemno = deliveryBatch.itemno!!,
                batch = deliveryBatch.batch!!,
                materialno = deliveryBatch.materialno!!,
                quantity = selectedQuantity,
                uom = materialBaseUnit,
                customizebatch = "",
                storageLocation = deliveryBatch.storageLocation?:"",
                plant = deliveryBatch.plant?:""
            )
            Log.d("getBatchScan", " $deliveryBatch , $materialBaseUnit , " +
                    "$selectedQuantity , ${convertFromBase(deliveryBatch.materialno!!,
                        deliveryBatch.uom!!,selectedQuantity)}")
            headerBatches.add(headerBatch)
        }
        return headerBatches
    }

    fun convertFromBase(materialNumber: String, ToUnit: String, Quantity: Double): Double {
        return mRepository.convertFromBase(materialNumber, ToUnit, Quantity)
    }

    fun convertToBase(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        return mRepository.convertToBase(materialNumber, FromUnit, Quantity)
    }
    fun convertToBaseWithoutRounding(materialNumber: String, FromUnit: String, Quantity: Double): Double {
        return mRepository.convertToBaseWithoutRounding(materialNumber, FromUnit, Quantity)
    }

    fun getMaterialBaseUnit(materialNumber: String, FromUnit: String): String {
        return mRepository.getMaterialBaseUnit(materialNumber, FromUnit)
    }

    fun checkCreatedDocument(customer: String) : LiveData<SalesDocHeader>{
        return salesDocRepository.checkCreatedDocument(customer)
    }

    fun finishVisitSuccessfully(iSalesDocHeader: ISalesDocHeader) {
        visitsRepository = VisitsRepository(application)
        val visitItemNum = try {
            iSalesDocHeader.visititem?.toInt()
        } catch (e: Exception) {
            0
        }
        if (visitItemNum != null) {
            visitsRepository.finishVisit(
                visitListID = iSalesDocHeader.visitid,
                visitItemNo = visitItemNum
            )
        }
    }


    //On UnitChanged
    fun onUnitChanged(
        batchNo: String?,
        itemNo: String?,
        materialNo: String?,
        baseUnit: String?,
        baseCounted: Double?,
        batchSelectedUnit: String?,
        batchQuantity: Double?
    ) {
        val newQuantity = convertUnit.convertUnit(
            materialNumber = materialNo!!,
            FromUnit = baseUnit!!,
            ToUnit = batchSelectedUnit!!,
            Quantity = batchQuantity!!
        )

        val newCountedQuantity = convertUnit.convertUnit(
            materialNumber = materialNo,
            FromUnit = baseUnit,
            ToUnit = batchSelectedUnit,
            Quantity = baseCounted!!
        )

        updateBatchCountedAndRequestedQuantityBasedOnUnit(
            batchNo!!,
            itemNo!!,
            newQuantity,
            newCountedQuantity,
            batchSelectedUnit
        )
    }

    private fun updateBatchCountedAndRequestedQuantityBasedOnUnit(
        batchNo: String,
        itemNo: String,
        newQuantity: Double,
        newCountedQuantity: Double,
        newUnit: String
    ) {
        for (i in deliveriesBatchesMLD.value!!.indices) {
            val requiredBatch = deliveriesBatchesMLD.value!![i]
            if (requiredBatch.batch == batchNo && requiredBatch.itemno == itemNo) {
                requiredBatch.uom = newUnit
                requiredBatch.requestedQuantity = newQuantity
                requiredBatch.countedQuantity = newCountedQuantity
            }
        }
    }


    fun getMaterialsUnit(materialNumber: String): List<String> {
        val materialsUnit = convertUnit.getMaterialUnits(materialNumber)
        val units = arrayListOf<String>()
        for (i in materialsUnit.indices) {
            units.add(materialsUnit[i].unit)
        }
        return units.distinct()
    }



    private fun createStorageLocUseCase(): StorageLocationUseCases.StorageLocInvoke {
        return StorageLocUseCaseImpl(StorageLocationRepositoryImpl(storageLocGateway))
    }

    private fun createDocumentUseCase(): CreateDocumentUseCases.CreateDocumentInvoke {
        return CreateDocumentUseCaseImpl(CreateDocumentRepositoryImpl(createDocumentGateway))
    }

  /*  fun getStorageLocation(storageLocationBody: StorageLocationBody) = viewModelScope.launch {
        viewStateStorageLocView.postValue(GetStorageLocViewState.Loading(true))
        when (val response = getStorageLocationUseCase.invoke(storageLocationBody)) {
            is ApiResponse.Success -> {
                val data = response.body
                viewStateStorageLocView.postValue(GetStorageLocViewState.Loading(false))
                viewStateStorageLocView.postValue(GetStorageLocViewState.Data(data))
            }
            is ApiResponse.NetworkError -> viewStateStorageLocView.postValue(
                GetStorageLocViewState.NetworkFailure
            )
            else -> viewStateStorageLocView.postValue(GetStorageLocViewState.Loading(false))
        }
    }*/

    fun createDocument(salesDocBody: SalesDocBody)
    = viewModelScope.launch {
        if(salesDocBody.iHeader.paymentterm == "A" && isApproved) {
            salesDocBody.iHeader.paymentterm = "C"
        }

        viewStateCreateDocument.postValue(CreateDocumentViewState.Loading(true))
        when (val response = createDocumentUseCase.invoke(salesDocBody)) {
            is ApiResponse.Success -> {
                val data = response.body
                viewStateCreateDocument.postValue(CreateDocumentViewState.Loading(false))
                viewStateCreateDocument.postValue(CreateDocumentViewState.Data(data))
            }
            is ApiResponse.NetworkError -> viewStateCreateDocument.postValue(
                CreateDocumentViewState.NetworkFailure
            )
            is ApiResponse.EmptyResponse -> viewStateCreateDocument.postValue(
                CreateDocumentViewState.EmptyResponse
            )
            is ApiResponse.EmptyResponse -> viewStateCreateDocument.postValue(
                CreateDocumentViewState.UnknownError
            )
            else -> viewStateCreateDocument.postValue(CreateDocumentViewState.Loading(false))
        }
    }

}