package com.github.passit.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.passit.core.extension.bindService
import com.github.passit.ui.services.ChatService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestartServiceBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, ChatService.BROADCAST_SERVICE_RESTART -> {
                context.applicationContext.bindService(ChatService::class.java)
            }
        }
    }
}