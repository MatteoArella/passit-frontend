package com.github.passit.ui.screens.insertions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.R
import com.github.passit.databinding.ActivityInsertionsCreateBinding
import com.github.passit.ui.models.insertions.InsertionViewModel
import com.github.passit.ui.validators.setValidator
import com.github.passit.ui.view.ErrorAlert
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateInsertionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val INSERTION_TAG = "insertion"
        const val INSERTION_TITLE_TAG = "title"
        const val INSERTION_DESCRIPTION_TAG = "description"
        const val INSERTION_SUBJECT_TAG = "subject"
        const val INSERTION_LOCATION_CITY_TAG = "city"
        const val INSERTION_LOCATION_STATE_TAG = "state"
        const val INSERTION_LOCATION_COUNTRY_TAG = "country"
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
        binding.cityTextLayout.setValidator(getString(R.string.create_insertion_city_empty_error)) { it.isNotEmpty() }
        binding.stateTextLayout.setValidator(getString(R.string.create_insertion_state_empty_error)) { it.isNotEmpty() }
        binding.countryTextLayout.setValidator(getString(R.string.create_insertion_country_empty_error)) { it.isNotEmpty() }

        savedInstanceState?.let { savedState ->
            binding.titleTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_TITLE_TAG))
            binding.descriptionTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_DESCRIPTION_TAG))
            binding.subjectTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(INSERTION_SUBJECT_TAG))
            binding.cityTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(
                INSERTION_LOCATION_CITY_TAG))
            binding.stateTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(
                INSERTION_LOCATION_STATE_TAG))
            binding.countryTextLayout.editText?.text = SpannableStringBuilder(savedState.getString(
                INSERTION_LOCATION_COUNTRY_TAG))
        }

        binding.createInsertionBtn.setOnClickListener {
            if (!binding.titleTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.descriptionTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.subjectTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.cityTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.countryTextLayout.editText?.text.isNullOrEmpty() &&
                !binding.stateTextLayout.editText?.text.isNullOrEmpty()) {
                launch {
                    insertionModel.createInsertion(
                        title = binding.titleTextLayout.editText?.text.toString(),
                        description = binding.descriptionTextLayout.editText?.text.toString(),
                        subject = binding.subjectTextLayout.editText?.text.toString(),
                        city = binding.cityTextLayout.editText?.text.toString(),
                        state = binding.stateTextLayout.editText?.text.toString(),
                        country = binding.countryTextLayout.editText?.text.toString()
                    ).onStart {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }.onCompletion {
                        binding.progressIndicator.visibility = View.INVISIBLE
                    }.catch { error ->
                        launch { ErrorAlert(this@CreateInsertionActivity).setTitle("Create Error").setMessage(Gson().toJson(error)).show() }
                    }.collect { insertion ->
                        setResult(Activity.RESULT_OK, Intent().putExtra(INSERTION_TAG, Gson().toJson(insertion)))
                        finish()
                    }
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
        outState.putString(INSERTION_LOCATION_CITY_TAG, binding.cityTextLayout.editText?.text.toString())
        outState.putString(INSERTION_LOCATION_STATE_TAG, binding.stateTextLayout.editText?.text.toString())
        outState.putString(INSERTION_LOCATION_COUNTRY_TAG, binding.countryTextLayout.editText?.text.toString())
    }
}