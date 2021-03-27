package com.github.passit.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE
import com.github.passit.R
import com.github.passit.databinding.ActivitySigninBinding
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.usecase.exception.auth.SignInError
import com.github.passit.ui.contracts.auth.ConfirmCodeContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.screens.main.MainActivity
import com.github.passit.ui.validators.isValidEmail
import com.github.passit.ui.validators.isValidPassword
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class SignInActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivitySigninBinding
    private val authModel: AuthViewModel by viewModels()
    private val confirmSignUp = registerForActivityResult(ConfirmCodeContract(), ::onConfirmCodeResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // if present retrieve stored credentials

        binding.emailTextLayout.setValidator(getString(R.string.error_invalid_email)) { s -> s.isValidEmail() }
        binding.passwordTextLayout.setValidator(getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }
        binding.signInGotoSignUpBtn.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
        binding.resetPasswordBtn.setOnClickListener { startActivity(Intent(this, ResetPasswordActivity::class.java)) }
        binding.signInBtn.setOnClickListener { signIn() }
        binding.googleSignInBtn.setOnClickListener { signInWithGoogle() }
    }

    private fun signIn() {
        if (!binding.emailTextLayout.editText?.text.isNullOrEmpty() and !binding.passwordTextLayout.editText?.text.isNullOrEmpty()) {
            lifecycleScope.launchWhenResumed {
                authModel.signIn(binding.emailTextLayout.editText?.text.toString(), binding.passwordTextLayout.editText?.text.toString())
                        .handleSignInFlow().collect { data ->
                    if (data.isSignedIn) {
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        this@SignInActivity.finish()
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        launch {
            authModel.signInWithGoogle(this@SignInActivity).handleSignInFlow().collect { data ->
                if (data.isSignedIn) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    this@SignInActivity.finish()
                }
            }
        }
    }

    private fun Flow<AuthSignIn>.handleSignInFlow(): Flow<AuthSignIn> {
        return this.onStart {
            binding.progressIndicator.visibility = View.VISIBLE
        }.onCompletion {
            binding.progressIndicator.visibility = View.INVISIBLE
        }.catch { error ->
            when (error) {
                is SignInError.Unconfirmed -> {
                    confirmSignUp.launch(binding.emailTextLayout.editText?.text.toString())
                }
                else -> launch {
                    ErrorAlert(this@SignInActivity).setTitle(getString(R.string.signin_error_alert_title)).setMessage(error.localizedMessage?.toString()).show()
                }
            }
        }
    }

    private fun onConfirmCodeResult(confirmed: Boolean) {
        if (confirmed) {
            signIn()
        }
    }

    @Suppress("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            WEB_UI_SIGN_IN_ACTIVITY_CODE -> data?.let {
                lifecycleScope.launchWhenResumed {
                    authModel.handleFederatedSignInResponse(it).collect()
                }
            }
        }
    }
}