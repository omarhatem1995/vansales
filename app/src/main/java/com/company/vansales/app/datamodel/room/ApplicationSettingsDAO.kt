package com.company.vansales.app.datamodel.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.company.vansales.app.datamodel.models.localmodels.ApplicationSettings

@Dao
abstract class ApplicationSettingsDAO : BaseDao<ApplicationSettings>() {

    @Query("SELECT * FROM application_settings")
    abstract fun getApplicationSettingsLiveData(): LiveData<ApplicationSettings>

    @Query("SELECT * FROM application_settings")
    abstract fun getApplicationSettings(): ApplicationSettings

    @Query("SELECT * FROM application_settings")
    abstract fun getUrlComponents(): ApplicationSettings?

    @Query("UPDATE application_settings SET language = :selectedLanguage WHERE id = 1")
    abstract fun updateAppLanguage(selectedLanguage: String)

    @Query("UPDATE application_settings SET isDayStarted = :isDayStarted WHERE id = 1")
    abstract fun updateDayStatus(isDayStarted : Boolean)

    @Query("SELECT language FROM application_settings")
    abstract fun getCurrentLanguage(): String?

    @Query("UPDATE application_settings SET host = :host, port = :port , driver = :driver , " +
            "printerMACAddress =:printerMACAddress WHERE id = 1 ")
    abstract fun updateSettings(port : Int, host : String , driver : String? ,printerMACAddress: String?)

    @Query("UPDATE application_settings SET " +
            "printerMACAddress =:printerMACAddress WHERE id = 1 ")
    abstract fun updatePrinterSettings(printerMACAddress: String?)

    @Query("SELECT driver FROM application_settings WHERE id = 1")
    abstract fun getDriverNumber(): String

}
