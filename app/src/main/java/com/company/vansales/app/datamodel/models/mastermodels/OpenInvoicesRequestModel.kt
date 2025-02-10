package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OpenInvoicesRequestModel (
    @SerializedName("salesorg")
    var salesOrg : String,
    @SerializedName("distchannel")
    var distChannel : String,
    @SerializedName("division")
    var division : String,
    @SerializedName("customer")
    var customer : String
) : Serializable