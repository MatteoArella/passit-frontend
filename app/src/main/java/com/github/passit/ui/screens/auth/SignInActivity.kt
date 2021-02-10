package com.github.passit.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE
import com.github.passit.data.repository.auth.AuthSignInResult
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.R
import com.github.passit.databinding.ActivitySigninBinding
import com.github.passit.domain.usecase.core.Result
import com.github.passit.domain.usecase.exception.auth.SignInError
import com.github.passit.ui.contracts.auth.ConfirmCodeContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.screens.main.MainActivity
import com.github.passit.ui.validators.isValidEmail
import com.github.passit.ui.validators.setValidator
import kotlinx.coroutines.*
import com.github.passit.ui.validators.isValidPassword
import com.github.passit.ui.view.ErrorAlert
import kotlinx.coroutines.flow.collect

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
                authModel.signIn(binding.emailTextLayout.editText?.text.toString(), binding.passwordTextLayout.editText?.text.toString()).collect(::handleSignInResult)
            }
        }
    }

    private fun signInWithGoogle() {
        launch {
            authModel.signInWithGoogle(this@SignInActivity).collect(::handleSignInResult)
        }
    }

    private fun handleSignInResult(authSignInResult: Result<Error, AuthSignInResult>) {
        authSignInResult
            .onSuccess { data ->
                if (data.isSignedIn) {
                    startActivity(Intent(this@SignInActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    this@SignInActivity.finish()
                }
            }
            .onError { error -> when (error) {
                is SignInError.Unconfirmed -> {
                    confirmSignUp.launch(binding.emailTextLayout.editText?.text.toString())
                }
                else -> launch {
                    ErrorAlert(this@SignInActivity).setTitle("Sign In Error").setMessage(error.localizedMessage?.toString()).show()
                }
            }}
            .onStateLoading {
                binding.progressIndicator.visibility = View.VISIBLE
            }
            .onStateLoaded {
                binding.progressIndicator.visibility = View.INVISIBLE
            }
    }

    private fun onConfirmCodeResult(confirmed: Boolean) {
        if (confirmed) {
            signIn()
        }
    }

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