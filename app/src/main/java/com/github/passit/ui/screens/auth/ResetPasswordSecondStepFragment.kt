package com.github.passit.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.passit.R
import com.github.passit.databinding.FragmentResetPasswordSecondStepBinding
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.validators.isValidPassword
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordSecondStepFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentResetPasswordSecondStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordSecondStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordTextLayout.setValidator(getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }
        binding.confirmationPasswordTextLayout.setValidator(getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }

        binding.sendNewResetPasswordBtn.setOnClickListener {
            launch {
                authModel.resetPassword(authModel.email.value.toString()).catch { error ->
                        ErrorAlert(requireContext()).setTitle(getString(R.string.reset_password_error_alert_title)).setMessage(error.localizedMessage).show()
                    }.collect()
            }
        }

        binding.confirmResetPasswordBtn.setOnClickListener {
            if (binding.passwordTextLayout.editText?.text.toString() != binding.confirmationPasswordTextLayout.editText?.text.toString()) {
                launch { ErrorAlert(requireContext()).setTitle(getString(R.string.reset_password_error_alert_title)).setMessage(resources.getString(R.string.signup_passwords_missmatch)).show() }
            } else {
                launch {
                    authModel.confirmResetPassword(binding.passwordTextLayout.editText?.text.toString(), binding.verificationCodeTextLayout.editText?.text.toString())
                        .catch { error ->
                            ErrorAlert(requireContext()).setTitle(getString(R.string.reset_password_error_alert_title)).setMessage(error.localizedMessage).show()
                        }.onCompletion {
                            startActivity(Intent(requireContext(), SignInActivity::class.java))
                            activity?.finishAffinity()
                        }.collect()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}