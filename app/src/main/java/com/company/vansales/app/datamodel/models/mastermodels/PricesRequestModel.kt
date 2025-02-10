package com.company.vansales.app.datamodel.models.mastermodels

data class PricesRequestModel(
    val driver: String,
    val distChannel: String,
    val salesOrg: String,
    val pageNo: String = "1",
    val pageSize: String = "12000",
    var changeTimestamp: String? = null
) {
    fun withPage(pageNo: String, pageSize: String): PricesRequestModel {
        return copy(pageNo = pageNo, pageSize = pageSize)
    }
}