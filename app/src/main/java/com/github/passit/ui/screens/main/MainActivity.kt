package com.github.passit.ui.screens.main

import android.content.ComponentName
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.github.passit.R
import com.github.passit.core.extension.bindService
import com.github.passit.databinding.ActivityMainBinding
import com.github.passit.domain.model.Message
import com.github.passit.ui.models.conversations.UnreadMessagesViewModel
import com.github.passit.ui.receivers.ChatBroadcastReceiver
import com.github.passit.ui.services.ChatService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private lateinit var binding: ActivityMainBinding
    private val intentFilter = IntentFilter().apply {
        addAction(ChatService.BROADCAST_NEW_CONVERSATION)
        addAction(ChatService.BROADCAST_NEW_MESSAGE)
    }
    private val receiver = ChatBroadcastReceiver()
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    private val unreadMessagesViewModel: UnreadMessagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // start chat service if not already started
        bindService(ChatService::class.java, serviceConnection)
        receiver.setOnNewMessageListener(::onNewMessage)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.chatFragment) {
                val badge = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
                badge.number = 0
                badge.isVisible = false
            }
        }

        lifecycleScope.launchWhenResumed {
            unreadMessagesViewModel.count.collect { count ->
                if (binding.bottomNavigation.selectedItemId == R.id.chatFragment) return@collect
                val badge = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
                badge.number = count
                badge.isVisible = count != 0
            }
        }
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

    private fun onNewMessage(message: Message?) {
        if (binding.bottomNavigation.selectedItemId == R.id.chatFragment) return
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.chatFragment)
        badge.number = badge.number + 1
        badge.isVisible = true
    }
}