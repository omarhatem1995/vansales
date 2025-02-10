package com.company.vansales.app.datamodel.models.mastermodels

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "item_batch")
data class ItemBatch(
    var unit: String,
    var batch: String,
    @PrimaryKey
    var itemNo: Int,
    var materialName: String,
    var expiryDate:String,
    var quantity: Int,
    var invoiceNO: Int,
    var neededQuantity:Int
)
