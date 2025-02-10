package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.localmodels.TransactionsHistory
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.mastermodels.BaseBody
import com.company.vansales.app.datamodel.models.mastermodels.Delivery
import com.company.vansales.app.datamodel.models.mastermodels.Materials
import com.company.vansales.app.datamodel.models.mastermodels.TruckBatch
import com.company.vansales.app.datamodel.models.mastermodels.TruckItem
import com.company.vansales.app.datamodel.models.mastermodels.TruckResponse
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.MaterialsDAO
import com.company.vansales.app.datamodel.room.TransactionsHistoryDAO
import com.company.vansales.app.datamodel.room.TruckBatchDAO
import com.company.vansales.app.datamodel.room.TruckItemDAO
import com.company.vansales.app.datamodel.services.ClientService
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.AppUtils.checkCategory
import com.company.vansales.app.utils.AppUtils.convertToEnglish
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_DAMAGED
import com.company.vansales.app.utils.Constants.MATERIAL_DELIVERIES
import com.company.vansales.app.utils.Constants.MATERIAL_RETURN
import com.company.vansales.app.utils.Constants.MATERIAL_SELLABLE
import com.company.vansales.app.utils.Constants.OPEN_DAY_STOCK
import com.company.vansales.app.utils.Constants.REQUEST_ADD
import com.company.vansales.app.utils.Constants.TRANSACTION_ADD
import com.company.vansales.app.utils.Constants.TRANSACTION_SUBTRACT
import com.company.vansales.app.utils.DocumentsConstants.DELIVERY
import com.company.vansales.app.utils.DocumentsConstants.RETURN_WITHOUT_REFERENCE
import io.reactivex.Observable

class TruckManagementRepository(application: Application) {

    var headerItemsList = listOf<HeaderItems>()
    var headerBatchesList = listOf<HeaderBatches>()
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private var clientService = ClientService.getClient(application)
    private var convertUnit = ConvertUnit(application)
    val truckItemDAO: TruckItemDAO
    val truckBatchDAO: TruckBatchDAO
    val transactionsHistoryDAO: TransactionsHistoryDAO
    private val materialsDAO: MaterialsDAO
    val getTruckItems: LiveData<List<TruckItem>>

    init {
        truckItemDAO = appDB.getTruckItem()
        truckBatchDAO = appDB.getTruckBatch()
        materialsDAO = appDB.getMaterials()
        getTruckItems = truckItemDAO.getTruckItemsLiveData()
        transactionsHistoryDAO = appDB.getTransactionsHistory()

    }

    fun getMaterialByMaterialNo(materialNo: String): Materials {
        return materialsDAO.getMaterialByMaterialNo(materialNo)
    }

    fun getMaterialTruckItems(): List<TruckItem> {
        return truckItemDAO.getAllTruckItems()
    }

    fun getMaterialsTruckItemsSellableOfSpecificItem(materialNo: String): TruckItem {
        return truckItemDAO.getMaterialsTruckItemsSellableOfSpecificItem(materialNo)
    }

    fun getMaterialsTruckBatchesOfSpecificItem(materialNo: String): TruckBatch? {
        return truckBatchDAO.getMaterialsTruckBatchesOfSpecificItem(materialNo)
    }

    fun getMaterialOfSellable(): List<TruckItem> {
        return truckItemDAO.getMaterialsSellable()
    }

    fun getMaterialsOfDamaged(): List<TruckItem> {
        return truckItemDAO.getMaterialsOfDamaged()
    }

    fun getTruckContentRemote(baseBody: BaseBody): Observable<TruckResponse> {
        return clientService.getTruckContent(baseBody)
    }

    fun upsertTruckItems(truckItems: List<TruckItem>, isStartDay: Boolean) {
        truckItemDAO.upsert(truckItems)
        if (isStartDay) {
            mapOpenDayStockQuantity()
        }
    }

