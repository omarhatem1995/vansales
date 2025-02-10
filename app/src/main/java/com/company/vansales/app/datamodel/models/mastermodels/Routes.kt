package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity

@Entity(tableName = "routes", primaryKeys = ["driver", "route"])
data class Routes(
    var name: String?,
    var location: String?,
    var driver: String,
    var salesOrg: String?,
    var distChannel: String?,
    var customizeField: String?,
    var nameArabic: String?,
    var portofolio: String?,
    var route: String,
    var division: String?,
    var cashJournal: String?,
    var plant: String?,
    var shippingPoint: String?,
    var companyCode: String?,
    var routeDate: String?
)
