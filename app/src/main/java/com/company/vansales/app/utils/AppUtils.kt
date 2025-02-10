package com.company.vansales.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.company.vansales.R
import com.company.vansales.app.activity.MaterialSelectionActivity
import com.company.vansales.app.datamodel.repository.ApplicationSettingsRepository
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URLDecoder
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

object AppUtils {
    fun showMessage(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun urlBuilder(application: Application): String {
        val applicationSettingsRepository = ApplicationSettingsRepository(application)
        val applicationSettings = applicationSettingsRepository.getUrlComponents()
        return if (applicationSettings == null)
            "http://${Constants.BASE_URL}:${Constants.BASE_PORT}/"
        else {
            applicationSettings.getFullLink()
        }
    }

    fun checkCategory(itemCategory: String): String {
        return if (itemCategory == Constants.MATERIAL_CATEGORY_RETURN_SELLABLE ||
            itemCategory == Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_SELLABLE ||
            itemCategory == Constants.MATERIAL_CATEGORY_EXCHANGE_SELLABLE ||
            itemCategory == Constants.MATERIAL_CATEGORY_FREE
        ) {
            Constants.MATERIAL_SELLABLE
        } else if (itemCategory == Constants.MATERIAL_CATEGORY_RETURN_DAMAGE ||
            itemCategory == Constants.MATERIAL_CATEGORY_EXCHANGE_RETURN_DAMAGE
        ) {
            Constants.MATERIAL_DAMAGED
        } else {
            itemCategory
        }
    }

    fun calculateExInvoiceNumber(invoiceNumber: Int): String {
//        val invoiceNumber2 = addDigitToEnd(invoiceNumber,7)
        val calculated = invoiceNumber + 1

        var result = calculated.toString()
        Log.d("calculateEx", "$invoiceNumber")
        repeat(6 - calculated.toString().length) {
            result = "0$result"
        }
        Log.d("calculateEx", "$result")

        return result
    }

    fun calculateExInvoiceNumberFree(invoiceNumber: Int): String {
//        val invoiceNumber2 = addDigitToEnd(invoiceNumber,8)
        val calculated = invoiceNumber + 2
        var result = calculated.toString()
        Log.d("calculateEx", "$invoiceNumber")
        repeat(6 - calculated.toString().length) {
            result = "0$result"
        }

        return result
    }

    fun calculateDistanceByKM(
        customerLat: Double,
        customerLong: Double,
        driverLat: Double,
        driverLong: Double
    ): Int {
        val customerLocation = Location("")
        customerLocation.latitude = customerLat
        customerLocation.longitude = customerLong

        val driverLocation = Location("")
        driverLocation.latitude = driverLat
        driverLocation.longitude = driverLong
        return (customerLocation.distanceTo(driverLocation) / 1000).roundToInt()
    }

    fun getLanguageDependencyString(
        stringEnglish: String?,
        stringArabic: String?,
        application: Application
    ): String {
        val applicationSettingsRepository = ApplicationSettingsRepository(application)
        return if (!stringEnglish.isNullOrBlank() && !stringArabic.isNullOrBlank()) {
            if (applicationSettingsRepository.getCurrentLanguage() == Constants.ARABIC_LANGUAGE) {
                decodeArabicString(stringArabic)
            } else {
                stringEnglish
            }
        } else {
            if (!stringEnglish.isNullOrBlank() && stringArabic.isNullOrBlank()) {
                stringEnglish
            } else if (stringEnglish.isNullOrBlank() && !stringArabic.isNullOrBlank()) {
                decodeArabicString(stringArabic)
            } else {
                ""
            }
        }
    }

    fun expiryDateChecker(expiryDate: String): String {
        Log.d("getExpiryDate", "$expiryDate")
        return if (expiryDate.isEmpty()) {
            formatDateAndTime("31-12-2030T00:00:00.00") ?: ""
        } else {
            formatDateAndTime(expiryDate) ?: ""
        }
    }
    fun getDriver(application: Application): String {
        val applicationSettingsRepository = ApplicationSettingsRepository(application)
        return applicationSettingsRepository.getDriverNumber()
    }

    fun decodeArabicString(arabicString: String): String {
        val newArabic = arabicString.replace("+", " ").replace("%", " ")
        return URLDecoder.decode(newArabic, "UTF-8")
    }
    fun getStringFormattedCurrentDateTime(): String {
        val date = getCurrentDateTime()
        return date.toString("yyyy/MM/dd HH:mm:ss")
    }
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    fun decimalNumberRegexChecker(amount: String): Boolean {
        val decimalFormatRegex = """^[0-9]\d*(\.\d+)?${'$'}""".toRegex()
        return decimalFormatRegex.matches(amount)
    }

    fun getStringCurrentDateDifferentFormat(): String {
        val date = getCurrentDateTime()
        return date.toString("yyyyMMdd")
    }

    fun getStringCurrentTimeDifferentFormat(): String {
        val date = getCurrentDateTime()
        return date.toString("HHmmss")
    }

    fun getFormatForEndDay(inputDate: String): String {
        val inputFormat = SimpleDateFormat("", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        // Parse input date string into a Date object
        val date: Date = inputFormat.parse(inputDate) ?: Date()

        // Format the Date object into the desired output format
        return outputFormat.format(date)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
    fun getStringCurrentTime(): String {
        val date = getCurrentDateTime()
        return date.toString("HH:mm")
    }
    fun getFormatForEndDay(): String {
        val date = getCurrentDateTime()
        val format = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US
        )
        return format.format(date)
    }

    fun getFormatForCurrentDay(): String {
        val date = getCurrentDateTime()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return format.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateAndTime(time: String): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val output = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val d = try {
            sdf.parse(time)
        } catch (e: InvocationTargetException) {
            return "9999-12-12T00:00:00.000+0000"
        } catch (ex: ParseException) {
            return "9999-12-12T00:00:00.000+0000"
        }
        return output.format(d)

    }

    fun getNextDueDate(myDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = myDate
        calendar.add(Calendar.DAY_OF_YEAR, +7)
        val newDate = calendar.time
        return newDate.toString("dd/MM/yyyy")
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(time: String): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val output = SimpleDateFormat("yyyy-MM-dd")
            val d = sdf.parse(time)
            output.format(d)

        } catch (e: Throwable) {
            ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(time: String, checking: Boolean): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val output = SimpleDateFormat("dd-MM-yyyy")
            val d = sdf.parse(time)
            output.format(d)

        } catch (e: Throwable) {
            ""
        }

    }

    fun formatTime(time: Date): String? {
        return try {
            val output = SimpleDateFormat("HH:mm")
            output.format(time)
        } catch (e: Throwable) {
            ""
        }
    }


    fun formatDateTime(time: Date): String? {
        return try {
            val output = SimpleDateFormat("yyyy-MM-dd HH:mm")
            output.format(time)
        } catch (e: Throwable) {
            null
        }
    }

    fun getCurrentTime(): Date {
        return Date() // Returns the current time as a Date object
    }

    // Example usage

    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: String): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val output = SimpleDateFormat("HH:mm")
            val d = sdf.parse(time)
            output.format(d)

        } catch (e: Throwable) {
            ""
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateTime(time: String): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val output = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val d = sdf.parse(time)
            output.format(d)

        } catch (e: Throwable) {
            ""
        }

    }

    fun getStringCurrentDate(): String {
        val date = getCurrentDateTime()
        return date.toString("dd/MM/yyyy")
    }

    fun round(number: Double): Double {
        val df = DecimalFormat("#.###", DecimalFormatSymbols(Locale.ENGLISH))
        return df.format(number).toDouble()
    }

    fun round(number: BigDecimal): BigDecimal {
//        val factor = 10.0.pow(Constants.DecimalPlaces)
//        return ((number.toDouble() * factor).roundToLong() / factor).toBigDecimal()
        return number.setScale(3, RoundingMode.HALF_UP)
    }

    fun round(number: BigDecimal, scaleValue: Int): BigDecimal {
//        val factor = 10.0.pow(Constants.DecimalPlaces)
//        return ((number.toDouble() * factor).roundToLong() / factor).toBigDecimal()
        return number.setScale(scaleValue, RoundingMode.HALF_UP)
    }


    fun convertToArabic(value: String, language: String): String? {
        return if (language == "ar") {
            (value + "")
                .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
                .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
                .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
                .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
                .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
                .replace(":".toRegex(), ":").replace(".", ",")
        } else
            value
    }

    fun String.convertToEnglish(value: String): String? {
        return (value + "")
            .replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
            .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
            .replace(":".toRegex(), ":").replace("٫".toRegex(), ".")
    }

    fun convertToEnglish(value: String): String? {
        return (value + "")
            .replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3").replace("٤".toRegex(), "4")
            .replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8")
            .replace("٩".toRegex(), "9").replace("٠".toRegex(), "0")
            .replace(":".toRegex(), ":").replace("٫".toRegex(), ".")
    }

    fun convertToArabic(value: Char, language: String): Char {
        return when (value) {
            '1' -> '١'
            '2' -> '٢'
            '3' -> '٣'
            '4' -> '٤'
            '5' -> '٥'
            '6' -> '٦'
            '7' -> '٧'
            '8' -> '٨'
            '9' -> '٩'
            '0' -> '٠'
            '*' -> '*'
            '-' -> '-'
            else -> ' '
        }
    }

    var erpNumber = ""
    fun dialogWithOkButton(
        message: String,
        activity: Activity,
        context: Context,
        finishActivity: Boolean,
        toActivity: Activity,
        isBatchScanActivity: Boolean = false,
        isOrderMaterial: Boolean = false,
        erpNumber: String? = "",
        isApproval: Boolean? = false
    ) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
                if (erpNumber != null) {
                    this.erpNumber = erpNumber
                }
                if (isBatchScanActivity) {
                    if (isApproval != null && isApproval)
                        proceedToActivity(context, activity, toActivity)
           /*         else
                        BatchScanActivity.instance?.showPrinterDialog()*/
                    dialog.dismiss()

                } else if (isOrderMaterial) {
                    dialog.dismiss()
                    Log.d("erpNumberIsDial", " $erpNumber")
                } else {
                    if (finishActivity) {
                        dialog.dismiss()
                        proceedToActivity(context, activity, toActivity)
                    } else {
                        dialog.dismiss()
                    }
                }
            }
        val alert = builder.create()
        alert.show()
    }
    fun proceedToActivity(context: Context, activity: Activity, toActivity: Activity?) {
        if (toActivity != null) {
            val i = Intent(context, toActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(i)
            activity.finish()
        }
    }
}