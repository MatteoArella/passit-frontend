package it.uniroma1.macc.project.ui.screens.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import it.uniroma1.macc.project.R
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.databinding.FragmentResetPasswordFirstStepBinding
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import it.uniroma1.macc.project.ui.validators.isValidEmail
import it.uniroma1.macc.project.ui.validators.setValidator
import it.uniroma1.macc.project.ui.view.ErrorAlert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFirstStepFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentResetPasswordFirstStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordFirstStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailTextField.setValidator("Email is not valid") { s -> s.isValidEmail() }

        binding.confirmResetEmailBtn.setOnClickListener {
            launch {
                authModel.resetPassword(binding.emailTextField.text.toString()).collect { result ->
                    result
                        .onSuccess {
                            authModel.email.postValue(binding.emailTextField.text.toString())
                            findNavController().navigate(R.id.action_resetPasswordFirstStepFragment_to_resetPasswordSecondStepFragment)
                        }
                        .onError { error ->
                            ErrorAlert(requireContext()).setTitle("Reset Password Error").setMessage(error.localizedMessage).show()
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