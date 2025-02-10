package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity


@Entity(
    tableName = "help_view",
    primaryKeys = ["fieldName", "salesOrg", "fieldValue", "fieldLanguage"]
)
data class HelpView(
    var fieldName: String,
    var salesOrg: String,
    var fieldValue: String,
    var fieldLanguage: String,
    var filedDescrption: String,
    var fieldDescrption2: String
)