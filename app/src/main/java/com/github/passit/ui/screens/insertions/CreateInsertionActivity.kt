package com.github.passit.ui.screens.insertions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.R
import com.github.passit.core.domain.Result
import com.github.passit.databinding.ActivityInsertionsCreateBinding
import com.github.passit.domain.model.Insertion
import com.github.passit.ui.models.insertions.InsertionViewModel
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateInsertionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val INSERTION_TAG = "insertion"
        const val INSERTION_TITLE_TAG = "title"
        const val INSERTION_DESCRIPTION_TAG = "description"
        const val INSERTION_SUBJECT_TAG = "subject"
    }
    private val insertionModel: InsertionViewModel by viewModels()
    private lateinit var binding: ActivityInsertionsCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInsertionsCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.titleTextLayout.setValidator(getString(R.string.create_insertion_title_empty_error)) { it.isNotEmpty() }
        binding.descriptionTextLayout.setValidator(getString(R.string.create_insertion_description_empty_error)) { it.isNotEmpty() }
        binding.subjectTextLayout.setValidator(getString(R.string.create_insertion_subject_empty_error)) { it.isNotEmpty() }

        savedInstanceState?.let { savedState ->
            binding.titleTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_TITLE_TAG))
            binding.descriptionTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_DESCRIPTION_TAG))
            binding.subjectTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_SUBJECT_TAG))
        }

        binding.createInsertionBtn.setOnClickListener {
            if (!binding.titleTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.descriptionTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.subjectTextLayout.editText?.text.isNullOrEmpty()) {
                launch {
                    insertionModel.createInsertion(
                        title = binding.titleTextLayout.editText?.text.toString(),
                        description = binding.descriptionTextLayout.editText?.text.toString(),
                        subject = binding.subjectTextLayout.editText?.text.toString()
                    ).collect(::onCreateInsertionResult)
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INSERTION_TITLE_TAG, binding.titleTextLayout.editText?.text.toString())
        outState.putString(INSERTION_DESCRIPTION_TAG, binding.descriptionTextLayout.editText?.text.toString())
        outState.putString(INSERTION_SUBJECT_TAG, binding.subjectTextLayout.editText?.text.toString())
    }

    private fun onCreateInsertionResult(createInsertionResult: Result<Error, Insertion>) {
        createInsertionResult
            .onSuccess { insertion ->
                setResult(Activity.RESULT_OK, Intent().putExtra(INSERTION_TAG, Gson().toJson(insertion)))
                finish()
            }
            .onError { error ->
                launch { ErrorAlert(this@CreateInsertionActivity).setTitle("Create Error").setMessage(Gson().toJson(error)).show() }
            }
            .onStateLoading { binding.progressIndicator.visibility = View.VISIBLE }
            .onStateLoaded { binding.progressIndicator.visibility = View.INVISIBLE }
    }
}