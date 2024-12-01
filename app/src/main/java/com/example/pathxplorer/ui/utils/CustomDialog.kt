package com.example.pathxplorer.ui.utils

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.pathxplorer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

class CustomDialog {
    fun showDialog(context: Context, title: String = "Confirmation", message: String = "Are you sure?", positiveButtonText: String = "Yes", negativeButtonText: String = "No", callback: (Boolean) -> Unit) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = View.inflate(context, R.layout.custom_dialog_layout, null)
        builder.setView(inflater)

        val dialog = builder.create()

        inflater.findViewById<TextView>(R.id.title_dialog).text = title
        inflater.findViewById<TextView>(R.id.message_dialog).text = message
        inflater.findViewById<Button>(R.id.btn_positive).text = positiveButtonText
        inflater.findViewById<Button>(R.id.btn_negative).text = negativeButtonText

        inflater.findViewById<Button>(R.id.btn_positive).setOnClickListener {
            callback(true)
            dialog.dismiss()
        }

        inflater.findViewById<Button>(R.id.btn_negative).setOnClickListener {
            callback(false)
            dialog.dismiss()
        }

        dialog.show()
    }
}