package it.uniroma1.macc.project.ui.screens.auth

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE
import com.google.gson.Gson
import it.uniroma1.macc.project.data.repository.auth.AuthSignInResult
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.R
import it.uniroma1.macc.project.databinding.ActivitySigninBinding
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.exception.auth.SignInError
import it.uniroma1.macc.project.ui.contracts.auth.ConfirmCodeContract
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import it.uniroma1.macc.project.ui.screens.main.MainActivity
import it.uniroma1.macc.project.ui.validators.isValidEmail
import it.uniroma1.macc.project.ui.validators.setValidator
import kotlinx.coroutines.*
import it.uniroma1.macc.project.ui.screens.auth.ConfirmCodeActivity.Companion.RC_CONFIRMATION
import it.uniroma1.macc.project.ui.validators.isValidPassword
import it.uniroma1.macc.project.ui.view.ErrorAlert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@AndroidEntryPoint
class SignInActivity() : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivitySigninBinding
    private val authModel: AuthViewModel by viewModels()
    private val confirmSignUp = registerForActivityResult(ConfirmCodeContract(), ::onConfirmCodeResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // if present retrieve stored credentials

        binding.emailTextField.setValidator(getString(R.string.error_invalid_email)) { s -> s.isValidEmail() }
        binding.passwordTextField.setValidator(getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }
        binding.signInGotoSignUpBtn.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }
        binding.resetPasswordBtn.setOnClickListener { startActivity(Intent(this, ResetPasswordActivity::class.java)) }
        binding.signInBtn.setOnClickListener { signIn() }
        binding.googleSignInBtn.setOnClickListener { signInWithGoogle() }
    }

    private fun signIn() {
        if (!binding.emailTextField.text.isNullOrEmpty() and !binding.passwordTextField.text.isNullOrEmpty()) {
            lifecycleScope.launchWhenResumed {
                authModel.signIn(binding.emailTextField.text.toString(), binding.passwordTextField.text.toString()).collect(::handleSignInResult)
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
                    confirmSignUp.launch(binding.emailTextField.text.toString())
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
            WEB_UI_SIGN_IN_ACTIVITY_CODE -> data?.let { lifecycleScope.launchWhenResumed { authModel.handleFederatedSignInResponse(it).collect() } }
        }
    }
}