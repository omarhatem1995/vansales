package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

data class StorageLocationResponse(
    @SerializedName("matnr") val matnr: String,
    @SerializedName("werks") val werks: String,
    @SerializedName("lgort") val lgort: String
)