package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

import androidx.room.Entity

@Entity(tableName = "sales_doc_batch", primaryKeys = ["exInvoice","itemNum","batch"])
data class SalesDocItemBatch(
    var exInvoice: String,
    var itemNum: Int,
    var batch: String,
    var quantity: Double?,
    var materialNo : String?,
    var unit: String?,
    var bType : String?,
    var storageLocation: String?,
    var plant: String?
)