    fun mapOpenDayStockQuantity() {
        val truckMaterialNumbers = getAllTruckItemsMaterialNumbers()
        val truckItemsStartDayRecord = arrayListOf<TransactionsHistory>()
        var transactionId = transactionsHistoryDAO.getMaxIdNumber() + 1
        for (i in truckMaterialNumbers.indices) {
            val record = TransactionsHistory(
                transactionId,
                "",
                "",
                "",
                OPEN_DAY_STOCK,
                truckMaterialNumbers[i],
                "",
                getMaterialAllTypesAvailability(truckMaterialNumbers[i]).toString()
            )

            truckItemsStartDayRecord.add(record)
            transactionId++
        }
        transactionsHistoryDAO.upsert(truckItemsStartDayRecord)
    }

    fun getMaterialAllTypesAvailability(materialNo: String): Double {
        val material = truckItemDAO.getTruckItemByMaterialNumber(materialNo)
        var sum = 0.0
        for (i in material.indices) {
            sum += convertToEnglish(material[i].available!!)?.toDouble()?:0.0
        }

        return AppUtils.round(sum)
    }

    fun getTruckItemByBarCode(barcode: String): TruckItem {
        return truckItemDAO.getTruckItemByBarCode(barcode)
    }

    private fun getAllTruckItemsMaterialNumbers(): List<String> {
        val allTruckItems = truckItemDAO.getAllTruckItems()
        val materialNumbers = arrayListOf<String>()
        for (i in allTruckItems.indices) {
            materialNumbers.add(allTruckItems[i].materialNo)

        }
        return materialNumbers.distinct()
    }


    private fun upsertTruckItem(
        truckItem: TruckItem,
        transactionType: String,
        docType: String,
        referenceNumber: String
    ) {
        if (truckItemDAO.isItemAlreadyExists(
                truckItem.materialNo,
                truckItem.salesOrg,
                truckItem.mtype
            )
        ) {
            manageTruckItems(truckItem, docType, referenceNumber, transactionType)
        } else {
            truckItemDAO.upsert(truckItem)
            upsertNewTransactionMaterial(truckItem , referenceNumber , docType)
        }
    }

    private fun upsertNewTransactionMaterial(truckItem: TruckItem , referenceNumber: String , docType: String) {
        val transactionId = transactionsHistoryDAO.getMaxIdNumber() + 1
        val transactionIdOpenStock = transactionsHistoryDAO.getMaxIdNumber() + 2
        val record = TransactionsHistory(
            transactionId,
            docType,
            referenceNumber,
            "",
            TRANSACTION_ADD,
            truckItem.materialNo,
            "",
            truckItem.available?:"0.0"
        )
        val openStockEmptyRecord = TransactionsHistory(
            transactionIdOpenStock,
            docType,
            referenceNumber,
            "",
            OPEN_DAY_STOCK,
            truckItem.materialNo,
            "",
            "0.0"
        )
        transactionsHistoryDAO.upsert(openStockEmptyRecord)
        transactionsHistoryDAO.upsert(record)
    }

    private fun saveTruckTransaction(
        actionType: String,
        truckItem: TruckItem,
        docType: String,
        referenceNumber: String
    ) {
        val transactionId = transactionsHistoryDAO.getMaxIdNumber() + 1
        val transactionsHistory = TransactionsHistory(
            transactionId,
            docType,
            referenceNumber,
            AppUtils.getStringCurrentTime(),
            actionType,
            truckItem.materialNo,
            truckItem.unit,
            truckItem.available?:"0.0"
        )
        transactionsHistoryDAO.upsert(transactionsHistory)
    }


