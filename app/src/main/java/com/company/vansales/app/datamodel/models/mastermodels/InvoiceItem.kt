package com.company.vansales.app.datamodel.models.mastermodels

data class InvoiceItem (
    var invoiceNo:Int,
    var itemNo:Int,
    var materialNo:Int,
    var netValue:Double,
    var unitPrice:Double,
    var quanitity:Int,
    var description:String,
    var itemCategory:String,
    var highLevelRef:String
)