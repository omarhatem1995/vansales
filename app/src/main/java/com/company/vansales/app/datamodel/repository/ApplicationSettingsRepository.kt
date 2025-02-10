package com.company.vansales.app.datamodel.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.company.vansales.app.datamodel.models.localmodels.ApplicationConfig
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSettings
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSyncTimeStamp
import com.company.vansales.app.datamodel.room.AppDataBase
import com.company.vansales.app.datamodel.room.ApplicationConfigDAO
import com.company.vansales.app.datamodel.room.ApplicationSettingsDAO
import com.company.vansales.app.datamodel.room.ApplicationSyncTimeStampDAO

class ApplicationSettingsRepository(application: Application) {


    private val settingsDAO: ApplicationSettingsDAO
    private val appConfigDao: ApplicationConfigDAO
    private val appDB: AppDataBase = AppDataBase.getDatabase(application)!!
    private val appSyncTimeStamp: ApplicationSyncTimeStampDAO
    val getApplicationSettingsLiveData: LiveData<ApplicationSettings>
    val getApplicationSettings: ApplicationSettings

    init {
        settingsDAO = appDB.getApplicationSettings()
        appConfigDao = appDB.getAppConfig()
        appSyncTimeStamp = appDB.getApplicationSyncTimeStamp()
        getApplicationSettingsLiveData = settingsDAO.getApplicationSettingsLiveData()
        getApplicationSettings = settingsDAO.getApplicationSettings()
    }

    fun deleteAllApplicationSyncData(){
        return appSyncTimeStamp.deleteAllAppSyncData()
    }

    fun insertAppTypeTimeStamp(applicationSyncTimeStamp: ApplicationSyncTimeStamp) {
        appSyncTimeStamp.upsert(applicationSyncTimeStamp)
    }

    fun getAppTimeStampByType(type : String) : ApplicationSyncTimeStamp {
        return appSyncTimeStamp.getAppConfigValueByType(type)
    }

    fun insert(applicationSettings: ApplicationSettings) {
        settingsDAO.insert(applicationSettings)
    }

    fun updateAppLanguage(selectedLanguage: String) {
        settingsDAO.updateAppLanguage(selectedLanguage)
    }

    fun updateDayStatus(isDayStarted: Boolean) {
        settingsDAO.updateDayStatus(isDayStarted)
    }

    fun getCurrentLanguage(): String? {
        return settingsDAO.getCurrentLanguage()
    }

    fun updateSettings(port: String, host: String, driver: String?,printerMACAddress:String?) {
        settingsDAO.updateSettings(port.toInt(), host, driver,printerMACAddress)
    }    fun updateSettings(printerMACAddress:String?) {
        settingsDAO.updatePrinterSettings(printerMACAddress)
    }

    fun getUrlComponents(): ApplicationSettings? {
        return settingsDAO.getUrlComponents()
    }

    fun getAppConfigValueByAppParamter(appParamter : String): String {
        return appConfigDao.getAppConfigValueByAppParamter(appParamter)
    }

    fun getDriverNumber(): String {
        return settingsDAO.getDriverNumber()
    }

    fun upsertApplicationSettings(appConfig: List<ApplicationConfig>) {
        appConfigDao.upsert(appConfig)
    }

}