    private fun upsertTruckBatch(truckBatch: TruckBatch, transactionType: String) {
        if (truckBatchDAO.isBatchAlreadyExists(
                truckBatch.materialNo,
                truckBatch.salesOrg,
                truckBatch.batch,
                truckBatch.mtype
            )
        ) {
            when (transactionType) {
                TRANSACTION_SUBTRACT -> {
                    truckBatchDAO.subtractFromTruckBatches(
                        truckBatch.available!!,
                        truckBatch.materialNo,
                        truckBatch.salesOrg,
                        truckBatch.batch,
                        truckBatch.mtype
                    )
                }
                TRANSACTION_ADD -> {
                    truckBatchDAO.addToTruckBatches(
                        truckBatch.available!!,
                        truckBatch.materialNo,
                        truckBatch.salesOrg,
                        truckBatch.batch,
                        truckBatch.mtype
                    )
                }
                else -> {

                }
            }
        } else {
            truckBatchDAO.upsert(truckBatch)
        }
    }
    fun mapDeliveriesIntoTruckItemsAndBatches(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        delivery: Delivery
    ) {
        for (i in headerItems.indices) {
            val headerItem = headerItems[i]
            val truckItem = TruckItem(
                salesOrg = delivery.salesOrg!!,
                isSellable = "",
                description = "",
                materialNo = headerItem.materialno!!,
                customizeField = "",
                barcode = materialsDAO.getMaterialByMaterialNo(headerItem.materialno!!).barcode,
                unit = convertUnit.getMaterialBaseUnit(headerItem.materialno!!),
                plant = "",
                mtype = MATERIAL_CATEGORY_SELLABLE,
                total = headerItem.countedQuantity!!.toDouble(),
                materialGroup = "",
                groupDesc = "",
                available = convertUnit.convertToBase(
                    headerItem.materialno!!,
                    headerItem.uom!!,
                    headerItem.countedQuantity!!
                ).toString()
            )
            upsertTruckItem(
                truckItem,
                TRANSACTION_ADD,
                DELIVERY,
                delivery.delivery!!
            )
        }

        for (i in headerBatches.indices) {
            val headerBatch = headerBatches[i]
            val truckBatch = TruckBatch(
                salesOrg = delivery.salesOrg!!,
                plant = "",
                batch = headerBatch.batch!!,
                count = 0.0,
                materialNo = headerBatch.materialno!!,
                customizeField = "",
                expiryDate = AppUtils.expiryDateChecker(headerBatch.expiryDate!!),
                barcode = "",
                mtype = headerBatch.bType,
                uom = convertUnit.getMaterialBaseUnit(headerBatch.materialno!!),
                available = convertUnit.convertToBase(
                    headerBatch.materialno!!,
                    headerBatch.baseUnit!!,
                    headerBatch.countedQuantity!!.toDouble()
                )
            )
            upsertTruckBatch(truckBatch, TRANSACTION_ADD)
        }
    }

    fun upsertTruckBatches(truckBatches: List<TruckBatch>) {
        truckBatchDAO.upsert(truckBatches)
    }

    fun getAllMaterialTruckItems(): ArrayList<TruckItem> {
        return ArrayList(truckItemDAO.getMaterialsByType(MATERIAL_SELLABLE))
    }

    fun getAllMaterialTruckItemsTransactions(materialNo: String): ArrayList<TransactionsHistory> {
        return ArrayList(transactionsHistoryDAO.getAllTransactionHistory(materialNo))
    }

    fun getSellable(): ArrayList<TruckItem> {
        return ArrayList(truckItemDAO.getMaterialsByType(MATERIAL_SELLABLE))
    }

    //TODO check mtype of delivery materials
    fun getReturns(): ArrayList<TruckItem> {
        return ArrayList(truckItemDAO.getMaterialsByType(MATERIAL_RETURN))
    }

    fun getDamaged(): ArrayList<TruckItem> {
        return ArrayList(truckItemDAO.getMaterialsByType(MATERIAL_DAMAGED))
    }

    //TODO check mtype of delivery materials
    fun getDeliveries(): ArrayList<TruckItem> {
        return ArrayList(truckItemDAO.getMaterialsByType(MATERIAL_DELIVERIES))
    }

    fun getSellableBatchesByMaterialNo(materialNo: String, salesOrg: String): List<TruckBatch> {
        return truckBatchDAO.getBatchesByMaterialNo(
            materialNo,
            MATERIAL_SELLABLE,
//            MATERIAL_DELIVERIES,
            salesOrg
        )
    }

    fun getSellableBatchesAndDeliveriesByMaterialNo(materialNo: String, salesOrg: String): List<TruckBatch> {
        return truckBatchDAO.getBatchesByMaterialNo(
            materialNo,
            MATERIAL_SELLABLE,
            MATERIAL_DELIVERIES,
            salesOrg
        )
    }

    fun getDamagedBatchesByMaterialNo(materialNo: String, salesOrg: String): List<TruckBatch> {
        return truckBatchDAO.getBatchesByMaterialNo(
            materialNo,
            MATERIAL_DAMAGED,
//            "",
            salesOrg
        )
    }


