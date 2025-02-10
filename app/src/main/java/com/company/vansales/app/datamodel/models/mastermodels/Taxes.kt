package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity

@Entity(tableName = "taxes" , primaryKeys = ["salesOrg","customerCode","materialCode"])
data class Taxes (
    val salesOrg : String,
    val customerCode : String,
    val materialCode : String,
    val condition : String?,
    val condValue : String?,
    val condUnit : String?,
    val condCustiomize : String?
)