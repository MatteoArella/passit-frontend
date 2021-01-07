package it.uniroma1.macc.project.ui.screens.launch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.core.Amplify
import it.uniroma1.macc.project.R
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.ui.screens.auth.SignInActivity
import it.uniroma1.macc.project.ui.screens.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.data.repository.auth.AuthSessionResult
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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