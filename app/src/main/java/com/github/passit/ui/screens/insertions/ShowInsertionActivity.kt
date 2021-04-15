package com.github.passit.ui.screens.insertions

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.github.passit.R
import com.github.passit.databinding.ActivityInsertionsShowBinding
import com.github.passit.domain.usecase.exception.conversation.CreateConversationError
import com.github.passit.ui.contracts.conversation.ChatContract
import com.github.passit.ui.models.auth.AuthViewModel
import com.github.passit.ui.models.conversations.CreateConversationViewModel
import com.github.passit.ui.models.insertions.GetInsertionViewModel
import com.github.passit.ui.models.insertions.InsertionView
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class ShowInsertionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val INSERTION_TAG = "insertionId"
    }

    private val authModel: AuthViewModel by viewModels()
    private val getInsertionViewModel: GetInsertionViewModel by viewModels()
    private lateinit var binding: ActivityInsertionsShowBinding

    private val createConversationViewModel: CreateConversationViewModel by viewModels()
    private val openConversation = registerForActivityResult(ChatContract()) {}

    private fun updateViewWithInsertionView(binding: ActivityInsertionsShowBinding, insertion: InsertionView) {
        binding.insertionTitle.text = insertion.title
        binding.insertionLocation.text = getString(R.string.show_insertion_location_format, insertion.location?.city ?: "", insertion.location?.state ?: "", insertion.location?.country ?: "")
        insertion.createdAt?.let { createdAt ->
            binding.insertionDate.text = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(createdAt)
        }
        binding.tutorName.text = getString(R.string.show_insertion_tutor_name_format, insertion.tutor?.givenName ?: "", insertion.tutor?.familyName ?: "")
        binding.insertionDescription.text = insertion.description
        insertion.tutor?.picture?.let { picture ->
            Picasso.get().load(picture.toURI().toString()).into(binding.tutorPicture)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInsertionsShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInsertionViewModel.insertion.value?.let { savedState ->
            updateViewWithInsertionView(binding, savedState)
        }

        lifecycleScope.launchWhenResumed {
            getInsertionViewModel.getInsertion(
                insertionId = intent.extras?.getString(INSERTION_TAG)!!,
            ).onStart {
                binding.progressIndicator.visibility = View.VISIBLE
            }.onCompletion {
                binding.progressIndicator.visibility = View.INVISIBLE
            }.catch { error ->
                launch { ErrorAlert(this@ShowInsertionActivity).setTitle("Displaying Error").setMessage(Gson().toJson(error)).show() }
            }.collect { insertion ->
                updateViewWithInsertionView(binding, insertion)
                registerContactTutorAction(insertion)
            }
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

    private suspend fun registerContactTutorAction(insertion: InsertionView) {
        insertion.tutor?.id?.let { tutorId ->
            authModel.fetchUserAttributes().collect { currentUser ->
                val canContactTutor = currentUser.id != tutorId
                binding.contactTutorExtendedFab.isVisible = canContactTutor
                if (!canContactTutor) return@collect
                binding.contactTutorExtendedFab.setOnClickListener {
                    lifecycleScope.launch {
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
            }
        }
    }
}