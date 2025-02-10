package com.company.vansales.app.datamodel.models.localmodels

data class VisitItem(
    var visitListId: String,
    var visitItemNumber: String,
    var customer: String,
    var startTime: String,
    var endTime: String,
    var startOdemeter: Double,
    var startLatitude: Double,
    var startLongitude: Double,
    var endLatitude: Double,
    var endLongitude: Double,
    var faildReason: String,
    var visitStatus: String
)