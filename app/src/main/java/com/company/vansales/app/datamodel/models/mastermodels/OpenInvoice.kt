package com.company.vansales.app.datamodel.models.mastermodels

data class OpenInvoice(
    var invoiceNumber:Int,
    var customer: String,
    var totalAmount:String,
    var paid:Double,
    var remain:Double
)