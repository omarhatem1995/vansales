package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItems

@Dao
abstract class SalesDocHeaderItemsDAO : BaseDao<SalesDocItems>() {

    @Query("SELECT * FROM sales_doc_item")
    abstract fun getAllSalesDocItems(): LiveData<List<SalesDocItems>>

    @Query("SELECT * FROM sales_doc_item")
    abstract fun getAllSalesDocItemsList(): List<SalesDocItems>

    @Query("SELECT * FROM sales_doc_item WHERE exInvoice = :exInvoice AND itemCategory != :isFree ")
    abstract fun getSalesDocItemsNotFree(exInvoice: String, isFree: String): LiveData<List<SalesDocItems>>

    @Query("SELECT * FROM sales_doc_item WHERE exInvoice = :exInvoice")
    abstract fun getSalesDocItemsWithInvoiceNumber(exInvoice: String): LiveData<List<SalesDocItems>>

    @Query("SELECT * FROM sales_doc_item WHERE exInvoice = :exInvoice")
    abstract fun getAllSalesDocItemsWithInvoiceNumberList(exInvoice: String): List<SalesDocItems>

    @Query("SELECT * FROM sales_doc_item WHERE exInvoice = :exInvoice AND itemCategory = :isFree")
    abstract fun getSalesDocFree(exInvoice: String, isFree: String): LiveData<List<SalesDocItems>>

    @Query("SELECT * FROM sales_doc_item WHERE mType = :type")
    abstract fun getAllSoldItems(type: String): LiveData<List<SalesDocItems>>

    @Query("SELECT * FROM sales_doc_item WHERE exInvoice = :exInvoice")
    abstract fun getItemsWithInvoiceNumber(exInvoice: String): List<SalesDocItems>

    @Query("DELETE FROM sales_doc_item")
    abstract fun deleteDataInTable()
}