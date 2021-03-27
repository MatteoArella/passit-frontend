package com.github.passit.ui.models.insertions

import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.usecase.insertions.CreateInsertion
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class InsertionViewModel @ViewModelInject constructor(
        private val createInsertion: CreateInsertion
) : ViewModel() {
    private val _insertion: MutableLiveData<InsertionView> = MutableLiveData()
    val insertion: LiveData<InsertionView> = _insertion

    fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String,
                        @NonNull city: String, @NonNull state: String, @NonNull country: String): Flow<Insertion>
        = createInsertion(CreateInsertion.Params(title, description, subject, city, state, country)).onEach {
            _insertion.postValue(InsertionEntityToUIMapper.map(it))
        }
}