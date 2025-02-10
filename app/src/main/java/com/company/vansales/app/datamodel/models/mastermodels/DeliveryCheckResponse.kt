package com.company.vansales.app.datamodel.models.mastermodels

data class DeliveryCheckResponse(
    var type: String,
    var msg1: String,
    var msg2: String,
    var data : Deliveries
)

data class DeliveriesBody(var documnetno: String)