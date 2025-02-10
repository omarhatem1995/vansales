package com.company.vansales.app.datamodel.models.mastermodels

data class DriverCondition(

    var driver: String,
    var conditionType: String,
    var conditionCounter: String,
    var conditionUnit: String,
    var conditionValue: String,
    var conditionSign: String
)