    fun deleteAllTruckContent() {
        truckItemDAO.deleteAllItems()
        truckBatchDAO.deleteAllBatches()
    }

    fun mapSelectedMaterialsIntoTruckItemsAndBatches(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        iSalesDocHeader: ISalesDocHeader
    ) {

        for (i in headerItems.indices) {
            val headerItem = headerItems[i]
            val truckItem = TruckItem(
                salesOrg = iSalesDocHeader.salesorg,
                isSellable = "",
                description = "",
                materialNo = headerItem.materialno!!,
                customizeField = "",
                barcode = materialsDAO.getMaterialByMaterialNo(headerItem.materialno!!).barcode,
                unit = convertUnit.getMaterialBaseUnit(headerItem.materialno!!),
                plant = "",
                mtype = checkCategory(headerItem.itemCategory!!),
                total = convertUnit.convertToBase(
                    headerItem.materialno!!,
                    headerItem.uom!!,
                    headerItem.countedQuantity!!.toDouble()
                ),
                materialGroup = "",
                groupDesc = "",
                available = convertUnit.convertToBase(
                    headerItem.materialno!!,
                    headerItem.uom!!,
                    headerItem.countedQuantity!!.toDouble()
                ).toString()
            )
            if (isBatchOrMaterialReturn(headerItem.itemCategory!!, iSalesDocHeader.paymentterm)||
                iSalesDocHeader.doctype == RETURN_WITHOUT_REFERENCE) {
                if (iSalesDocHeader.paymentterm == "A") {
                    upsertTruckItem(
                        truckItem,
                        REQUEST_ADD,
                        iSalesDocHeader.doctype,
                        iSalesDocHeader.exdoc
                    )
                } else
                    upsertTruckItem(
                        truckItem,
                        TRANSACTION_ADD,
                        iSalesDocHeader.doctype,
                        iSalesDocHeader.exdoc
                    )
            } else if (iSalesDocHeader.paymentterm == "A") {
                upsertTruckItem(
                    truckItem,
                    REQUEST_ADD,
                    iSalesDocHeader.doctype,
                    iSalesDocHeader.exdoc
                )
            } else
                upsertTruckItem(
                    truckItem,
                    TRANSACTION_SUBTRACT,
                    iSalesDocHeader.doctype,
                    iSalesDocHeader.exdoc
                )
        }

        for (i in headerBatches.indices) {
            val headerBatch = headerBatches[i]
            val truckBatch = TruckBatch(
                salesOrg = iSalesDocHeader.salesorg,
                plant = "",
                batch = headerBatch.batch!!,
                count = 0.0,
                materialNo = headerBatch.materialno!!,
                customizeField = "",
                expiryDate = AppUtils.expiryDateChecker(headerBatch.expiryDate!!),
                barcode = "",
                mtype = checkCategory(headerBatch.bType),
                uom = convertUnit.getMaterialBaseUnit(headerBatch.materialno!!),
                available = convertUnit.convertToBase(
                    headerBatch.materialno!!,
                    headerBatch.baseUnit!!,
                    headerBatch.baseCounted!!.toDouble()
                )
            )
            if (isBatchOrMaterialReturn(headerBatch.bType, iSalesDocHeader.paymentterm)) {
                if (iSalesDocHeader.paymentterm == "A")
                    upsertTruckBatch(truckBatch, REQUEST_ADD)
                else
                    upsertTruckBatch(truckBatch, TRANSACTION_ADD)
            } else {
                if (iSalesDocHeader.paymentterm == "A")
                    upsertTruckBatch(truckBatch, REQUEST_ADD)
                else
                    upsertTruckBatch(truckBatch, TRANSACTION_SUBTRACT)
            }
        }


    }


