package it.uniroma1.macc.project.ui.view

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class Alert(context: Context) : MaterialAlertDialogBuilder(context) {
    init {
        this.setPositiveButton("OK", null)
    }
}