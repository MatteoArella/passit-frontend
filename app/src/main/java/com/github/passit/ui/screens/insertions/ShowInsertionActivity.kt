package com.github.passit.ui.screens.insertions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.passit.R
import com.github.passit.databinding.ActivityInsertionsShowBinding
import com.github.passit.ui.models.insertions.GetInsertionViewModel
import com.github.passit.ui.models.insertions.InsertionView
import com.github.passit.ui.models.insertions.InsertionViewModel
import com.github.passit.ui.validators.setValidator
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

@AndroidEntryPoint
class ShowInsertionActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    companion object {
        const val INSERTION_TAG = "insertionId"
    }
    private val getInsertionViewModel: GetInsertionViewModel by viewModels()
    private lateinit var binding: ActivityInsertionsShowBinding

    private fun updateViewWithInsertionView(binding: ActivityInsertionsShowBinding, insertion: InsertionView) {
        binding.insertionTitle.text = insertion.title
        binding.insertionLocation.text = getString(R.string.show_insertion_location_format, insertion.location?.city ?: "", insertion.location?.state ?: "", insertion.location?.country ?: "")
        binding.insertionDate.text = insertion.createdAt.toString()
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

        launch {
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
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}