package it.uniroma1.macc.project.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import it.uniroma1.macc.project.R

import it.uniroma1.macc.project.databinding.FragmentSignUpFirstStepBinding
import it.uniroma1.macc.project.ui.models.auth.AuthViewModel
import it.uniroma1.macc.project.ui.validators.isValidEmail
import it.uniroma1.macc.project.ui.validators.isValidPassword
import it.uniroma1.macc.project.ui.validators.setValidator
import it.uniroma1.macc.project.ui.view.ErrorAlert
import it.uniroma1.macc.project.util.crypto.CryptoManager
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
    ): View? {
        _binding = FragmentSignUpFirstStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.emailTextField.setValidator(resources.getString(R.string.error_invalid_email)) { s -> s.isValidEmail() }
        binding.passwordTextField.setValidator(resources.getString(R.string.error_invalid_password)) { s -> s.isValidPassword() }
        binding.confirmationPasswordTextField.setValidator(resources.getString(R.string.signup_passwords_missmatch)) { s -> s.isValidPassword() }

        binding.signUpNextBtn.setOnClickListener {
            // validate input
            if (binding.emailTextField.text.isNullOrEmpty() or binding.passwordTextField.text.isNullOrEmpty() or binding.confirmationPasswordTextField.text.isNullOrEmpty()) {
                launch { ErrorAlert(requireContext()).setTitle("Error").setMessage(resources.getString(R.string.signup_missing_parameters)).show() }
            }
            else if (binding.passwordTextField.text.toString() != binding.confirmationPasswordTextField.text.toString()) {
                launch { ErrorAlert(requireContext()).setTitle("Error").setMessage(resources.getString(R.string.signup_passwords_missmatch)).show() }
            } else {
                // bind data
                authModel.email.postValue(binding.emailTextField.text.toString())

                // encrypt password
                val encryptedData = cryptoManager.encrypt(binding.passwordTextField.text.toString(), cryptoManager.getCipherForEncryption())
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