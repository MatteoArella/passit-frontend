package com.github.passit.ui.contracts.insertion

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.github.passit.ui.screens.insertions.ShowInsertionActivity

class ShowInsertionContract : ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, insertionId: String): Intent {
        return Intent(context, ShowInsertionActivity::class.java).putExtra(ShowInsertionActivity.INSERTION_TAG, insertionId)
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {

    }
}