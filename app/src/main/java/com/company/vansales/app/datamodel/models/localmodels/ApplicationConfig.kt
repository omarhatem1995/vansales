package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity

@Entity(tableName = "application_config" ,primaryKeys = ["id", "salesOrg","appParamter"])
data class ApplicationConfig(
    var id: Int,
    var salesOrg: String,
    var appParamter: String,
    var value: String
)