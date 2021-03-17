package com.github.passit.ui.screens.auth

import android.content.Intent
import android.view.View
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.github.passit.R
import com.github.passit.databinding.FragmentSignUpSecondStepBinding
import com.github.passit.ui.view.ErrorAlert
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.ui.contracts.auth.ConfirmCodeContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.core.platform.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
                ).onStart {
                    binding.progressIndicator.visibility = View.VISIBLE
                }.onCompletion {
                    binding.progressIndicator.visibility = View.INVISIBLE
                }.catch { error ->
                    ErrorAlert(requireContext()).setTitle(getString(R.string.signup_error_alert_title)).setMessage(error.localizedMessage).show()
                }.collect {
                    confirmSignUp.launch(authModel.email.value)
                }
            }
        }
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