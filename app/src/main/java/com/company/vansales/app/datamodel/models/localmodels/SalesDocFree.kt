package com.company.vansales.app.datamodel.models.localmodels

data class SalesDocFree(
    var exInvoice: String,
    var itemNum: Int,
    var freeCounter: String,
    var material: String,
    var description: String,
    var unit: String,
    var category: String
)