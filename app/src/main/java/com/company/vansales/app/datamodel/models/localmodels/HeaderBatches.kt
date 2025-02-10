package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels


data class HeaderBatches(
    var exdoc: String?,
    var itemno: String?,
    var batch: String?,
    var materialno: String?,
    var parentBaseRequestedQuantity : Double?,
    var baseRequestedQuantity : Double?,
    var baseUnit : String?,
    var baseCounted: Double?,
    var requestedQuantity: Double?,
    var countedQuantity: Double?,
    var expiryDate : String?,
    var uom: String?,
    var bType : String,
    var customizebatch: String?,
    var storageLocation: String?,
    var plant: String?
)