package com.github.passit.ui.screens.profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.map
import com.github.passit.R
import com.github.passit.databinding.FragmentProfileBinding
import com.github.passit.domain.model.auth.User
import com.github.passit.ui.contracts.insertion.ShowInsertionContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.models.insertions.GetUserInsertionsViewModel
import com.github.passit.ui.screens.auth.SignInActivity
import com.github.passit.ui.services.ChatService
import com.github.passit.ui.screens.list.InsertionsAdapter
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
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

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { bitmap ->
        launch {
            cropBitmap(bitmap, this@ProfileFragment)
        }
    }

    private val uploadPicture = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        launch {
            var bitmap : Bitmap? = null

            // Create the bitmap from the URI with different APIs depending on the system version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source: ImageDecoder.Source = ImageDecoder.createSource(requireActivity().applicationContext?.contentResolver!!, uri)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
            else {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().applicationContext?.contentResolver, uri)
            }

            cropBitmap(bitmap!!, this@ProfileFragment)
        }
    }

    private suspend fun cropBitmap(bitmap: Bitmap, frag: Fragment) {
        withContext(Dispatchers.IO) {
            ByteArrayOutputStream().use { bos ->
                bitmap.compress(CompressFormat.PNG, 50, bos)
                val byteArray: ByteArray = bos.toByteArray()

                val photoFile: File = File(getActivity()?.getCacheDir(), "IMG_" + System.currentTimeMillis())

                val fos: FileOutputStream = FileOutputStream(photoFile)
                fos.write(byteArray)
                fos.close()

                val srcUri = Uri.fromFile(photoFile)
                val destUri = Uri.fromFile(File(getActivity()?.getCacheDir(), "IMG_" + System.currentTimeMillis()))

                UCrop.of(srcUri, destUri)
                        .withMaxResultSize(1000, 1000)
                        .withAspectRatio(1.0F, 1.0F)
                        .start(requireActivity(), frag)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Handler", "Inside onActivityResult")
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            launch {
                val resultUri = UCrop.getOutput(data!!)
                getActivity()?.applicationContext?.contentResolver?.openInputStream(resultUri!!)?.buffered()?.use {
                    authModel.changeUserPicture(it).catch { e -> Log.i("changePicture", "$e") }.collect {
                        Log.i("upload", Gson().toJson(it))
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            ErrorAlert(requireActivity()).setTitle(getString(R.string.signin_error_alert_title)).setMessage(cropError?.localizedMessage?.toString()).show()
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
                }.collectLatest {  pagingData ->
                    Log.i("Insertions", "actually showing insertions")
                    insertionsAdapter.submitData(pagingData)
                    Log.i("Insertions", insertionsAdapter.snapshot().toString())
                }
            }
        }
        lifecycleScope.launchWhenResumed {
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

        binding.shootProfilePicBtn.setOnClickListener {
            launch {
                Dexter.withContext(context)
                    .withPermission(Manifest.permission.CAMERA)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse) {
                            takePicture.launch(null)
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}

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

        binding.uploadProfilePicBtn.setOnClickListener {
            launch {
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                                // uploadPicture.launch(null)
                                uploadPicture.launch("image/*")
                            }

                            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {}

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
        insertionsAdapter = InsertionsAdapter { insertionView -> showInsertion.launch(insertionView.id) }
        binding.insertionsRecyclerView.adapter = insertionsAdapter

        binding.refreshLayout.setOnRefreshListener {
            insertionsAdapter.refresh()
            binding.refreshLayout.isRefreshing = false
        }

        /*viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            insertionsAdapter.loadStateFlow
                    .collectLatest { loadState ->
                        binding.progressIndicator.isVisible = loadState.source.refresh is LoadState.Loading
                        binding.emptyLayout.emptyLayout.isVisible = (loadState.refresh is LoadState.NotLoading &&
                                insertionsAdapter.itemCount == 0)
                        binding.insertionsRecyclerView.isVisible = !binding.emptyLayout.emptyLayout.isVisible
                    }
        }*/
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}