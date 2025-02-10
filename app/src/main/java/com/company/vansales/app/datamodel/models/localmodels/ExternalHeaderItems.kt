package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

data class ExternalHeaderItems(
    var exdoc: String,
    var itemno: String,
    var itemcategory: String,
    var materialno: String,
    var quantity: Double,
    var uom: String,
    var totalvalue: Double,
    var currency: String,
    var customizeitem: String,
    var returnreason: String,
    var storageLocation: String? = null,
    var plant: String? = null
)