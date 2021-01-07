package it.uniroma1.macc.project.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import it.uniroma1.macc.project.R
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.databinding.FragmentResetPasswordSecondStepBinding
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import it.uniroma1.macc.project.ui.view.ErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordSecondStepFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentResetPasswordSecondStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordSecondStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sendNewResetPasswordBtn.setOnClickListener {
            launch {
                authModel.resetPassword(authModel.email.value.toString()).collect { result ->
                    result.onError { error ->
                        ErrorAlert(requireContext()).setTitle("Reset Password Error").setMessage(error.localizedMessage).show()
                    }
                }
            }
        }

        binding.confirmResetPasswordBtn.setOnClickListener {
            if (binding.passwordTextField.text.toString() != binding.confirmationPasswordTextField.text.toString()) {
                launch { ErrorAlert(requireContext()).setTitle("Error").setMessage(resources.getString(R.string.signup_passwords_missmatch)).show() }
            } else {
                launch {
                    authModel.confirmResetPassword(binding.passwordTextField.text.toString(), binding.verificationCodeTextField.text.toString()).collect { result ->
                        result.onSuccess {
                                startActivity(Intent(requireContext(), SignInActivity::class.java))
                                activity?.finishAffinity()
                            }
                            .onError { error ->
                                ErrorAlert(requireContext()).setTitle("Reset Password Error").setMessage(error.localizedMessage).show()
                            }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}