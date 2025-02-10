package com.company.vansales.app.datamodel.models.localmodels

import java.io.Serializable

data class ShowAddModel(
    val type: String,
    val image: Int,
    val header: String,
    val FOCText: String,
    val FOCValue: String,
    val materialGroupFOCValue: String?,
    val materialGroupFOCTotal: String?,
): Serializable
