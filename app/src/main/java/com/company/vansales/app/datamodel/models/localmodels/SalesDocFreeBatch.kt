package com.company.vansales.app.datamodel.models.localmodels

data class SalesDocFreeBatch(
    var exInvoice: String,
    var itemNum: Int,
    var batch: String,
    var freeItemCounter: String,
    var quantity: Double,
    var field: String
)