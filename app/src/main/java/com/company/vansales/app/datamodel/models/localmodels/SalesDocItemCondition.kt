package com.company.vansales.app.datamodel.models.localmodels

data class SalesDocItemCondition(
    var exInvoice: String,
    var itemNum: Int,
    var conditionType: String,
    var conditionCounter: String,
    var conditionValue: String,
    var conditionUnit: String,
    var conditionSign: String
)