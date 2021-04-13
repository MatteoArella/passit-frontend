package com.github.passit.ui.screens.insertions

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.passit.R
import com.github.passit.databinding.ActivityInsertionsShowBinding
import com.github.passit.domain.model.InsertionStatus
import com.github.passit.domain.usecase.exception.conversation.CreateConversationError
import com.github.passit.ui.contracts.conversation.ChatContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.models.conversations.CreateConversationViewModel
import com.github.passit.ui.models.insertions.InsertionView
import com.github.passit.ui.models.insertions.InsertionViewModel
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class ShowInsertionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val INSERTION_TAG = "insertionId"
    }

    private val authModel: AuthViewModel by viewModels()
    private val insertionViewModel: InsertionViewModel by viewModels()
    private lateinit var binding: ActivityInsertionsShowBinding

    private val createConversationViewModel: CreateConversationViewModel by viewModels()
    private val openConversation = registerForActivityResult(ChatContract()) {}

    private fun updateViewWithInsertionView(binding: ActivityInsertionsShowBinding, insertion: InsertionView) {
        binding.insertionTitle.text = insertion.title
        binding.insertionLocation.text = getString(R.string.show_insertion_location_format, insertion.location?.city ?: "", insertion.location?.state ?: "", insertion.location?.country ?: "")
        insertion.updatedAt?.let { updatedAt ->
            binding.insertionDate.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(updatedAt)
        }
        binding.tutorName.text = getString(R.string.show_insertion_tutor_name_format, insertion.tutor?.givenName ?: "", insertion.tutor?.familyName ?: "")
        binding.insertionDescription.text = insertion.description
        insertion.tutor?.picture?.let { picture ->
            Picasso.get().load(picture.toURI().toString()).placeholder(R.drawable.ic_person).into(binding.tutorPicture)
        }
        if (insertion.status != InsertionStatus.CLOSED) {
            binding.insertionStatus.visibility = View.GONE
        } else {
            binding.insertionStatus.text = getString(R.string.show_insertion_status_format, insertion.status)
            binding.insertionStatus.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInsertionsShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launchWhenResumed {
            insertionViewModel.insertion.collectLatest { insertion ->
                insertion?.let { savedState ->
                    updateViewWithInsertionView(binding, savedState)
                    registerActions(savedState)
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            insertionViewModel.getInsertion(
                insertionId = intent.extras?.getString(INSERTION_TAG)!!,
            ).onStart {
                binding.progressIndicator.visibility = View.VISIBLE
            }.onCompletion {
                binding.progressIndicator.visibility = View.INVISIBLE
            }.catch { error ->
                launch { ErrorAlert(this@ShowInsertionActivity).setTitle("Displaying Error").setMessage(Gson().toJson(error)).show() }
            }.collect()
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.contactTutorExtendedFab.shrink()
            } else {
                binding.contactTutorExtendedFab.extend()
            }
        }

        binding.insertionToolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private suspend fun registerActions(insertion: InsertionView) {
        insertion.tutor?.id?.let { tutorId ->
            authModel.fetchUserAttributes().collect { currentUser ->
                val canContactTutor = currentUser.id != tutorId
                val canModifyInsertion = currentUser.id == tutorId
                binding.contactTutorExtendedFab.isVisible = canContactTutor
                binding.openCloseInsertionBtn.isVisible = canModifyInsertion
                if (canContactTutor) {
                    binding.contactTutorExtendedFab.setOnClickListener {
                        createConversation(tutorId)
                    }
                }
                if (canModifyInsertion) {
                    if (insertion.status == InsertionStatus.OPEN) {
                        binding.openCloseInsertionBtn.text = getString(R.string.show_insertion_close_button)
                        binding.openCloseInsertionBtn.setOnClickListener {
                            closeInsertion(insertionId = insertion.id)
                        }
                    } else if (insertion.status == InsertionStatus.CLOSED) {
                        binding.openCloseInsertionBtn.text = getString(R.string.show_insertion_open_button)
                        binding.openCloseInsertionBtn.setOnClickListener {
                            openInsertion(insertionId = insertion.id)
                        }
                    }
                }
            }
        }
    }

    private fun createConversation(tutorId: String) {
        lifecycleScope.launchWhenResumed {
            createConversationViewModel.createConversation(tutorId).catch { error ->
                when (error) {
                    is CreateConversationError.SameUser -> {
                        ErrorAlert(this@ShowInsertionActivity).setTitle("Create conversation Error").setMessage(getString(R.string.show_insertion_conversation_same_user_error)).show()
                    }
                    else -> ErrorAlert(this@ShowInsertionActivity).setTitle("Create conversation Error").setMessage(Gson().toJson(error)).show()
                }
            }.collect { conversation ->
                openConversation.launch(conversation)
            }
        }
    }

    private fun openInsertion(insertionId: String) {
        lifecycleScope.launchWhenResumed {
            insertionViewModel.updateInsertion(insertionId = insertionId, status = InsertionStatus.OPEN).collect()
        }
    }

    private fun closeInsertion(insertionId: String) {
        lifecycleScope.launchWhenResumed {
            insertionViewModel.updateInsertion(insertionId = insertionId, status = InsertionStatus.CLOSED).collect()
        }
    }
}