package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

data class ExternalHeaderBatches(
    var exdoc: String,
    var itemno: String,
    var batch: String,
    var materialno: String,
    var quantity: Double,
    var uom: String,
    var customizebatch: String,
    var storageLocation: String,
    var plant: String
)