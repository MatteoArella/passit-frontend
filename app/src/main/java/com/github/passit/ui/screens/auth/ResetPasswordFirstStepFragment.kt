package com.github.passit.ui.screens.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.passit.R
import com.github.passit.databinding.FragmentResetPasswordFirstStepBinding
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.validators.isValidEmail
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFirstStepFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentResetPasswordFirstStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordFirstStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailTextLayout.setValidator(getString(R.string.error_invalid_email)) { s -> s.isValidEmail() }

        binding.confirmResetEmailBtn.setOnClickListener {
            launch {
                authModel.resetPassword(binding.emailTextLayout.editText?.text.toString())
                        .catch { error ->
                            ErrorAlert(requireContext()).setTitle(getString(R.string.reset_password_error_alert_title)).setMessage(error.localizedMessage).show()
                        }.collect {
                            authModel.email.postValue(binding.emailTextLayout.editText?.text.toString())
                            findNavController().navigate(R.id.action_resetPasswordFirstStepFragment_to_resetPasswordSecondStepFragment)
                        }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}