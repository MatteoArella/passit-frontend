package com.github.passit.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.model.Message
import com.github.passit.ui.services.ChatService
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatBroadcastReceiver: BroadcastReceiver() {
    private var onNewConversation: ((Conversation) -> Unit)? = null
    private var onNewMessage: ((Message) -> Unit)? = null

    fun setOnNewConversationListener(listener: (Conversation) -> Unit) {
        this.onNewConversation = listener
    }

    fun setOnNewMessageListener(listener: (Message) -> Unit) {
        this.onNewMessage = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ChatService.BROADCAST_NEW_CONVERSATION -> {
                // received new conversation signal
                val conversation = Gson().fromJson(intent.extras?.getString(ChatService.CONVERSATION_EXTRA_KEY), Conversation::class.java)
                onNewConversation?.let { it(conversation) }
            }
            ChatService.BROADCAST_NEW_MESSAGE -> {
                // receive new message signal
                val message = Gson().fromJson(intent.extras?.getString(ChatService.MESSAGE_EXTRA_KEY), Message.ReceivedMessage::class.java)
                onNewMessage?.let { it(message) }
            }
        }
    }
}