package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class LoginRequestModel (
    @SerializedName("driver")
    val driver : String,
    @SerializedName("username")
    val userName : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("crtPass")
    val crtPass : String? = null //x if he create new password
)