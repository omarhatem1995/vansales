package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.salesdocmodels.SalesDocItemBatch
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SalesDocHeaderBatchDAO  : BaseDao<SalesDocItemBatch>() {

    @Query("SELECT * FROM sales_doc_batch")
    abstract fun getAllSalesDocBatches() : LiveData<List<SalesDocItemBatch>>

    @Query("SELECT * FROM sales_doc_batch")
    abstract fun getAllSalesDocBatchesList() : List<SalesDocItemBatch>

    @Query("SELECT * FROM sales_doc_batch WHERE exInvoice = :exInvoiceNumber")
    abstract fun getSalesDocBatches(exInvoiceNumber : String) : List<SalesDocItemBatch>

    @Query("SELECT * FROM sales_doc_batch WHERE exInvoice = :exInvoiceNumber AND materialNo =:materialNo AND quantity =:quantity")
    abstract fun getSalesDocBatches(exInvoiceNumber : String,materialNo : String,quantity : Double)
    : List<SalesDocItemBatch>
    @Query("SELECT * FROM sales_doc_batch WHERE exInvoice = :exInvoiceNumber AND materialNo =:materialNo AND unit =:unit")
    abstract fun getSalesDocBatches(exInvoiceNumber : String,materialNo : String,unit : String)
    : List<SalesDocItemBatch>
    @Query("SELECT * FROM sales_doc_batch WHERE exInvoice = :exInvoiceNumber AND materialNo =:materialNo")
    abstract fun getSalesDocBatches(exInvoiceNumber : String,materialNo : String)
    : List<SalesDocItemBatch>

    @Query("DELETE FROM sales_doc_batch")
    abstract fun deleteDataInTable()

}