package com.company.vansales.app.utils

import android.app.Activity
import android.app.Dialog
import android.graphics.Point
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.text.HtmlCompat
import com.company.vansales.R
import com.company.vansales.app.datamodel.models.mastermodels.Visits
import com.company.vansales.app.datamodel.repository.HelpViewRepository
import com.company.vansales.app.datamodel.repository.VisitsRepository

object ViewUtils {


    fun highlightSelectedValue(title: String, value: String, textview: TextView) {
        val sentence = "$title <font color='#0A6ED1'><br>$value</font>"
        textview.text = HtmlCompat.fromHtml(sentence, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }


    fun boldValue(title: String, value: String, textview: TextView) {
        val sentence = "$title <font color='#FAFAFA'><br><b>$value</b></font>"
        textview.text = HtmlCompat.fromHtml(sentence, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }


    fun highlight(title: String, value: String, textview: TextView) {
        val sentence = "$title <font color='#0A6ED1'>$value</font>"
        textview.text = HtmlCompat.fromHtml(sentence, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }


/*    fun showVisitFailureReasonDialog(
        finishActivity: Boolean,
        visitsRepository: VisitsRepository,
        visit: Visits,
        activity: Activity,
        latitude: Double,
        longitude: Double
    ) {
        val helpViewRepository = HelpViewRepository()
        val dialog = initDialog(activity)
        dialog.setCancelable(false)
        val reasonsHashMap = helpViewRepository.getFailedVisitReasons()
        val reasons = arrayListOf<String>()
        for (key in reasonsHashMap.keys) {
            reasons.add(key)
        }

        val spinnerAdapter = ArrayAdapter(
            activity,
            android.R.layout.simple_list_item_1,
            reasons
        )
        val spinnerFailure = dialog.findViewById<Spinner>(R.id.failureReasons_SP)
        val visitComment_ET = dialog.findViewById<EditText>(R.id.visitComment_ET)
        val dialogTitle_TV = dialog.findViewById<TextView>(R.id.dialogTitle_TV)
        val visitFailureDone_BTN = dialog.findViewById<Button>(R.id.visitFailureDone_BTN)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        spinnerFailure.adapter = spinnerAdapter
        spinnerFailure.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val text = spinnerFailure.selectedItem.toString()
                    if (text == activity.resources.getString(R.string.other)) {
                        visitComment_ET.visibility = View.VISIBLE
                    } else {
                        visitComment_ET.visibility = View.GONE
                    }
                }
            }
        dialogTitle_TV.setOnClickListener {
            dialog.dismiss()

        }
        visitFailureDone_BTN.setOnClickListener {
            if (spinnerFailure.selectedItem.toString() == activity.resources.getString(R.string.other)) {
                val otherReason = visitComment_ET.text.trim()
                visitsRepository.finishFailedVisit(
                    visit.visitListID,
                    visit.visitItemNo,
                    otherReason.toString(),
                    reasonsHashMap[spinnerFailure.selectedItem.toString()]!!
                )
            } else {
                visitsRepository.updateEndVisitData(visit.visitListID,visit.visitItemNo,
                    AppUtils.getFormatForEndDay(),AppUtils.getFormatForEndDay(),latitude,longitude,failedReason = spinnerFailure.selectedItem.toString())
                visitsRepository.finishFailedVisit(
                    visit.visitListID,
                    visit.visitItemNo,
                    spinnerFailure.selectedItem.toString(),
                    reasonsHashMap[spinnerFailure.selectedItem.toString()]!!
                )

            }
            if (finishActivity) {
                dialog.dismiss()
                activity.finish()
            } else {
                dialog.dismiss()
            }
        }
    }*/

   /* private fun initDialog(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_failed_visit_layout)
        val size = Point()
        val w = activity.windowManager
        w.defaultDisplay.getSize(size)
        val width = WindowManager.LayoutParams.MATCH_PARENT
        val height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)
        dialog.show()
        return dialog
    }
*/

    fun disableView(view : View){
        view.alpha = 0.5F
        view.isClickable = false
        view.isEnabled = false
    }

    fun enableView(view : View){
        view.alpha = 1F
        view.isClickable = true
        view.isEnabled = true

    }

    /*fun showProgressDialog(progressDialog: Dialog) {
        progressDialog.setContentView(R.layout.dialog_progress_bar_layout)
//        progressDialog.window?.setLayout(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )
//        progressDialog.window?.setGravity(Gravity.CENTER)
//        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }*/



}