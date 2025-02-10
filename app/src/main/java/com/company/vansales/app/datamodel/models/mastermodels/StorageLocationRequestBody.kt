package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

data class StorageLocationRequestBody(
    @SerializedName("iMaterials")
    val iMaterials : MutableList<IMaterials?>
)

data class IMaterials(
    @SerializedName("matnr")
    val matnr: String
)

