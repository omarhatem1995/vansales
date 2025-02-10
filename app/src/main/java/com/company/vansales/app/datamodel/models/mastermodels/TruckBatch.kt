package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity

@Entity(
    tableName = "truck_batch",
    primaryKeys = ["salesOrg", "plant", "materialNo", "batch", "mtype"]
)
data class TruckBatch(
    var salesOrg: String,
    var plant: String,
    var batch: String,
    var count: Double?,
    var materialNo: String,
    var customizeField: String?,
    var expiryDate: String?,
    var barcode: String?,
    var mtype: String,
    var uom: String?,
    var available: Double?
)