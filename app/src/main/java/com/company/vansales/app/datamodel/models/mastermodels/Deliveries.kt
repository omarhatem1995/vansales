package com.company.vansales.app.datamodel.models.mastermodels

import java.io.Serializable

data class Deliveries(
    var deliveryBatch: List<DeliveryBatch>,
    var deliveryItem: List<DeliveryItem>
) : Serializable

