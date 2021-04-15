package com.github.passit.core.extension

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity

fun <S : Service> Context.bindService(service: Class<S>, serviceConnection: ServiceConnection? = null): Intent {
    return Intent(this, service).also {
        startService(it)
        serviceConnection?.let { conn ->
            // BIND_ADJUST_WITH_ACTIVITY keeps service alive for the time it is bound to visible activity
            bindService(it, conn, AppCompatActivity.BIND_ADJUST_WITH_ACTIVITY)
        }
    }
}
