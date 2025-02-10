package com.company.vansales.app.datamodel.models.mastermodels

data class CustomerCondition(
    var customerNo: Int,
    var materialNo: Int,
    var conditionType: String,
    var conditionCounter: String,
    var conditionUnit: String,
    var conditionValue: String,
    var conditionSign: String
)