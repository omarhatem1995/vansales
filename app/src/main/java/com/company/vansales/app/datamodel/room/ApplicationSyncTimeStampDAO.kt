package com.company.vansales.app.datamodel.room

import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSyncTimeStamp
import com.company.vansales.app.datamodel.room.BaseDao

@Dao
abstract class ApplicationSyncTimeStampDAO : BaseDao<ApplicationSyncTimeStamp>() {

    @Query("SELECT * FROM application_sync WHERE type = :type")
    abstract fun getAppConfigValueByType(type: String): ApplicationSyncTimeStamp

    @Query("DELETE FROM application_sync")
    abstract fun deleteAllAppSyncData()
}
