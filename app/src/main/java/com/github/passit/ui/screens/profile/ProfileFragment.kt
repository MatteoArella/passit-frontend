package com.github.passit.ui.screens.profile

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import com.github.passit.R
import com.github.passit.databinding.FragmentProfileBinding
import com.github.passit.domain.model.User
import com.github.passit.domain.usecase.core.Result
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.screens.auth.SignInActivity
import com.github.passit.ui.view.ErrorAlert
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.catch

@AndroidEntryPoint
class ProfileFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authModel: AuthViewModel by activityViewModels()
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { bitmap ->
        bitmap?.let {
            launch {
                ByteArrayOutputStream().use { bos ->
                    withContext(Dispatchers.IO) { bitmap.compress(CompressFormat.PNG, 50, bos) }
                    ByteArrayInputStream(bos.toByteArray()).use { bs ->
                        authModel.changeUserPicture(bs).catch { e -> Log.i("changePicture", "$e") }.collect {
                            Log.i("upload", Gson().toJson(it))
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            authModel.fetchUserAttributes().collect()
        }

        authModel.currentUser.observe(viewLifecycleOwner, ::showUserProfile)

        binding.signOutBtn.setOnClickListener {
            launch { authModel.signOut().collect(::handleSignOutResult) }
        }

        binding.changeProfilePictureCard.setOnClickListener {
            binding.plusProfilePictureImageView.visibility = View.VISIBLE
        }
        binding.coverView.setOnClickListener {
            if (binding.plusProfilePictureImageView.visibility == View.VISIBLE) {
                binding.plusProfilePictureImageView.visibility = View.GONE
            }
        }

        binding.plusProfilePictureImageView.setOnClickListener {
            launch {
                Dexter.withContext(context)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            takePicture.launch(null)
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) { }

                        override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }
                    })
                    .check()
            }
        }
    }

    private fun showUserProfile(profile: User?) {
        profile?.let {
            binding.emailTextField.text = profile.email
            binding.userName.text = getString(
                R.string.profile_username,
                it.givenName.capitalize(Locale.getDefault()),
                it.familyName.capitalize(Locale.getDefault())
            )
            it.phoneNumber?.let { phone ->
                binding.phoneTextField.text = phone
                binding.phoneTextField.visibility = View.VISIBLE
            }
            it.picture?.let { picture ->
                Picasso.get().load(picture.toURI().toString()).into(binding.profilePicture)
            }
        }
    }

    private fun handleSignOutResult(result: Result<Error, Unit>) {
        result
            .onSuccess {
                startActivity(Intent(context, SignInActivity::class.java))
                requireActivity().finish()
            }
            .onError { error ->
                ErrorAlert(requireContext()).setTitle("Error").setMessage(error.message).show()
            }
            .onStateLoading { binding.progressIndicator.visibility = View.VISIBLE }
            .onStateLoaded { binding.progressIndicator.visibility = View.INVISIBLE }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}