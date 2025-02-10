package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocHeader
import com.company.vansales.app.utils.DocumentsConstants

@Dao
abstract class SalesDocHeaderDAO : BaseDao<SalesDocHeader>() {

    @Query("SELECT * FROM sales_doc_header")
    abstract fun getAllSalesDocHeader(): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header")
    abstract fun getAllSalesDocHeaderList(): List<SalesDocHeader>

    @Query("SELECT * FROM sales_doc_header WHERE paymentTerm != 'A'")
    abstract fun getAllSalesDocHeaderWithApprovalStatus(): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE exInvoice = :exInvoiceNumber")
    abstract fun getSalesDocHeaderWithInvoiceNumber(exInvoiceNumber: String): SalesDocHeader

    @Query("SELECT * FROM sales_doc_header WHERE erpInvoice = :erpInvoice")
    abstract fun getSalesDocHeaderWithERPInvoiceNumber(erpInvoice: String): LiveData<SalesDocHeader>

    @Query("SELECT * FROM sales_doc_header WHERE exInvoice = :exInvoiceNumber")
    abstract fun geHeader(exInvoiceNumber: String): SalesDocHeader

    @Query("SELECT * FROM sales_doc_header WHERE customer = :customerNo")
    abstract fun getSalesDocHeaderByCustomerNo(customerNo:String): List<SalesDocHeader>

    @Query("SELECT * FROM sales_doc_header WHERE customer = :customer AND paymentTerm != 'A'")
    abstract fun getCustomerSalesDocHeaders(customer: String): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE docStatus = :status")
    abstract fun getDocumentByStatus(status: String): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE paymentTerm = :paymentTerm")
    abstract fun getDocumentByPaymentTermForApprovals(paymentTerm: String): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE customer = :customer AND paymentTerm = :paymentTerm")
    abstract fun getDocumentByPaymentTermForApprovalsSpecificCustomer(customer: String?,paymentTerm: String): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE paymentTerm = :paymentTerm")
    abstract fun getDocumentByPaymentTermForApprovalsAllCustomers(paymentTerm: String): LiveData<List<SalesDocHeader>>

    @Query("UPDATE sales_doc_header SET paymentTerm = :paymentTerm WHERE exInvoice = :exInvoiceNumber")
    abstract fun updatePaymentTermByExInvoice(paymentTerm: String,
                                          exInvoiceNumber : String)

    @Query("UPDATE sales_doc_header SET docStatus = :status, erpInvoice = :erpInvoice WHERE exInvoice = :exInvoiceNumber ")
    abstract fun updateSalesDocHeaderStatus(
        exInvoiceNumber: String,
        erpInvoice: String,
        status: String
    )
    @Query("UPDATE sales_doc_header SET docStatus = :status, docType = :docType, erpInvoice = :erpInvoice WHERE exInvoice = :exInvoiceNumber ")
    abstract fun updateSalesDocHeaderStatusDocType(
        exInvoiceNumber: String,
        erpInvoice: String,
        docType : String,
        status: String
    )

    @Query("SELECT * FROM sales_doc_header WHERE customer = :customer")
    abstract fun isThereAnyDocument(customer : String) : LiveData<SalesDocHeader>

    @Query("SELECT * FROM sales_doc_header WHERE docType = :topUp OR docType = :loadUp")
    abstract fun getAllRequestsSalesDocHeaders(
        topUp: String = DocumentsConstants.TOP_UP_ORDER,
        loadUp: String = DocumentsConstants.LOAD_ORDER
    ): LiveData<List<SalesDocHeader>>



