package com.company.vansales.app.datamodel.models.mastermodels

import com.google.gson.annotations.SerializedName

data class CreateCustomer(
    @SerializedName("exDoc")
    var exDoc: String,
    @SerializedName("visitsListID")
    var visitsListID: String,
    @SerializedName("visitListItemNumber")
    var visitListItemNumber: String,
    @SerializedName("salesOrg")
    var salesOrg: String,
    @SerializedName("distChannel")
    var distChannel: String,
    @SerializedName("division")
    var division: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("name3")
    var name3: String,
    @SerializedName("telephone")
    var telephone: String,
    @SerializedName("telephone2")
    var telephone2: String,
    @SerializedName("address")
    var address: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("longitude")
    var longitude: Double?,
    @SerializedName("latitude")
    var latitude: Double?,
    @SerializedName("street2")
    var street2: String,
    @SerializedName("searchTerm1")
    var searchTerm1: String,
    @SerializedName("searchTerm2")
    var searchTerm2: String,
    @SerializedName("houseNumber")
    var houseNumber: String,
    @SerializedName("accountGroup")
    var accountGroup: String,
    @SerializedName("customizeField")
    var customizeField: String
)
