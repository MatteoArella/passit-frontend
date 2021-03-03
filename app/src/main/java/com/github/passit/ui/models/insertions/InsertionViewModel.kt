package com.github.passit.ui.models.insertions

import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.usecase.insertions.CreateInsertion
import kotlinx.coroutines.flow.Flow
import com.github.passit.core.domain.Result
import com.github.passit.core.domain.onSuccess
import com.github.passit.ui.mapper.InsertionEntityToUIMapper

class InsertionViewModel @ViewModelInject constructor(
        private val createInsertion: CreateInsertion
) : ViewModel() {
    private val _insertion: MutableLiveData<InsertionView> = MutableLiveData()
    val insertion: LiveData<InsertionView> = _insertion

    fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String): Flow<Result<Error, Insertion>>
        = createInsertion(CreateInsertion.Params(title, description, subject)).onSuccess {
            _insertion.postValue(InsertionEntityToUIMapper.map(this))
        }
}