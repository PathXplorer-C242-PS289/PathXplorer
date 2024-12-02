package com.example.pathxplorer.ui.utils

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.pathxplorer.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs

class CustomDialog {
    fun showDialog(context: Context, title: String = "Confirmation", message: String = "Are you sure?", positiveButtonText: String = "Yes", negativeButtonText: String = "No", isCancleable: Boolean = true, callback: (Boolean) -> Unit) {
        val builder = MaterialAlertDialogBuilder(context)
        val inflater = View.inflate(context, R.layout.custom_dialog_layout, null)
        builder.setView(inflater)

        val dialog = builder.create()

        val titleComponent = inflater.findViewById<TextView>(R.id.title_dialog)
        val messageComponent = inflater.findViewById<TextView>(R.id.message_dialog)
        val positiveButton = inflater.findViewById<Button>(R.id.btn_positive)
        val negativeButton = inflater.findViewById<Button>(R.id.btn_negative)

        titleComponent.text = title
        messageComponent.text = message

        if (!isCancleable) {
            dialog.setCancelable(false)
            negativeButton.visibility = View.GONE
        }

        positiveButton.text = positiveButtonText
        negativeButton.text = negativeButtonText

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