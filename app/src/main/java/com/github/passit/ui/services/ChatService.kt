package com.github.passit.ui.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.github.passit.R
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.model.Message
import com.github.passit.domain.usecase.conversations.SubscribeNewConversations
import com.github.passit.domain.usecase.conversations.SubscribeNewMessages
import com.github.passit.ui.mapper.ConversationEntityToUIMapper
import com.github.passit.ui.screens.chat.ChatActivity
import com.github.passit.ui.screens.main.MainActivity
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatService: LifecycleService() {
    companion object {
        @JvmField val BROADCAST_NEW_CONVERSATION = "${ChatService::class.java.`package`!!.name}.chat.conversation"
        @JvmField val BROADCAST_NEW_MESSAGE = "${ChatService::class.java.`package`!!.name}.chat.message"
        @JvmField val BROADCAST_SERVICE_RESTART = "${ChatService::class.java.`package`!!.name}.chat.restart"

        const val NOTIFICATION_CHANNEL_ID = "PASSIT_CHANNEL"
        const val CONVERSATION_EXTRA_KEY = "Data"
        const val MESSAGE_EXTRA_KEY = "Data"
        const val MESSAGE_CONTENT_SHORT_LENGTH = 20
    }

    inner class ChatServiceBinder: Binder() {
        fun getService(): ChatService = this@ChatService
    }

    private val binder = ChatServiceBinder()
    @Inject lateinit var subscribeNewConversations: SubscribeNewConversations
    @Inject lateinit var subscribeNewMessages: SubscribeNewMessages
    private var isServiceStarted = false
    private var connections: Int = 0
    private var isServiceBound = false
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startid: Int): Int {
        super.onStartCommand(intent, flags, startid)
        startService()
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        stopForeground(true)
        isServiceBound = true
        return binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        stopForeground(true)
        isServiceBound = true
        connections++
    }

    override fun onUnbind(intent: Intent?): Boolean {
        super.onUnbind(intent)
        connections--
        isServiceBound = connections > 0
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
        stopService()
        // broadcast service restart intent
        Intent().apply {
            action = BROADCAST_SERVICE_RESTART
            sendBroadcast(this)
        }
        Log.i("chat-service", "Service destroyed")
    }

    private fun startService() {
        if (isServiceStarted) return
        isServiceStarted = true
        Log.i("chat-service", "Service started")

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ChatService::lock").apply {
                    acquire()
                }
            }

        createNotificationChannel()

        // subscribe to new conversations
        lifecycleScope.launchWhenStarted {
            subscribeNewConversations(SubscribeNewConversations.Params()).catch { error ->
                Log.e("ChatService", "$error")
            }.collect(::onNewConversation)
        }
        // subscribe to new messages
        lifecycleScope.launchWhenStarted {
            subscribeNewMessages(SubscribeNewMessages.Params()).catch { error ->
                Log.e("ChatService", "$error")
            }.collectLatest(::onNewMessage)
        }
    }

    private fun stopService() {
        isServiceStarted = false
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.i("mess-service", "error: $e")
        }
    }

    private fun onNewConversation(conversation: Conversation) {
        Intent().apply {
            action = BROADCAST_NEW_CONVERSATION
            putExtra(CONVERSATION_EXTRA_KEY, Gson().toJson(conversation))
            sendBroadcast(this)
        }
    }

    private fun onNewMessage(message: Message.ReceivedMessage) {
        Intent().apply {
            action = BROADCAST_NEW_MESSAGE
            putExtra(MESSAGE_EXTRA_KEY, Gson().toJson(message))
            sendBroadcast(this)
        }
        if (isServiceBound) return
        val notification = createNotification(message)
        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "passit_notification"
            val descriptionText = "passit service notifications channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.WHITE
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: Message.ReceivedMessage): Notification {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chatActivityIntent = Intent(this, ChatActivity::class.java).apply {
            putExtra(ChatActivity.CHAT_TAG, Gson().toJson(ConversationEntityToUIMapper.map(message.message.conversation!!)))
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivities(this, 0, arrayOf(mainActivityIntent, chatActivityIntent), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(
                        R.string.profile_username,
                        message.message.author?.givenName?.capitalize(Locale.getDefault()),
                        message.message.author?.familyName?.capitalize(Locale.getDefault())
                ))
                .setContentText(message.message.content.subSequence(0, message.message.content.length.coerceAtMost(MESSAGE_CONTENT_SHORT_LENGTH)))
                .setStyle(NotificationCompat.BigTextStyle().bigText(message.message.content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build()

    }
}