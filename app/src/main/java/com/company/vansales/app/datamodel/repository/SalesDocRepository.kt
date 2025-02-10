package com.company.vansales.app.datamodel.repository

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ExternalHeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderBatches
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.HeaderItems
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.ISalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocBody
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItemBatch
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems
import com.company.vansales.app.datamodel.models.mastermodels.Customer
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.CustomersDAO
import com.company.vansales.app.datamodel.room.InvoiceHeaderDAO
import com.company.vansales.app.datamodel.room.InvoiceNumberDAO
import com.company.vansales.app.datamodel.room.RoutesDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderBatchDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderDAO
import com.company.vansales.app.datamodel.room.SalesDocHeaderItemsDAO
import com.company.vansales.app.datamodel.room.VisitsDAO
import com.company.vansales.app.utils.AppUtils
import com.company.vansales.app.utils.Constants
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_EXCHANGE_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_FREE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_RETURN_DAMAGE
import com.company.vansales.app.utils.Constants.MATERIAL_CATEGORY_RETURN_SELLABLE
import com.company.vansales.app.utils.Constants.MATERIAL_DAMAGED
import com.company.vansales.app.utils.Constants.MATERIAL_SELLABLE
import com.company.vansales.app.utils.DocumentsConstants.CANCEL_INVOICE
import com.company.vansales.app.utils.enums.DocumentsEnum

class SalesDocRepository(application : Application) {

    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private val invoiceNumberDAO: InvoiceNumberDAO
    private val invoiceHeaderDAO: InvoiceHeaderDAO
    private val salesDocHeaderDAO: SalesDocHeaderDAO
    private val visitsDAO: VisitsDAO
    private val salesDocHeaderItemsDAO: SalesDocHeaderItemsDAO
    private val salesDocHeaderBatchesDAO: SalesDocHeaderBatchDAO
    private val customerDAO: CustomersDAO
    private val routesDAO: RoutesDAO


    init {
        salesDocHeaderDAO = appDB.getSalesDocHeader()
        salesDocHeaderItemsDAO = appDB.getSalesDocItems()
        salesDocHeaderBatchesDAO = appDB.getSalesDocBatches()
        invoiceNumberDAO = appDB.getInvoiceNumber()
        invoiceHeaderDAO = appDB.getInvoiceHeader()
        customerDAO = appDB.getCustomers()
        routesDAO = appDB.getRoutes()
        visitsDAO = appDB.getVisits()

    }

