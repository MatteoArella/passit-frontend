package com.github.passit.ui.screens.auth

import android.content.Intent
import android.view.View
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.passit.domain.usecase.core.Result
import com.github.passit.data.repository.auth.AuthSignUpResult
import com.github.passit.databinding.FragmentSignUpSecondStepBinding
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.domain.model.SignUpUserAttributes
import com.github.passit.ui.contracts.auth.ConfirmCodeContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.util.crypto.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpSecondStepFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentSignUpSecondStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by activityViewModels()
    @Inject lateinit var cryptoManager: CryptoManager

    private val confirmSignUp = registerForActivityResult(ConfirmCodeContract(), ::onConfirmCodeResult)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpSecondStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpGotoSignInBtn.setOnClickListener {
            startActivity(Intent(context, SignInActivity::class.java))
        }

        binding.signUpBtn.setOnClickListener {
            val attributes = SignUpUserAttributes(
                    email = authModel.email.value!!,
                    familyName = binding.familyNameTextField.text.toString(),
                    givenName = binding.givenNameTextField.text.toString(),
                    phoneNumber = binding.phoneNumberTextField.text.toString(),
                    birthDate = binding.birthDateTextField.text.toString()
            )
            launch {
                authModel.signUp(
                    authModel.email.value!!,
                    cryptoManager.decrypt(authModel.password.value!!.encrypted, cryptoManager.getCipherForDecryption(authModel.password.value!!.initializationVector)),
                    attributes
                ).collect(::handleSignUpResult)
            }
        }
    }

    private fun handleSignUpResult(authSignUpResult: Result<Error, AuthSignUpResult>) {
        authSignUpResult
            .onSuccess {
                confirmSignUp.launch(authModel.email.value)
            }
            .onError { error ->
                ErrorAlert(requireContext()).setTitle("Sign Up Error").setMessage(error.localizedMessage).show()
            }
            .onStateLoading { binding.progressIndicator.visibility = View.VISIBLE }
            .onStateLoaded { binding.progressIndicator.visibility = View.INVISIBLE }
    }

    private fun onConfirmCodeResult(confirmed: Boolean) {
        if (confirmed) {
            startActivity(Intent(context, SignInActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}