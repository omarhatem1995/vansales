package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class CollectionsRequestModel (
    @SerializedName("iData")
    var iData : IData,
    @SerializedName("fromDate")
    var fromDate : String,
    @SerializedName("toDate")
    var toDate : String,
) : Serializable

class IData(
    @SerializedName("customer")
    var customer : String,
    @SerializedName("distchannel")
    var distChannel : String,
    @SerializedName("division")
    var division : String,
    @SerializedName("salesorg")
    var salesOrg : String
)