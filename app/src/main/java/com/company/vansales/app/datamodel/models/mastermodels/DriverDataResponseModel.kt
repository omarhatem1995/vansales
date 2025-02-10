package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class DriverDataResponseModel (
    @SerializedName("type")
    var type : String,
    @SerializedName("msg1")
    var msg1 : String,
    @SerializedName("msg2")
    var msg2 : String,
    @SerializedName("data")
    var data : List<Data>,
    @SerializedName("status")
    var status : String,
    @SerializedName("success")
    var success : Boolean
)
class Data(
    @SerializedName("salesOrg")
    var salesOrg : String,
    @SerializedName("distChannel")
    var distChannel :String,
    @SerializedName("customer")
    var customer : String,
    @SerializedName("address")
    var address : String,
    @SerializedName("region")
    var region : String,
    @SerializedName("name1")
    var name1 : String,
    @SerializedName("paymentTerm")
    var paymentTerm : String,
    @SerializedName("printOption")
    var printOption : String,
    @SerializedName("mobile")
    var mobile : String,
    @SerializedName("regionArabic")
    var regionArabic : String,
    @SerializedName("credit")
    var credit : String,
    @SerializedName("nameArabic")
    var nameArabic : String,
    @SerializedName("addressArabic")
    var addressArabic : String,
    @SerializedName("currency")
    var currency : String,
    @SerializedName("longitude")
    var longitude : String,
    @SerializedName("customizeField")
    var customizeField : String,
    @SerializedName("city")
    var city : String,
    @SerializedName("latitude")
    var latitude : String,
    @SerializedName("telephone")
    var telephone : String? = null,
/*    @SerializedName("yexcTax")
    var yexcTax :String?,
    @SerializedName("yexcTax")
    var yvatTax :String?,*/

)