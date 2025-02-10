package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.InvoiceHeader
import com.company.vansales.app.datamodel.room.BaseDao

@Dao
abstract class InvoiceHeaderDAO : BaseDao<InvoiceHeader>() {


    @Query("SELECT * FROM invoice_header ")
    abstract fun getAllInvoiceHeader(): List<InvoiceHeader>

    @Query("SELECT * FROM invoice_header WHERE ofr = :ofr")
    abstract fun getInvoiceHeader(ofr: String): InvoiceHeader

    @Query("UPDATE invoice_header SET invoiceType =:docType WHERE ofr = :ofr")
    abstract fun cancelInvoiceHeader(ofr: String ,docType:String)
    @Query("SELECT * FROM invoice_header WHERE invoiceNumber = :erpNumber")
    abstract fun getInvoiceHeaderByErpNumber(erpNumber: String ,): InvoiceHeader

    @Query("UPDATE invoice_header SET invoiceNumber = :erpInvoice WHERE ofr = :exInvoiceNumber ")
    abstract fun updateSalesDocHeaderStatusDocType(
        exInvoiceNumber: String,
        erpInvoice: String,
    )

    @Query("DELETE FROM invoice_header")
    abstract fun deleteAllInvoiceHeader()
}