    fun addItemsToTruck(
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>,
        iSalesDocHeader: ISalesDocHeader
    ) {

        for (i in headerItems.indices) {
            val headerItem = headerItems[i]
            val truckItem = TruckItem(
                salesOrg = iSalesDocHeader.salesorg,
                isSellable = "",
                description = "",
                materialNo = headerItem.materialno!!,
                customizeField = "",
                barcode = materialsDAO.getMaterialByMaterialNo(headerItem.materialno!!).barcode,
                unit = convertUnit.getMaterialBaseUnit(headerItem.materialno!!),
                plant = "",
                mtype = checkCategory(headerItem.itemCategory!!),
                total = convertUnit.convertToBase(
                    headerItem.materialno!!,
                    headerItem.uom!!,
                    headerItem.requestedQuantity!!.toDouble()
                ),
                materialGroup = "",
                groupDesc = "",
                available = convertUnit.convertToBase(
                    headerItem.materialno!!,
                    headerItem.uom!!,
                    headerItem.requestedQuantity!!.toDouble()
                ).toString()
            )
            if (isBatchOrMaterialReturn(headerItem.itemCategory!!, iSalesDocHeader.paymentterm)) {
                if (iSalesDocHeader.paymentterm == "A")
                    upsertTruckItem(
                        truckItem,
                        REQUEST_ADD,
                        iSalesDocHeader.doctype,
                        iSalesDocHeader.exdoc
                    )
                else
                    upsertTruckItem(
                        truckItem,
                        TRANSACTION_ADD,
                        iSalesDocHeader.doctype,
                        iSalesDocHeader.exdoc
                    )
            } else {
                upsertTruckItem(
                    truckItem,
                    TRANSACTION_SUBTRACT,
                    iSalesDocHeader.doctype,
                    iSalesDocHeader.exdoc
                )
            }
        }

        for (i in headerBatches.indices) {
            val headerBatch = headerBatches[i]

            val truckBatch = TruckBatch(
                salesOrg = iSalesDocHeader.salesorg,
                plant = "",
                batch = headerBatch.batch!!,
                count = 0.0,
                materialNo = headerBatch.materialno!!,
                customizeField = "",
                expiryDate = AppUtils.expiryDateChecker(headerBatch.expiryDate!!),
                barcode = "",
                mtype = checkCategory(headerBatch.bType),
                uom = convertUnit.getMaterialBaseUnit(headerBatch.materialno!!),
                available = convertUnit.convertToBase(
                    headerBatch.materialno!!,
                    headerBatch.baseUnit!!,
                    headerBatch.baseCounted!!.toDouble()
                )
            )
            if (isBatchOrMaterialReturn(headerBatch.bType, iSalesDocHeader.paymentterm)) {
                if (iSalesDocHeader.paymentterm == "A")
                    upsertTruckBatch(truckBatch, REQUEST_ADD)
                else
                    upsertTruckBatch(truckBatch, TRANSACTION_ADD)
            } else {
                if (iSalesDocHeader.paymentterm == "A")
                    upsertTruckBatch(truckBatch, REQUEST_ADD)
                else
                    upsertTruckBatch(truckBatch, TRANSACTION_SUBTRACT)
            }
        }
    }

    private fun isBatchOrMaterialReturn(itemCategory: String, paymentTerm: String): Boolean {
        return (itemCategory == MATERIAL_CATEGORY_RETURN_SELLABLE ||
                itemCategory == MATERIAL_CATEGORY_RETURN_DAMAGE ||
                itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE ||
                itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE)
    }

    fun initDamagedTruckBatches(
        iSalesDocHeader: ISalesDocHeader,
        headerItems: ArrayList<HeaderItems>
    ): ArrayList<HeaderBatches>? {
        val headerBatches: MutableList<HeaderBatches>? = ArrayList()
        for (i in headerItems.indices) {
            val truckBatches =
                getDamagedBatchesByMaterialNo(
                    headerItems[i].materialno!!,
                    headerItems[i].salesOrg
                )
            for (j in truckBatches.indices) {
                val truckBatch = truckBatches[j]
                val headerBatch = HeaderBatches(
                    exdoc = iSalesDocHeader.exdoc,
                    itemno = headerItems[i].itemno,
                    batch = truckBatch.batch,
                    materialno = headerItems[i].materialno,
                    parentBaseRequestedQuantity = truckBatch.available,
                    baseRequestedQuantity = truckBatch.available,
                    baseUnit = truckBatch.uom,
                    baseCounted = truckBatch.available,
                    requestedQuantity = truckBatch.available,
                    countedQuantity = truckBatch.available,
                    expiryDate = truckBatch.expiryDate,
                    uom = truckBatch.uom,
                    bType = truckBatch.mtype,
                    customizebatch = "",
                    storageLocation = headerItems[i].storageLocation,
                    plant = headerItems[i].plant
                )
                headerBatches!!.add(headerBatch)
            }
        }
        headerItemsList = headerItems
        headerBatchesList = headerBatches!!
        autoCountMaterialsAmount()
        return headerBatches as ArrayList<HeaderBatches>
    }


