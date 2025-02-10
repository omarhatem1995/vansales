package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity

@Entity(tableName = "application_sync" ,primaryKeys = ["id", "type"])
data class ApplicationSyncTimeStamp(
    var id: Int,
    var type: String,
    var timeStamp: String,
)