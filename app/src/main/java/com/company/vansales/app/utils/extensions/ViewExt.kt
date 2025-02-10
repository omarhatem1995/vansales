package com.company.vansales.app.utils.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.company.vansales.app.SAPWizardApplication
import com.google.android.material.snackbar.Snackbar


fun Context.displayToast(text: String){
    Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
}
fun View.displaySnackbar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()
}
fun Context.displayToastLong(text: String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}


fun Context.displayToast(res: Int){
    Toast.makeText(this, this.getString(res), Toast.LENGTH_SHORT).show()
}


fun Context.displayToastLong(res: Int){
    Toast.makeText(this, this.getString(res), Toast.LENGTH_SHORT).show()
}

fun <T> List<T>.toArrayList(): ArrayList<T>{
    return ArrayList(this)
}

fun View.hide(){
    this.visibility = GONE
}


fun View.show(){
    this.visibility = VISIBLE
}

fun Context.stringText(res: Int): String{
    return this.getString(res)
}


fun Activity.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.asBitmap(textSize: Float, textColor: Int): Bitmap? {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = textSize
    paint.color = textColor
    paint.textAlign = Paint.Align.LEFT
    val baseline: Float = -paint.ascent() // ascent() is negative
    val width = (paint.measureText(this) + 0.5f).toInt() // round
    val height = (baseline + paint.descent() + 0.5f).toInt()
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(this, 0f, baseline, paint)
    return image
}

fun String.toHtml(): Spanned? {
    return Html.fromHtml(this)
}

fun String.chunked(size: Int): List<String> {
    val nChunks = length / size
    return (0 until nChunks).map { substring(it * size, (it + 1) * size) }
}

fun Context.isLocationEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // This is a new method provided in API 28
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.isLocationEnabled
    } else {
        // This was deprecated in API 28
        val mode: Int = Settings.Secure.getInt(
            this.contentResolver, Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        )
        mode != Settings.Secure.LOCATION_MODE_OFF
    }
}


fun String.addZerosToDriver(): String {
    var driverNumber = this
    if (driverNumber.length < 10) {
        try {
            val driverInt = driverNumber.toInt()
            val formatted = driverInt.toString().padStart(10, '0')
            driverNumber = formatted
        } catch (ex: Exception) {
        }
    } else {
        return driverNumber
    }
    return driverNumber
}