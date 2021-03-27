package com.github.passit.ui.screens.auth

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.R
import com.github.passit.databinding.ActivityConfirmCodeBinding
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

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
            launch {
                authModel.confirmSignUp(email, binding.verificationCodeTextLayout.editText?.text.toString())
                        .onStart {
                            binding.progressIndicator.visibility = View.VISIBLE
                        }.onCompletion {
                            binding.progressIndicator.visibility = View.INVISIBLE
                        }.catch { error ->
                            ErrorAlert(this@ConfirmCodeActivity).setTitle(getString(R.string.confirmation_error_alert_title)).setMessage(error.localizedMessage).show()
                        }.collect {
                            setResult(RESULT_OK)
                            this@ConfirmCodeActivity.finish()
                        }
            }
        }

        binding.resendConfirmationBtn.setOnClickListener {
            launch {
                authModel.resendConfirmationCode(email).catch { error ->
                    ErrorAlert(this@ConfirmCodeActivity).setTitle(getString(R.string.confirmation_error_alert_title)).setMessage(error.localizedMessage).show()
                }.collect()
            }
        }
    }
}
