package com.github.passit.ui.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class Alert(context: Context) : MaterialAlertDialogBuilder(context) {
    init {
        this.setPositiveButton("OK", null)
    }
}