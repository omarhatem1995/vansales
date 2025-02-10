package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class LoginResponseModel (
    @SerializedName("type")
    val type :String,
    @SerializedName("msg1")
    val msg1 : String?,
    @SerializedName("msg2")
    val msg2 : String,
    @SerializedName("data")
    val data : String,
    @SerializedName("success")
    val success : Boolean
)