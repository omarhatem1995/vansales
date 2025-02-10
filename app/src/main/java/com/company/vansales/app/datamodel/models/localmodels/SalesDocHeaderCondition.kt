package com.company.vansales.app.datamodel.models.localmodels

data class SalesDocHeaderCondition(
    var exInvoice: String,
    var conditionType: String,
    var conditionCounter: String,
    var conditionValue: String,
    var conditionUnit: String,
    var conditionSign: String
)