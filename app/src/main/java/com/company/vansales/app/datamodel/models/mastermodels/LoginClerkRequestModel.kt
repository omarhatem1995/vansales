package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class LoginClerkRequestModel (
    @SerializedName("username")
    val username : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("barcode")
    val barcode : String,
    @SerializedName("customerizeBarcode")
    val customerizeBarcode : String? = null
)