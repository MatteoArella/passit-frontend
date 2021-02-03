package com.github.passit.ui.contracts.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.github.passit.ui.screens.auth.ConfirmCodeActivity

class ConfirmCodeContract : ActivityResultContract<String, Boolean>() {
    override fun createIntent(context: Context, email: String): Intent {
        val intentInput = Bundle().apply { putString("email", email) }
        return Intent(context, ConfirmCodeActivity::class.java).putExtras(intentInput)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean = when(resultCode) {
        Activity.RESULT_OK -> true
        else -> false
    }
}