package com.company.vansales.app.datamodel.models.localmodels.salesdocmodels

import java.io.Serializable

data class HeaderItems(
    var salesOrg: String,
    var distChannel: String,
    var exdoc: String?,
    var itemno: String?,
    var materialno: String?,
    var isSellable: String?,
    var requestedQuantity: Double?,
    var countedQuantity: Double?,
    var materialDescription: String?,
    var materialDescriptionArabic: String,
    var uom: String?,
    var totalvalue: Double?,
    var currency: String?,
    var customizeitem: String?,
    var returnreason: String?,
    var barcodeList: ArrayList<String>?,
    var price: Double?,
    var discount: Double?,
    var itemCategory: String?,
    var totalPrice: Double?,
    var isFocused: Boolean = false,
    var storageLocation: String? = null,
    var plant: String? = null
) : Serializable