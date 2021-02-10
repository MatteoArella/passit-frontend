package com.github.passit.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.R

import com.github.passit.databinding.FragmentSignUpFirstStepBinding
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.validators.isValidEmail
import com.github.passit.ui.validators.isValidPassword
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import com.github.passit.util.crypto.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFirstStepFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentSignUpFirstStepBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by activityViewModels()
    @Inject lateinit var cryptoManager: CryptoManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpFirstStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailTextLayout.setValidator(resources.getString(R.string.error_invalid_email)) { s -> s.isValidEmail() }
        binding.passwordTextLayout.setValidator(resources.getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }
        binding.confirmationPasswordTextLayout.setValidator(resources.getString(R.string.signup_passwords_missmatch)) { s -> s.isValidPassword() }

        binding.signUpNextBtn.setOnClickListener {
            // validate input
            if (binding.emailTextLayout.editText?.text.isNullOrEmpty() or binding.passwordTextLayout.editText?.text.isNullOrEmpty() or binding.confirmationPasswordTextLayout.editText?.text.isNullOrEmpty()) {
                launch { ErrorAlert(requireContext()).setTitle("Error").setMessage(resources.getString(R.string.signup_missing_parameters)).show() }
            }
            else if (binding.passwordTextLayout.editText?.text.toString() != binding.confirmationPasswordTextLayout.editText?.text.toString()) {
                launch { ErrorAlert(requireContext()).setTitle("Error").setMessage(resources.getString(R.string.signup_passwords_missmatch)).show() }
            } else {
                // bind data
                authModel.email.postValue(binding.emailTextLayout.editText?.text.toString())

                // encrypt password
                val encryptedData = cryptoManager.encrypt(binding.passwordTextLayout.editText?.text.toString(), cryptoManager.getCipherForEncryption())
                authModel.password.postValue(encryptedData)

                findNavController().navigate(R.id.action_signUpFirstStepFragment_to_signUpSecondStepFragment)
            }
        }

        binding.signUpGotoSignInBtn.setOnClickListener {
            val signInIntent = Intent(context, SignInActivity::class.java)
            startActivity(signInIntent)
            activity?.finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}