package com.company.vansales.app.view.fragments

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.company.vansales.R

class BlockedUserDialog(private val context: Context, val isCancelable : Boolean ) {
    fun show(){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.blocked_dialog)
        dialog.findViewById<Button>(R.id.dialog_blocked_button).setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(isCancelable)
        dialog.show()
    }
}