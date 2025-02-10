package com.company.vansales.app.datamodel.models.mastermodels
import androidx.room.Entity
import java.math.BigDecimal

@Entity(
    tableName = "prices",
    primaryKeys = ["customer", "materialNo", "salesOrg", "distChannel", "condition"]
)
data class Prices(
    var salesOrg: String,
    var distChannel: String,
    var customer: String,
    var materialNo: String,
    var condition: String,
    var condCounter: Int?,
    var condValue: Double?,
    var condUnit: String?,
    var condCustiomize: String?,
    var scaleValue: Double?,
    var scaleUnit: String?,
    var condSign: String?,
    var upperLimit: Double?,
    var defaultLimit: Double?,
    var materialGrp: String?,
    var isHeader: String?,
    var isPrice: String?,
    var lowerLimit: Double?,
    var operation: String?,
    var updateTimeStamp: String?
)