package com.github.passit.ui.view

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class Alert : MaterialAlertDialogBuilder {
    constructor(context: Context): super(context)
    constructor(context: Context, overrideStyleResId: Int): super(context, overrideStyleResId)

    init {
        this.setPositiveButton("OK", null)
    }
}