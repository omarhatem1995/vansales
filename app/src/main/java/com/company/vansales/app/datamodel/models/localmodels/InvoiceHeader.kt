package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity

@Entity(tableName = "invoice_header", primaryKeys = ["customerNumber","ofr"])
class InvoiceHeader (
    val customerNumber : String,
    val customerName : String,
    val deliverdTo : String,
    val salesManName : String,
    val salesManNumber : String,
    var ofr : String,
    val time : String,
    val date : String,
    val dueDate : String,
    var invoiceNumber : String,
    val paymentTerms : String,
    var route : String? = null,
    val address : String? = null,
    val collectedAmount : String? = null,
    var remainAmount : String? = null,
    var secondInvoice : String = "",
    var invoiceType : String? = null,
    var separated : Boolean? = false
)