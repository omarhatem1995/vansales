package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CollectionsResponseModel (
    @SerializedName("exinvoice")
    var exinvoice : String,
    @SerializedName("invoice")
    var invoice : String,
    @SerializedName("payer")
    var payer : String,
    @SerializedName("soldTo")
    var soldTo : String,
    @SerializedName("total")
    var total : String,
    @SerializedName("remain")
    var remain : String,
    @SerializedName("paid")
    var paid : String,
    @SerializedName("billingDate")
    var billingDate : String,
    @SerializedName("customzieField")
    var customzieField : String
) : Serializable