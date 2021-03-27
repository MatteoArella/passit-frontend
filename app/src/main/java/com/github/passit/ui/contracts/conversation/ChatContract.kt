package com.github.passit.ui.contracts.conversation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.github.passit.ui.models.conversations.ConversationView
import com.github.passit.ui.screens.chat.ChatActivity
import com.google.gson.Gson

class ChatContract : ActivityResultContract<ConversationView, Unit>() {
    override fun createIntent(context: Context, conversation: ConversationView): Intent {
        return Intent(context, ChatActivity::class.java).putExtra(ChatActivity.CHAT_TAG, Gson().toJson(conversation))
    }

    override fun parseResult(resultCode: Int, intent: Intent?) {}
}