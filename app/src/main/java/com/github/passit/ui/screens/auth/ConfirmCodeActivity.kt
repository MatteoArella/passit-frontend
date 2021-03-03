package com.github.passit.ui.screens.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.github.passit.R
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.core.domain.Result
import com.github.passit.databinding.ActivityConfirmCodeBinding
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.validators.setValidator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ConfirmCodeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        const val CONFIRMATION_KEY = "email"
    }

    private lateinit var binding: ActivityConfirmCodeBinding

    private val authModel: AuthViewModel by viewModels()

    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.verificationCodeTextLayout.setValidator(getString(R.string.verification_code_empty_error)) { s -> s.isNotEmpty() }

        email =  intent.extras?.getString(CONFIRMATION_KEY)!!

        binding.confirmSignUpBtn.setOnClickListener {
            launch { authModel.confirmSignUp(email, binding.verificationCodeTextLayout.editText?.text.toString()).collect(::handleConfirmSignUpResult) }
        }

        binding.resendConfirmationBtn.setOnClickListener {
            launch { authModel.resendConfirmationCode(email).collect(::handleResendConfirmCodeResult) }
        }
    }

    private fun handleConfirmSignUpResult(confirmSignUp: Result<Error, AuthSignUp>) {
        confirmSignUp
            .onSuccess {
                setResult(RESULT_OK)
                this@ConfirmCodeActivity.finish()
            }
            .onError { error ->
                ErrorAlert(this@ConfirmCodeActivity).setTitle(getString(R.string.confirmation_error_alert_title)).setMessage(error.localizedMessage).show()
            }
            .onStateLoading { binding.progressIndicator.visibility = View.VISIBLE }
            .onStateLoaded { binding.progressIndicator.visibility = View.INVISIBLE }
    }

    private fun handleResendConfirmCodeResult(resendConfirmationCode: Result<Error, AuthSignUp>) {
        resendConfirmationCode.onError { error ->
            ErrorAlert(this@ConfirmCodeActivity).setTitle(getString(R.string.confirmation_error_alert_title)).setMessage(error.localizedMessage).show()
        }
    }
}
