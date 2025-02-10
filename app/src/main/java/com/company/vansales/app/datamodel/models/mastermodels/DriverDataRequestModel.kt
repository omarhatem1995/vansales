package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

class DriverDataRequestModel (
    @SerializedName("salesorg")
    var salesOrg : String,
    @SerializedName("distchannel")
    var distChannel : String,
    @SerializedName("division")
    var division : String,
    @SerializedName("customer")
    var customer : String,
    @SerializedName("driver")
    var driver : String,
)