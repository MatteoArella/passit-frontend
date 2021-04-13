package com.github.passit.ui.screens.chat

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.passit.R
import com.github.passit.core.extension.bindService
import com.github.passit.databinding.ActivityChatBinding
import com.github.passit.domain.model.Message
import com.github.passit.ui.models.conversations.*
import com.github.passit.ui.receivers.ChatBroadcastReceiver
import com.github.passit.ui.services.ChatService
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

@AndroidEntryPoint
class ChatActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val CHAT_TAG = "chat"
    }
    private lateinit var binding: ActivityChatBinding
    private lateinit var conversation: ConversationView

    private lateinit var messagesAdapter: MessagesAdapter
    private val getMessagesViewModel: GetMessagesViewModel by viewModels()
    private val sendMessageViewModel: SendMessageViewModel by viewModels()
    private val unreadMessagesViewModel: UnreadMessagesViewModel by viewModels()

    private lateinit var intentFilter: IntentFilter
    private val receiver = ChatBroadcastReceiver()
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindService(ChatService::class.java, serviceConnection)

        conversation = Gson().fromJson(intent.extras?.getString(CHAT_TAG)!!, ConversationView::class.java)

        binding.tutorName.text = getString(
            R.string.profile_username,
            conversation.associated?.givenName?.capitalize(Locale.getDefault()),
            conversation.associated?.familyName?.capitalize(Locale.getDefault())
        )
        Picasso.get().load(conversation.associated?.picture?.toURI().toString()).placeholder(R.drawable.ic_person).into(binding.tutorPicture)

        binding.chatToolbar.setNavigationOnClickListener {
            finish()
        }

        messagesAdapter = MessagesAdapter()
        binding.chatRecyclerView.adapter = messagesAdapter
        (binding.chatRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
        (binding.chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        lifecycleScope.launch {
            unreadMessagesViewModel.resetUnreadMessagesCount(conversation.id!!).collect()
        }

        lifecycleScope.launchWhenResumed {
            getMessagesViewModel.getMessages(conversationId = conversation.id ?: "").catch { error ->
                ErrorAlert(this@ChatActivity).setTitle("Chat error").setMessage(error.localizedMessage).show()
            }.collectLatest {
                messagesAdapter.submitData(it)
            }
        }

        binding.sendMessageBtn.setOnClickListener {
            if (binding.messageContentText.text.isNullOrBlank()) return@setOnClickListener
            lifecycleScope.launchWhenResumed {
                conversation.associated?.id?.let { to ->
                    sendMessageViewModel.createMessage(to, conversation.id!!, binding.messageContentText.text.toString()).catch { error ->
                        ErrorAlert(this@ChatActivity).setTitle("Chat error").setMessage(error.localizedMessage).show()
                    }.collect {
                        binding.messageContentText.setText("", TextView.BufferType.EDITABLE)
                    }
                }
            }
        }

        receiver.setOnNewMessageListener(::onNewMessage)
        intentFilter = IntentFilter().apply {
            addAction(ChatService.BROADCAST_NEW_MESSAGE)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        bindService(ChatService::class.java, serviceConnection)
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        unbindService(serviceConnection)
    }

    private fun onNewMessage(message: Message) {
        lifecycleScope.launch {
            unreadMessagesViewModel.resetUnreadMessagesCount(conversation.id!!).collect()
        }
    }
}