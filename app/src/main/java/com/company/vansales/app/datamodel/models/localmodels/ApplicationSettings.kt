package com.company.vansales.app.datamodel.models.localmodels

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.company.vansales.app.utils.Constants

@Entity(tableName = "application_settings")
data class ApplicationSettings(
    @PrimaryKey
    var id: Int,
    var language: String?,
    var host: String?,
    var port: Int?,
    var driver: String?,
    var isDayStarted: Boolean?,
    var printerMACAddress: String?
){

    fun getFullLink(): String{
        if (host.isNullOrEmpty())
            host = Constants.BASE_URL
        if (port == null)
            port = Constants.BASE_PORT.toInt()
        return "http://$host:$port/"
    }

}