    fun getAllInvoiceHeader() : List<InvoiceHeader> {
        return invoiceHeaderDAO.getAllInvoiceHeader()
    }
    fun getInvoiceHeader(ofr: String ) : InvoiceHeader {
        return invoiceHeaderDAO.getInvoiceHeader(ofr)
    }
    fun getInvoiceHeaderByErp(erpNumber: String ) : InvoiceHeader {
        return invoiceHeaderDAO.getInvoiceHeaderByErpNumber(erpNumber)
    }
    fun cancelInvoiceHeader(erpNumber: String , documentType :String ) {
        invoiceHeaderDAO.cancelInvoiceHeader(erpNumber,documentType)
    }
    fun addHeader(invoiceHeader : InvoiceHeader){
        invoiceHeaderDAO.upsert(invoiceHeader)
    }
    fun addInvoiceNumber(invoiceNumber :String , route : String){
        invoiceNumberDAO.upsert(InvoiceNumber(invoiceNumber , route))
    }
    fun getAllSalesDocHeader(): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getAllSalesDocHeader()
    }
    fun getAllSalesDocHeaderList(): List<SalesDocHeader> {
        return salesDocHeaderDAO.getAllSalesDocHeaderList()
    }
    fun getAllSalesDocHeaderWithStatusApproval(): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getAllSalesDocHeaderWithApprovalStatus()
    }

    fun getDocumentByStatus(status: String): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getDocumentByStatus(status)
    }

    fun getDocumentByPaymentTermForApprovalsSpecificCustomer(customer : String?, paymentTerm: String): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getDocumentByPaymentTermForApprovalsSpecificCustomer(customer,paymentTerm)
    }

    fun getDocumentByPaymentTermForApprovalsAllCustomers( paymentTerm: String): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getDocumentByPaymentTermForApprovalsAllCustomers(paymentTerm)
    }

    fun getDocumentByPaymentTermForApprovals(paymentTerm: String): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getDocumentByPaymentTermForApprovals(paymentTerm)
    }

    fun getSalesDocHeaderByCustomerNo(customerNo:String): List<SalesDocHeader> {
        return salesDocHeaderDAO.getSalesDocHeaderByCustomerNo(customerNo)
    }
    fun getVisitHeaderData(): Visits {
        return visitsDAO.getVisitHeaderData()
    }

    fun updateExInvoice(paymentTerm: String,invoice: String) {
        return salesDocHeaderDAO.updatePaymentTermByExInvoice(paymentTerm = paymentTerm
            ,exInvoiceNumber = invoice)
    }

    fun saveDocument(
        exInvoiceNumber: String,
        mCurrentLocation: Location?,
        iSalesDocHeader: ISalesDocHeader,
        headerItems: List<HeaderItems>,
        headerBatches: List<HeaderBatches>?,
        erpInvoiceNumber: String?,
        status: DocumentsEnum
    ) {
        val exNo: String = exInvoiceNumber
        val exSecondNo: String? = iSalesDocHeader.secondExDoc
        val erpNo = erpInvoiceNumber ?: ""
        if(!salesDocHeaderDAO.invoiceNumberExists(erpNo) || erpNo.isEmpty()) {
            invoiceNumberDAO.upsert(InvoiceNumber(exNo, iSalesDocHeader.route))
            if(exSecondNo!=null){
                invoiceNumberDAO.upsert(InvoiceNumber(exSecondNo, iSalesDocHeader.route))
            }
            mapIntoSalesDocHeader(
                mCurrentLocation,
                iSalesDocHeader,
                erpNo,
                exNo,
                status.name
            )
            Log.d("enteredCreate", "Header Items : ${headerItems.size}")
            mapIntoSalesDocHeaderItems(headerItems, exNo)
            mapIntoSalesDocHeaderBatches(headerBatches, exNo)
        }
    }
 fun saveDocument(
        exInvoiceNumber: String,
        mCurrentLocation: Location?,
        iSalesDocHeader: ISalesDocHeader,
        erpInvoiceNumber: String?,
        status: DocumentsEnum
    ) {
        val exNo: String = exInvoiceNumber
        val exSecondNo: String? = iSalesDocHeader.secondExDoc
        val erpNo = erpInvoiceNumber ?: ""
        if(!salesDocHeaderDAO.invoiceNumberExists(erpNo) || erpNo.isEmpty()) {
            invoiceNumberDAO.upsert(InvoiceNumber(exNo, iSalesDocHeader.route))
            if(exSecondNo!=null){
                invoiceNumberDAO.upsert(InvoiceNumber(exSecondNo, iSalesDocHeader.route))
            }

          /*  if (erpNo.isNotEmpty()) {
                if (iSalesDocHeader.doctype == Documentspickup_return ||
                    iSalesDocHeader.doctype == DocumentsConsignment_pick_up
                ) {
                    exNo = erpNo
                    erpNo = ""
                }
            }*/
            mapIntoSalesDocHeader(
                mCurrentLocation,
                iSalesDocHeader,
                erpNo,
                exNo,
                status.name
            )
        }
    }


    private fun mapIntoSalesDocHeader(
        mCurrentLocation: Location?,
        iSalesDocHeader: ISalesDocHeader,
        erpInvoiceNumber: String?,
        exInvoiceNumber: String,
        status: String
    ) {
        var longitude = 0.0
        if(iSalesDocHeader.longitude != null && iSalesDocHeader.longitude!!.trim() != "null")
            longitude = iSalesDocHeader.longitude!!.toDouble()
        var latitude = 0.0
        if(iSalesDocHeader.latitude != null && iSalesDocHeader.latitude!!.trim() != "null")
            latitude = iSalesDocHeader.latitude!!.toDouble()


        val salesDocHeader = SalesDocHeader(
            salesOrg = iSalesDocHeader.salesorg,
            distChannel = iSalesDocHeader.distchannel,
            exInvoice = exInvoiceNumber,
            erpInvoice = erpInvoiceNumber,
            location = iSalesDocHeader.location,
            plant = iSalesDocHeader.plant,
            customer = iSalesDocHeader.customer,
            docType = iSalesDocHeader.doctype,
            docStatus = status,
            totalValue = iSalesDocHeader.totalvalue.toString(),
            totalDiscount = iSalesDocHeader.discountValue.toString(),
            visitListId = iSalesDocHeader.visitid,
            visitItemNumber = iSalesDocHeader.visititem,
            longitude = longitude,
            latitude = latitude,
            driver = iSalesDocHeader.driver,
            costumer = iSalesDocHeader.customer,
            paymentTerm = iSalesDocHeader.paymentterm,
            visitId = iSalesDocHeader.visitid,
            visitItem = iSalesDocHeader.visititem,
            creationDate = AppUtils.getFormatForEndDay(),
            creationTime = AppUtils.getFormatForEndDay(),
            secondExInvoice = iSalesDocHeader.secondExDoc?:""
        )
        salesDocHeaderDAO.upsert(salesDocHeader)
    }


    private fun mapIntoSalesDocHeaderItems(
        headerItems: List<HeaderItems>,
        exInvoiceNumber: String
    ) {
        Log.d("headerItemsInMapIntoSalesDoc", "${headerItems.size}")
        for (i in headerItems.indices) {
            val headerItem = headerItems[i]
            val salesDocItems = SalesDocItems(
                exInvoice = exInvoiceNumber,
                itemNum = headerItem.itemno!!.toInt(),
                material = headerItem.materialno,
                description = headerItem.materialDescription,
                descriptionArabic = headerItem.materialDescriptionArabic,
                quantity = headerItem.requestedQuantity,
                price = headerItem.price,
                discount = headerItem.discount,
                unit = headerItem.uom,
                itemCategory = checkItemCategory(headerItem),
                totalPrice = headerItem.totalPrice,
                currency = headerItem.currency,
                mType = checkItemType(headerItem),
                storageLocation = headerItem.storageLocation,
                plant = headerItem.plant
            )
            salesDocHeaderItemsDAO.upsert(salesDocItems)
        }

    }

    private fun mapIntoSalesDocHeaderBatches(
        headerBatches: List<HeaderBatches>?,
        exInvoiceNumber: String
    ) {
        if (!headerBatches.isNullOrEmpty()) {

            for (i in headerBatches.indices) {
                val headerBatch = headerBatches[i]
                val salesDocItemBatch = SalesDocItemBatch(
                    exInvoice = exInvoiceNumber,
                    itemNum = headerBatch.itemno!!.toInt(),
                    batch = headerBatch.batch!!,
                    quantity = headerBatch.baseCounted,
                    materialNo = headerBatch.materialno,
                    unit = headerBatch.baseUnit,
                    bType = checkBatchType(headerBatch),
                    storageLocation = headerBatch.storageLocation,
                    plant = headerBatch.plant
                )
                if(salesDocItemBatch.batch.isNotEmpty()||salesDocItemBatch.batch!="")
                salesDocHeaderBatchesDAO.upsert(salesDocItemBatch)
            }
        }

    }

    fun checkLastInvoice(route : String) : Boolean{
        return invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice!=null && invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice != "null"
    }

    fun buildExInvoiceNumber(route: String): String {
        val lastInvoice = invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice
        if(lastInvoice != "null" && lastInvoice != null) {
            val lastInvoiceNumber = invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice?.toInt()
            Log.d(
                "calculateBuild", "${
                    route.replaceFirstChar { "6" } + lastInvoiceNumber?.let {
                        AppUtils.calculateExInvoiceNumber(
                            it
                        )
                    }
                }")
            return route.replaceFirstChar { "6" } +
//                INVOICE_NUMBER_CONST +
                    lastInvoiceNumber?.let {
                        AppUtils.calculateExInvoiceNumber(
                            it
                        )
                    }
        }else{
            return ""
        }
    }
    fun buildExInvoiceNumberNew(route: String): String {
        val lastInvoice = invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice
        if (lastInvoice != "null" && lastInvoice != null) {
            val lastInvoiceNumber = try {
                lastInvoice.toLong() // Use Long instead of Int
            } catch (e: NumberFormatException) {
                Log.e("Error", "Invalid invoice number: $lastInvoice", e)
                null
            }

            if (lastInvoiceNumber != null) {
                Log.d("calculateBuild", "${lastInvoiceNumber + 1}")
                return (lastInvoiceNumber + 1).toString()
            } else {
                return ""
            }
        } else {
            return ""
        }
    }

    fun buildExInvoiceNumberFree(route: String): String {
        val lastInvoiceNumber = invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice?.toInt()
        return route.replaceFirstChar { "6" }  +
//                INVOICE_NUMBER_CONST +
                lastInvoiceNumber?.let {
                    AppUtils.calculateExInvoiceNumberFree(
                        it
                    )
                }
    }
    fun buildExInvoiceNumberFreeNew(route: String): String {
        val lastInvoiceNumber = invoiceNumberDAO.getLastNumberInTable(route)?.exInvoice?.toLong()
        return if (lastInvoiceNumber != null) {
            return (lastInvoiceNumber + 2).toString()
        }else{
            return ""
        }
    }

    fun deleteAllInvoices(){
        return invoiceNumberDAO.deleteAllInvoiceNumber()
    }

    fun deleteAllInvoiceHeaders(){
        return invoiceHeaderDAO.deleteAllInvoiceHeader()
    }

    fun getCustomerByCustomerNumber(
        customerID: String,
        salesOrg: String,
        distChannel: String
    ): Customer? {
        return customerDAO.getCustomerByID(customerID, salesOrg, distChannel)
    }

    fun getSalesDocHeader(exInvoiceNumber: String): SalesDocHeader {
        return salesDocHeaderDAO.getSalesDocHeaderWithInvoiceNumber(exInvoiceNumber)
    }

    fun getSalesDocHeaderWithERPInvoiceNumber(erpInvoiceNumber: String): LiveData<SalesDocHeader> {
        return salesDocHeaderDAO.getSalesDocHeaderWithERPInvoiceNumber(erpInvoiceNumber)
    }

    fun getAllSalesDocItems(): LiveData<List<SalesDocItems>> {
        return salesDocHeaderItemsDAO.getAllSalesDocItems()
    }

    fun getAllSalesDocItemsList(): List<SalesDocItems> {
        return salesDocHeaderItemsDAO.getAllSalesDocItemsList()
    }

    fun getAllSalesDocItemsNotFree(exInvoice: String): LiveData<List<SalesDocItems>> {
        return salesDocHeaderItemsDAO.getSalesDocItemsNotFree(exInvoice, MATERIAL_CATEGORY_FREE)
    }

    fun getSalesDocItemsWithInvoiceNumber(exInvoice: String): LiveData<List<SalesDocItems>> {
        return salesDocHeaderItemsDAO.getSalesDocItemsWithInvoiceNumber(exInvoice)
    }

    fun getAllSalesDocItemsWithInvoiceNumberList(exInvoice: String): List<SalesDocItems> {
        return salesDocHeaderItemsDAO.getAllSalesDocItemsWithInvoiceNumberList(exInvoice)
    }

    fun getAllSalesDocFree(exInvoice: String): LiveData<List<SalesDocItems>> {
        return salesDocHeaderItemsDAO.getSalesDocFree(exInvoice, MATERIAL_CATEGORY_FREE)
    }

    fun getAllSoldItems(): LiveData<List<SalesDocItems>> {
        return salesDocHeaderItemsDAO.getAllSoldItems(MATERIAL_SELLABLE)
    }

    fun deleteAllSalesDocHeaderItems(){
        return salesDocHeaderItemsDAO.deleteDataInTable()
    }
    fun deleteAllSalesDocHeader(){
        return salesDocHeaderDAO.deleteDataInTable()
    }
    fun deleteAllSalesDocBatches(){
        return salesDocHeaderBatchesDAO.deleteDataInTable()
    }
    fun getAllSalesDocBatches() : LiveData<List<SalesDocItemBatch>>{
        return salesDocHeaderBatchesDAO.getAllSalesDocBatches()
    }
    fun getAllSalesDocBatchesList() : List<SalesDocItemBatch>{
        return salesDocHeaderBatchesDAO.getAllSalesDocBatchesList()
    }
    fun getAllSalesDocBatchesList(exInvoice: String) : List<SalesDocItemBatch>{
        return salesDocHeaderBatchesDAO.getSalesDocBatches(exInvoice)
    }
    fun postOfflineDocument(exInvoice: String,routing: String = "",location: String = ""): SalesDocBody {
        val salesDocHeader = salesDocHeaderDAO.geHeader(exInvoice)
        val salesDocBatches = salesDocHeaderBatchesDAO.getSalesDocBatches(exInvoice)
        val salesDocItems = salesDocHeaderItemsDAO.getItemsWithInvoiceNumber(exInvoice)
//        val route = salesDocHeader.exInvoice.let { routesDAO.getRouteDetailsById(it.take(4)) }
        val route = routesDAO.getRoutesList()[0]
        val externalItemList = mutableListOf<ExternalHeaderItems>()
        val externalBatchesList = mutableListOf<ExternalHeaderBatches>()


        val iSalesDocHeader = ISalesDocHeader(
            salesorg = salesDocHeader.salesOrg!!,
            distchannel = salesDocHeader.distChannel!!,
            division = route.division ?: "",
            plant = routing,
            location = location,
            route = route.route,
            driver = salesDocHeader.driver ?: "",
            customer = salesDocHeader.customer ?: "",
            doctype = salesDocHeader.docType ?: "",
            exdoc = exInvoice,
            creationdate = null,
            creationtiem = null,
            totalvalue = salesDocHeader.totalValue!!.toBigDecimal(),
            discountValue = salesDocHeader.totalValue!!.toBigDecimal(),
            paymentterm = salesDocHeader.paymentTerm ?: "",
            docstatus = salesDocHeader.docStatus ?: "",
            customizeheader = "",
            sapDoc = "",
            visitid = salesDocHeader.visitId!!,
            visititem = salesDocHeader.visitItem!!
        )

        for (i in salesDocItems.indices) {
            val salesDocItem = salesDocItems[i]
            val externalItem = ExternalHeaderItems(
                exdoc = exInvoice,
                itemno = salesDocItem.itemNum.toString(),
                itemcategory = salesDocItem.itemCategory!!,
                materialno = salesDocItem.material!!,
                quantity = salesDocItem.quantity!!,
                uom = salesDocItem.unit!!,
                totalvalue = salesDocItem.totalPrice!!,
                currency = salesDocItem.currency!!,
                customizeitem = "",
                returnreason = ""
            )
            externalItemList.add(externalItem)
        }

        for (i in salesDocBatches.indices) {
            val salesDocBatch = salesDocBatches[i]
            val externalBatch = ExternalHeaderBatches(
                exdoc = exInvoice,
                itemno = salesDocBatch.itemNum.toString(),
                batch = salesDocBatch.batch,
                materialno = salesDocBatch.materialNo!!,
                quantity = salesDocBatch.quantity!!,
                uom = salesDocBatch.unit!!,
                customizebatch = "",
                storageLocation = salesDocBatch.storageLocation?:"",
                plant = salesDocBatch.plant?:""
            )
            externalBatchesList.add(externalBatch)
        }

        return SalesDocBody(iSalesDocHeader, externalItemList, externalBatchesList)
    }


    fun updateSalesDocHeaderStatus(exInvoiceNumber: String, erpInvoice: String) {
        salesDocHeaderDAO.updateSalesDocHeaderStatus(
            exInvoiceNumber,
            erpInvoice,
            DocumentsEnum.POSTED.name
        )
    }

    fun cancelSalesDocHeaderStatus(exInvoiceNumber: String, erpInvoice: String) {
        salesDocHeaderDAO.updateSalesDocHeaderStatusDocType(
            exInvoiceNumber,
            erpInvoice,
            CANCEL_INVOICE,
            DocumentsEnum.CANCELED.name
        )
        invoiceHeaderDAO.updateSalesDocHeaderStatusDocType(
            exInvoiceNumber,
            erpInvoice
        )
    }

    fun checkCreatedDocument(customer: String) : LiveData<SalesDocHeader>{
       return salesDocHeaderDAO.isThereAnyDocument(customer)
    }

    fun getSalesDocBatchesByExInvoice(exInvoice: String , materialNo : String, quantity :Double)
    : List<SalesDocItemBatch>{
       return salesDocHeaderBatchesDAO.getSalesDocBatches(exInvoice,materialNo,quantity)
    }
    fun getSalesDocBatchesByExInvoice(exInvoice: String , materialNo : String, unit :String)
    : List<SalesDocItemBatch>{
       return salesDocHeaderBatchesDAO.getSalesDocBatches(exInvoice,materialNo,unit)
    }
    fun getSalesDocBatchesByExInvoice(exInvoice: String , materialNo : String)
    : List<SalesDocItemBatch>{
       return salesDocHeaderBatchesDAO.getSalesDocBatches(exInvoice,materialNo)
    }

    private fun checkItemType(headerItem: HeaderItems): String {
        return if (headerItem.itemCategory == MATERIAL_CATEGORY_RETURN_SELLABLE ||
            headerItem.itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE ||
            headerItem.itemCategory == MATERIAL_CATEGORY_EXCHANGE_SELLABLE
        ) {
            MATERIAL_SELLABLE
        } else if (headerItem.itemCategory == MATERIAL_CATEGORY_RETURN_DAMAGE ||
            headerItem.itemCategory == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
        ) {
            MATERIAL_DAMAGED
        } else {
            headerItem.itemCategory!!
        }
    }

    private fun checkBatchType(headerBatch: HeaderBatches): String {
        return if (headerBatch.bType == MATERIAL_CATEGORY_RETURN_SELLABLE ||
            headerBatch.bType == MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE ||
            headerBatch.bType == MATERIAL_CATEGORY_EXCHANGE_SELLABLE
        ) {
            MATERIAL_SELLABLE
        } else if (headerBatch.bType == MATERIAL_CATEGORY_RETURN_DAMAGE ||
            headerBatch.bType == MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
        ) {
            MATERIAL_DAMAGED
        } else {
            headerBatch.bType
        }
    }

    private fun checkItemCategory(headerItem: HeaderItems): String {
        return when (headerItem.itemCategory) {
            MATERIAL_CATEGORY_RETURN_SELLABLE -> MATERIAL_SELLABLE
            MATERIAL_CATEGORY_RETURN_DAMAGE -> MATERIAL_DAMAGED
            else -> headerItem.itemCategory!!
        }
    }

    fun getCustomerSalesDocHeaders(customer: String): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getCustomerSalesDocHeaders(customer)
    }

    fun getAllRequestsSalesDocHeaders(): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getAllRequestsSalesDocHeaders()
    }

    fun getAllInvoicesDocuments(): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getAllInvoicesDocuments()
    }

    fun getAllInvoicesDocumentsForTotalValue(): LiveData<List<SalesDocHeader>> {
        return salesDocHeaderDAO.getAllInvoicesDocumentsForTotalValue()
    }

    fun getAllInvoicesDocumentsList(): List<SalesDocHeader> {
        return salesDocHeaderDAO.getAllInvoicesDocumentsList()
    }
    fun getCollectionsList(): List<SalesDocHeader> {
        return salesDocHeaderDAO.getCollectionsList()
    }

    fun updateHeader(invoiceHeader: InvoiceHeader) {
            val existingInvoiceHeader = invoiceHeaderDAO.getInvoiceHeader(invoiceHeader.ofr) // Get the existing invoice header from the DAO

            // Update the fields if new values are provided
            existingInvoiceHeader.ofr = invoiceHeader.ofr
            existingInvoiceHeader.invoiceNumber = invoiceHeader.invoiceNumber

            invoiceHeaderDAO.upsert(existingInvoiceHeader) // Update the invoice header in the DAO
    }

}
