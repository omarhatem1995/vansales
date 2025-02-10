package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity


@Entity(tableName = "materials_unit", primaryKeys = ["materialNo","unit"])
data class MaterialsUnit(
    var baseUnit: String?,
    var materialNo: String,
    var unit: String,
    var barcode: String?,
    var numenrator: Double?,
    var denominator: Double?
)