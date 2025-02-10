package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.InvoiceNumber
import com.company.vansales.app.datamodel.room.BaseDao

@Dao
abstract class InvoiceNumberDAO : BaseDao<InvoiceNumber>() {

    @Query("SELECT COUNT(*) FROM invoice_number")
    abstract fun getAllRowsInTable(): Int

    @Query("SELECT * FROM invoice_number WHERE route = :route ORDER BY CAST(exInvoice AS INTEGER) DESC LIMIT 1")
    abstract fun getLastNumberInTable(route: String): InvoiceNumber?

    @Query("DELETE FROM invoice_number")
    abstract fun deleteAllInvoiceNumber()
}