package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity

@Entity(tableName = "materials" , primaryKeys = ["salesOrg","distChannel","materialNo"])
data class Materials(
    var salesOrg: String,
    var distChannel: String,
    var baseUnit: String?,
    var materialNo: String,
    var materialDescrption: String?,
    var materialGrp: String?,
    var materialType: String?,
    var minShelf: Int?,
    var expiryDate: String?,
    var barcode: String?,
    var salesUnit: String?,
    var grpDesc: String?,
    var oldMat: String?,
    var totalShelf: Int?,
    var materialArabic: String?,
    var grpArabic: String?,
    var yvatTax: String?,
    var yexcTax: String?
)