package it.uniroma1.macc.project.ui.screens.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import it.uniroma1.macc.project.data.repository.auth.AuthSignUpResult
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.databinding.ActivityConfirmCodeBinding
import it.uniroma1.macc.project.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import it.uniroma1.macc.project.ui.validators.setValidator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ConfirmCodeActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        const val CONFIRMATION_KEY = "email"
        const val RC_CONFIRMATION = 1
    }

    private lateinit var binding: ActivityConfirmCodeBinding

    private val authModel: AuthViewModel by viewModels()

    private lateinit var email: String
    private val TAG = "auth"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.verificationCodeTextField.setValidator("Verification code cannot be empty") { s -> s.isNotEmpty() }

        email =  intent.extras?.getString(CONFIRMATION_KEY)!!

        binding.confirmSignUpBtn.setOnClickListener {
            launch { authModel.confirmSignUp(email, binding.verificationCodeTextField.text.toString()).collect(::handleConfirmSignUpResult) }
        }

        binding.resendConfirmationBtn.setOnClickListener {
            launch { authModel.resendConfirmationCode(email).collect(::handleResendConfirmCodeResult) }
        }
    }

    private fun handleConfirmSignUpResult(confirmSignUpResult: Result<Error, AuthSignUpResult>) {
        confirmSignUpResult
            .onSuccess {
                setResult(RESULT_OK)
                this@ConfirmCodeActivity.finish()
            }
            .onError { error ->
                ErrorAlert(this@ConfirmCodeActivity).setTitle("Confirmation Error").setMessage(error.localizedMessage).show()
            }
            .onStateLoading { binding.progressIndicator.visibility = View.VISIBLE }
            .onStateLoaded { binding.progressIndicator.visibility = View.INVISIBLE }
    }

    private fun handleResendConfirmCodeResult(resendConfirmationCodeResult: Result<Error, AuthSignUpResult>) {
        resendConfirmationCodeResult.onError { error ->
            ErrorAlert(this@ConfirmCodeActivity).setTitle("Confirmation Error").setMessage(error.localizedMessage).show()
        }
    }
}