    @Query("SELECT * FROM sales_doc_header WHERE docType = :cashInvoice OR docType = :creditInvoice OR docType = :btcInvoice OR docType = :freeInvoice OR docType = :billToBillInvoice OR docType = :returnInvoice OR docType = :exchangeInvoice OR docType = :canceled")
    abstract fun getAllInvoicesDocuments(
        cashInvoice: String = DocumentsConstants.CASH_INVOICE,
        creditInvoice: String = DocumentsConstants.CREDIT_INVOICE,
        btcInvoice: String = DocumentsConstants.BTC_INVOICE,
        freeInvoice: String = DocumentsConstants.FOC_INVOICE,
        billToBillInvoice: String = DocumentsConstants.Bill_to_Bill,
        returnInvoice: String = DocumentsConstants.RETURN_WITHOUT_REFERENCE,
        exchangeInvoice: String = DocumentsConstants.ExchangeInvoiceCash,
        canceled: String = DocumentsConstants.CANCEL_INVOICE,
    ): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE docType = :cashInvoice OR docType = :creditInvoice OR docType = :btcInvoice OR docType = :billToBillInvoice" +
            " OR docType = :exchangeCreditZero OR docType =:exchangeCredit OR docType = :exchangeBillToBill OR docType =:exchangeBTBZero OR docType =:exchangeInvoiceCash OR docType =:exchangeCashZero OR docType =:orderCustomer OR docType =:orderCustomer OR docType =:deliveryCustomer")
    abstract fun getAllInvoicesDocumentsForTotalValue(
        cashInvoice: String = DocumentsConstants.CASH_INVOICE,
        creditInvoice: String = DocumentsConstants.CREDIT_INVOICE,
        btcInvoice: String = DocumentsConstants.BTC_INVOICE,
        billToBillInvoice: String = DocumentsConstants.Bill_to_Bill,
        exchangeCreditZero : String = DocumentsConstants.ExchangeInvoiceCreditZero,
        exchangeCredit : String = DocumentsConstants.ExchangeInvoiceCredit,
        exchangeBillToBill : String = DocumentsConstants.ExchangeInvoiceBillToBill,
        exchangeBTBZero : String = DocumentsConstants.ExchangeInvoiceBTBZero,
        exchangeInvoiceCash : String = DocumentsConstants.ExchangeInvoiceCash,
        exchangeCashZero : String = DocumentsConstants.ExchangeInvoiceCashZero,
        orderCustomer : String = DocumentsConstants.CustomerOrder,
        deliveryCustomer : String = DocumentsConstants.CREATE_DELIVERY_INVOICE,
    ): LiveData<List<SalesDocHeader>>

    @Query("SELECT * FROM sales_doc_header WHERE docType = :collection")
    abstract fun getCollectionsList(collection : String = DocumentsConstants.BTB_Payment) : List<SalesDocHeader>

    @Query("SELECT * FROM sales_doc_header WHERE docType = :cashInvoice OR docType = :creditInvoice OR docType = :btcInvoice OR docType = :freeInvoice OR docType = :billToBillInvoice " +
            "OR docType = :returnInvoice OR docType = :exchangeCreditZero OR docType =:exchangeCredit OR docType = :exchangeBillToBill OR docType =:exchangeBTBZero " +
            "OR docType =:exchangeInvoiceCash OR docType =:exchangeCashZero OR docType =:collection" )
    abstract fun getAllInvoicesDocumentsList(
        cashInvoice: String = DocumentsConstants.CASH_INVOICE,
        creditInvoice: String = DocumentsConstants.CREDIT_INVOICE,
        btcInvoice: String = DocumentsConstants.BTC_INVOICE,
        freeInvoice: String = DocumentsConstants.FOC_INVOICE,
        billToBillInvoice: String = DocumentsConstants.Bill_to_Bill,
        returnInvoice: String = DocumentsConstants.RETURN_WITHOUT_REFERENCE,
        exchangeCreditZero : String = DocumentsConstants.ExchangeInvoiceCreditZero,
        exchangeCredit : String = DocumentsConstants.ExchangeInvoiceCredit,
        exchangeBillToBill : String = DocumentsConstants.ExchangeInvoiceBillToBill,
        exchangeBTBZero : String = DocumentsConstants.ExchangeInvoiceBTBZero,
        exchangeInvoiceCash : String = DocumentsConstants.ExchangeInvoiceCash,
        exchangeCashZero : String = DocumentsConstants.ExchangeInvoiceCashZero,
        collection : String = DocumentsConstants.BTB_Payment,
    ): List<SalesDocHeader>

    @Query("SELECT Count(1) FROM sales_doc_header WHERE erpInvoice = :erpInvoiceNumber")
    abstract fun invoiceNumberExists(erpInvoiceNumber: String): Boolean

    @Query("DELETE FROM sales_doc_header")
    abstract fun deleteDataInTable()
}