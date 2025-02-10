package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class LoginClerkResponseModel (
    @SerializedName("type")
    val type :String,
    @SerializedName("msg1")
    val msg1 : String,
    @SerializedName("msg2")
    val msg2 : String,
    @SerializedName("data")
    val data : List<DataClerk>?,
    @SerializedName("success")
    val success : Boolean
)

class DataClerk(
    @SerializedName("name1")
    val name1 :String,
    @SerializedName("companyCode")
    val companyCode : String,
    @SerializedName("salesOrg")
    val salesOrg : String,
    @SerializedName("distChannel")
    val distChannel : String,
    @SerializedName("plant")
    val plant : Boolean,
    @SerializedName("location")
    val location : Boolean,
    @SerializedName("shippingPoint")
    val shippingPoint : Boolean,
    @SerializedName("nameArabic")
    val nameArabic : Boolean,
    @SerializedName("customizeField")
    val customizeField : Boolean
)