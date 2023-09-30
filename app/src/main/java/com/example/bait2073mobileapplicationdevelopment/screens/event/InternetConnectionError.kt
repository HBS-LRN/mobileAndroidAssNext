package com.example.bait2073mobileapplicationdevelopment.screens.event

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.Button
import com.example.bait2073mobileapplicationdevelopment.R

class InternetConnectionError(private val context: Context) {
    private lateinit var dialog: Dialog

    public fun showLostInternetDialog() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.custom_dialog_no_internet)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false) // Optional
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        val okay: Button = dialog.findViewById(R.id.btn_okay)
        val cancel: Button = dialog.findViewById(R.id.btn_cancel)

        okay.setOnClickListener {
            dialog.dismiss()
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        dialog.show()
    }
}
