package com.company.vansales.app.datamodel.models.mastermodels

import java.io.Serializable

data class DeliveryBatch(
    var batch: String,
    var materialNo: String,
    var unit: String,
    var quantity: Double,
    var itemNo: String,
    var delivery: String,
    var totalAmount: Double,
    var pickingStatus: String,
    var storageLocation: String,
    var plant: String

) : Serializable