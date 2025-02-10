package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity

@Entity(tableName = "invoice_number", primaryKeys = ["exInvoice", "route"])
data class InvoiceNumber(
    var exInvoice: String,
    var route : String
)