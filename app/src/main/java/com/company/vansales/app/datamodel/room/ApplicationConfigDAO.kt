package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.ApplicationConfig

@Dao
abstract class ApplicationConfigDAO : BaseDao<ApplicationConfig>() {

    @Query("SELECT value FROM application_config WHERE appParamter = :appParamter")
    abstract fun getAppConfigValueByAppParamter(appParamter: String): String

}