    fun autoCountMaterialsAmount() {
        for (i in headerItemsList.indices) {
            for (j in headerBatchesList.indices) {
                val item = headerItemsList[i]
                val itemBatch = headerBatchesList[j]
                if (item.itemno == itemBatch.itemno
                    && item.materialno == itemBatch.materialno
                ) {
                    val itemRemainingRequested = item.requestedQuantity!! - item.countedQuantity!!
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

        }
    }

    private fun subtractBatchAddedAmount(batchNo: String, materialNo: String) {
        for (i in headerBatchesList.indices) {
            if (headerBatchesList[i].batch == batchNo &&
                headerBatchesList[i].materialno == materialNo
            ) {
                val requiredBatch = headerBatchesList[i]
                val batchBaseUnitQuantity = convertUnit.convertToBase(
                    requiredBatch.materialno!!,
                    requiredBatch.baseUnit!!,
                    requiredBatch.parentBaseRequestedQuantity!!
                )
                val subtractionResult =
                    batchBaseUnitQuantity -
                            calculateCountedAmountOfSpecificBatch(
                                requiredBatch.batch!!,
                                headerBatchesList[i].materialno!!
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

    private fun calculateCountedAmountOfSpecificBatch(batchNo: String, materialNo: String): Double {
        var batchTotalCountedQuantity = 0.0
        for (i in headerBatchesList.indices) {
            if (headerBatchesList[i].batch == batchNo && headerBatchesList[i].materialno == materialNo) {
                batchTotalCountedQuantity += convertUnit.convertToBase(
                    headerBatchesList[i].materialno!!,
                    headerBatchesList[i].baseUnit!!,
                    headerBatchesList[i].baseCounted!!
                )
            }
        }
        return batchTotalCountedQuantity
    }

    fun deleteDamagedItemsAndBatches() {
        truckItemDAO.deleteDamagedItems(MATERIAL_DAMAGED)
        truckBatchDAO.deleteDamagedBatches(MATERIAL_DAMAGED)
    }

    /*  private fun addToTruckItems(
          truckItem: TruckItem,
          docType: String, referenceNumber: String
      ) {
          truckItemDAO.addToTruckMaterials(
              truckItem.available!!.toDouble(), truckItem.materialNo,
              truckItem.salesOrg, truckItem.mtype
          )
          saveTruckTransaction(TRANSACTION_ADD, truckItem, docType, referenceNumber)
      }
  */
    private fun manageTruckItems(
        truckItem: TruckItem,
        docType: String, referenceNumber: String, transactionType: String
    ) {
        if (transactionType == TRANSACTION_SUBTRACT)
            truckItemDAO.subtractFromTruckMaterials(
                truckItem.available!!.toDouble(), truckItem.materialNo,
                truckItem.salesOrg, truckItem.mtype
            )
        else if (transactionType == TRANSACTION_ADD)
            truckItemDAO.addToTruckMaterials(
                truckItem.available!!.toDouble(), truckItem.materialNo,
                truckItem.salesOrg, truckItem.mtype
            )
        saveTruckTransaction(transactionType, truckItem, docType, referenceNumber)
    }
/*
    private fun requestFromTruckItems(
        truckItem: TruckItem,
        docType: String, referenceNumber: String
    ) {
        truckItemDAO.subtractFromTruckMaterials(
            truckItem.available!!.toDouble(), truckItem.materialNo,
            truckItem.salesOrg, truckItem.mtype
        )
        saveTruckTransaction(RequestAdd, truckItem, docType, referenceNumber)
    }*/
}