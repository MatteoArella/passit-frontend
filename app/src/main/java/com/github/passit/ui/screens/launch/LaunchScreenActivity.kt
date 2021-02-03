package com.github.passit.ui.screens.launch

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.R
import com.github.passit.ui.screens.auth.SignInActivity
import com.github.passit.ui.screens.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.ui.models.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LaunchScreenActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val authModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launch_screen)

        launch {
            authModel.fetchAuthSession().collect { result ->
                result.onSuccess { authSession ->
                    if (authSession.isSignedIn) {
                        startActivity(Intent(this@LaunchScreenActivity, MainActivity::class.java))
                        this@LaunchScreenActivity.finish()
                    } else {
                        startActivity(Intent(this@LaunchScreenActivity, SignInActivity::class.java))
                        this@LaunchScreenActivity.finish()
                    }
                }
            }
        }
    }
}