package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity

@Entity(
    tableName = "item_price_condition",
    primaryKeys = ["customer", "materialNo", "salesOrg", "distChannel", "condition", "condCounter"]
)
data class ItemPriceCondition(
    var salesOrg: String,
    var distChannel: String,
    var customer: String,
    var materialNo: String,
    var condition: String,
    var condCounter: Int,
    var condValue: Double?,
    var condUnit: String?,
    var condCustiomize: String?,
    var scaleValue: Int?,
    var scaleUnit: String?,
    var condSign: String?,
    var upperLimit: Double?,
    var defaultLimit: Double?,
    var materialGrp: String?,
    var isHeader: String?,
    var isPrice: String?,
    var lowerLimit: Double?,
    var operation: String?,
    var updateTimeStamp : String?
)