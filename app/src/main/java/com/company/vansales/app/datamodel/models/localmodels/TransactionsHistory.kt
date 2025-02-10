package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "transactions_history"
)
data class TransactionsHistory(

    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var docType : String,
    var referenceNumber : String,
    var timeStamp: String,
    var actionType: String?,
    var materialNumber: String,
    var unit: String?,
    var quantity: String = "0.0"
)