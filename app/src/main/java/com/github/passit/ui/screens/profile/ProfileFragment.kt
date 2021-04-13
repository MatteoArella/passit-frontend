package com.github.passit.ui.screens.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.github.passit.BuildConfig
import com.github.passit.R
import com.github.passit.databinding.FragmentProfileBinding
import com.github.passit.domain.model.auth.UploadResult
import com.github.passit.domain.model.auth.User
import com.github.passit.ui.contracts.insertion.ShowInsertionContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.models.insertions.GetUserInsertionsViewModel
import com.github.passit.ui.screens.auth.SignInActivity
import com.github.passit.ui.screens.list.InsertionsAdapter
import com.github.passit.ui.services.ChatService
import com.github.passit.ui.view.Alert
import com.github.passit.ui.view.ErrorAlert
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.lang.Exception
import java.util.*


@AndroidEntryPoint
class ProfileFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val showInsertion = registerForActivityResult(ShowInsertionContract()) { }

    private val authModel: AuthViewModel by activityViewModels()

    private var userInsertionsLoadJob: Job? = null
    private var userInfoLoadJob: Job? = null


    private val getUserInsertionsModel: GetUserInsertionsViewModel by viewModels() // by activityViewModels()
    private lateinit var insertionsAdapter: InsertionsAdapter

    private var pictureUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        it?.let { cropBitmap(pictureUri) }
    }

    private val getPictureContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        pictureUri = uri
        cropBitmap(uri)
    }

    private fun cropBitmap(pictureUri: Uri?) {
        pictureUri?.let { uri ->
            val destUri = Uri.fromFile(getPictureTempFile())

            val options = UCrop.Options().apply {
                setCompressionFormat(CompressFormat.JPEG)
                setCompressionQuality(50)
            }

            UCrop.of(uri, destUri)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(1.0F, 1.0F)
                .withOptions(options)
                .start(requireActivity(), this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Handler", "Inside onActivityResult")
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            launch {
                UCrop.getOutput(data!!)?.toFile()?.let { pictureFile ->
                    authModel.changeUserPicture(pictureFile)
                        .onStart {
                            binding.pictureProgressIndicator.visibility = View.INVISIBLE
                            binding.pictureProgressIndicator.isIndeterminate = false
                            binding.pictureProgressIndicator.visibility = View.VISIBLE
                        }
                        .catch { e -> Log.i("changePicture", "$e") }
                        .onCompletion {
                            binding.pictureProgressIndicator.visibility = View.INVISIBLE
                            binding.pictureProgressIndicator.isIndeterminate = true
                            binding.pictureProgressIndicator.visibility = View.VISIBLE
                        }
                        .collectIndexed { _, uploadResult ->
                            when (uploadResult) {
                                is UploadResult.UploadProgress -> { binding.pictureProgressIndicator.progress = (uploadResult.progress * 100).toInt() }
                                else -> {}
                            }
                        }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            ErrorAlert(requireActivity()).setTitle(getString(R.string.change_picture_error_alert_title)).setMessage(
                    cropError?.localizedMessage?.toString()
            ).show()
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

        userInfoLoadJob = lifecycleScope.launch {
            authModel.fetchUserAttributes().collect {
                Log.i("User id", it.id)
                getUserInsertionsModel.getUserInsertions(it.id).catch { error ->
                    Log.e("unknown_error", error.toString())
                }.collectLatest { pagingData ->
                    Log.i("Insertions", "actually showing insertions")
                    insertionsAdapter.submitData(pagingData)
                    Log.i("Insertions", insertionsAdapter.snapshot().toString())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            authModel.currentUser.collect(::showUserProfile)
        }

        binding.signOutBtn.setOnClickListener {
            lifecycleScope.launch {
                authModel.signOut().onStart {
                    //binding.progressIndicator.visibility = View.VISIBLE
                }.onCompletion {
                    // binding.progressIndicator.visibility = View.INVISIBLE
                    // stop chat service
                    requireActivity().let { it.stopService(Intent(it, ChatService::class.java)) }
                    startActivity(Intent(context, SignInActivity::class.java))
                    requireActivity().finish()
                }.catch { error ->
                    ErrorAlert(requireContext()).setTitle("Error").setMessage(error.message).show()
                }.collect()
            }
        }

        view.setOnTouchListener { v, event ->
            val viewRect = Rect()
            binding.plusProfilePictureImageView.getGlobalVisibleRect(viewRect)
            if (!viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                binding.plusProfilePictureImageView.isVisible = false
            }
            if (event.action == MotionEvent.ACTION_UP) v.performClick()

            true
        }

        binding.changeProfilePictureCard.setOnClickListener {
            binding.plusProfilePictureImageView.isVisible = true
        }

        binding.plusProfilePictureImageView.setOnClickListener {
            binding.plusProfilePictureImageView.isVisible = false

            Alert(requireContext())
                .setItems(R.array.dialog_change_profile_picture) { dialog, item ->
                    when (item) {
                        0 -> takeProfilePicture()
                        1 -> chooseProfilePicture()
                        else -> {
                            dialog.dismiss()
                        }
                    }
                }
                .setTitle(getString(R.string.profile_change_profile_picture_title))
                .setPositiveButton(null, null)
                .setNeutralButton(getString(R.string.profile_new_picture_cancel)) { dialog, _ ->
                    binding.plusProfilePictureImageView.isVisible = false
                    dialog.dismiss()
                }
                .show()
        }

        insertionsAdapter = InsertionsAdapter { insertionView -> showInsertion.launch(insertionView.id) }
        binding.insertionsRecyclerView.adapter = insertionsAdapter

        binding.refreshLayout.setOnRefreshListener {
            insertionsAdapter.refresh()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun takeProfilePicture() {
        launch {
            Dexter.withContext(context)
                    .withPermissions(
                            listOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(response: MultiplePermissionsReport) {
                            if (response.areAllPermissionsGranted()) {
                                pictureUri = getUriFromFile(getPictureTempFile())
                                takePicture.launch(pictureUri)
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permission: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }
                    })
                    .check()
        }
    }

    private fun chooseProfilePicture() {
        launch {
            Dexter.withContext(context)
                    .withPermissions(
                            listOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                    )
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(response: MultiplePermissionsReport) {
                            if (response.areAllPermissionsGranted()) {
                                getPictureContent.launch("image/*")
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                                permission: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                        ) {
                            token?.continuePermissionRequest()
                        }
                    })
                    .check()
        }
    }

    private fun getPictureTempFile(): File {
        return File.createTempFile("IMG_", ".jpg", requireActivity().filesDir)
    }

    private fun getUriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(
                requireActivity(),
                "${BuildConfig.APPLICATION_ID}.provider",
                file
        )
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
                binding.pictureProgressIndicator.visibility = View.VISIBLE
                Picasso.get().load(picture.toURI().toString()).placeholder(R.drawable.ic_person).into(binding.profilePicture, object: Callback {
                    override fun onSuccess() {
                        binding.pictureProgressIndicator.visibility = View.INVISIBLE
                    }
                    override fun onError(e: Exception?) {
                        binding.pictureProgressIndicator.visibility = View.INVISIBLE
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}