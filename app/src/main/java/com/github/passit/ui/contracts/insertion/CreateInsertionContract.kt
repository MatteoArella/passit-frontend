package com.github.passit.ui.contracts.insertion

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.github.passit.domain.model.Insertion
import com.github.passit.ui.screens.insertions.CreateInsertionActivity
import com.google.gson.Gson

class CreateInsertionContract : ActivityResultContract<Nothing?, Insertion?>() {
    override fun createIntent(context: Context, input: Nothing?): Intent {
        return Intent(context, CreateInsertionActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Insertion? = when(resultCode) {
        Activity.RESULT_OK -> {
            intent?.extras?.let {
                Gson().fromJson(it.getString(CreateInsertionActivity.INSERTION_TAG), Insertion::class.java)
            }
        }
        else -> null
    }
}