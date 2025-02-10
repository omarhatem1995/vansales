package com.company.vansales.app.datamodel.sharedpref

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class GetSharedPreferences(application: Application) {

    companion object{
        val PRINTER_ADDRESS = "PRINT_FILE_PATH"
        val DAY_STARTED = "DAY_STARTED"
        val LOGGED_IN = "LOGGED_IN"
        val DRIVER_NAME = "DRIVER_NAME"
        val DRIVER_USER_NAME = "DRIVER_USER_NAME"
        val DRIVER_PHONE_NUMBER = "DRIVER_PHONE_NUMBER"
        val DRIVER_NUMBER = "DRIVER_NUMBER"
        val SELECTED_ROUTE = "SELECTED_ROUTE"
        val DIST_CHANNEL = "DIST_CHANNEL"
        val SALES_ORG = "SALES_ORG"
        val DIVISION = "DIVISION"
        val SYNC_DATE = "SYNC_DATE"
        val SYNC_TIME = "SYNC_TIME"
        val SYNC_TIME_STAMP = "SYNC_TIME_STAMP"
        val PRICE_PAGE_NO = "PRICE_PAGE_NO"
        val CONDITION_PAGE_NO = "CONDITION_PAGE_NO"
        val LANG = "LANG"
    }

    var preference: SharedPreferences =
        application.getSharedPreferences("Van-Sales", Context.MODE_PRIVATE)

    fun setLanguage(language: String) {
        preference.edit()
            .putString(LANG, language).apply()
    }

    fun getLanguage() = preference.getString(LANG, "en")

    fun setPrinterAddress(printPath: String) {
        preference.edit()
            .putString(PRINTER_ADDRESS, printPath).apply()
    }

    fun getPrinterAddress() = preference.getString(PRINTER_ADDRESS, "")

    fun setDayStarted(isDayStarted: Boolean) {
        preference.edit()
            .putBoolean(DAY_STARTED, isDayStarted).apply()
    }

    fun getDayStarted() = preference.getBoolean(DAY_STARTED, false)

   fun setUserLoggedIn(isLoggedIn: Boolean) {
        preference.edit()
            .putBoolean(LOGGED_IN, isLoggedIn).apply()
    }

    fun getUserLoggedIn() = preference.getBoolean(LOGGED_IN, false)

    fun setDriverNumber(driver: String) {
        preference.edit()
            .putString(DRIVER_NUMBER, driver).apply()
    }

    fun getDriverNumber() = preference.getString(DRIVER_NUMBER,null)

    fun setDriverName(name: String) {
        preference.edit()
            .putString(DRIVER_NAME, name).apply()
    }

    fun getDriverName() = preference.getString(DRIVER_NAME,null)
    fun setDriverUserName(name: String) {
        preference.edit()
            .putString(DRIVER_USER_NAME, name).apply()
    }

    fun getDriverUserName() = preference.getString(DRIVER_USER_NAME,null)
    fun setDriverPhoneNumber(name: String) {
        preference.edit()
            .putString(DRIVER_PHONE_NUMBER, name).apply()
    }

    fun getDriverPhoneNumber() = preference.getString(DRIVER_PHONE_NUMBER,null)

    fun setSelectedRoute(route: String) {
        preference.edit()
            .putString(SELECTED_ROUTE, route).apply()
    }

    fun getSelectedRoute() = preference.getString(SELECTED_ROUTE,null)

    fun setDistChannel(distChannel: String) {
        preference.edit()
            .putString(DIST_CHANNEL, distChannel).apply()
    }
    fun getDistChannel() = preference.getString(DIST_CHANNEL,null)

    fun setSalesOrg(salesOrg: String) {
        preference.edit()
            .putString(SALES_ORG, salesOrg).apply()
    }
    fun getSalesOrg() = preference.getString(SALES_ORG,null)

    fun setDivision(division: String) {
        preference.edit()
            .putString(DIVISION, division).apply()
    }
    fun getDivision() = preference.getString(DIVISION,null)

    fun setCurrentSyncDate(date: String) {
        preference.edit()
            .putString(SYNC_DATE, date).apply()
    }
    fun getCurrentSyncDate() = preference.getString(SYNC_DATE,null)

    fun setCurrentSyncTime(time: String) {
        preference.edit()
            .putString(SYNC_TIME, time).apply()
    }
    fun getCurrentSyncTime() = preference.getString(SYNC_TIME,null)

    fun setCurrentTimeStamp(timeStamp: String) {
        preference.edit()
            .putString(SYNC_TIME_STAMP, timeStamp).apply()
    }
    fun getCurrentTimeStamp() = preference.getString(SYNC_TIME_STAMP,null)

    fun setCurrentPricesPageNo(pageNo: Int) {
        preference.edit()
            .putInt(PRICE_PAGE_NO, pageNo).apply()
    }
    fun getCurrentPricesPageNo() = preference.getInt(PRICE_PAGE_NO,1)

    fun setCurrentPriceConditionPageNo(pageNo: Int) {
        preference.edit()
            .putInt(CONDITION_PAGE_NO, pageNo).apply()
    }
    fun getCurrentPriceConditionPageNo() = preference.getInt(CONDITION_PAGE_NO,1)

    fun deleteAllSharedPref(){
        preference.edit().clear()
    }
}