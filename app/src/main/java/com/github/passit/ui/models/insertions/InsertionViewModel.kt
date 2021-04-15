package com.github.passit.ui.models.insertions

import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.passit.domain.model.Insertion
import com.github.passit.domain.model.InsertionStatus
import com.github.passit.domain.usecase.insertions.CreateInsertion
import com.github.passit.domain.usecase.insertions.GetInsertion
import com.github.passit.domain.usecase.insertions.UpdateInsertion
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.*

class InsertionViewModel @ViewModelInject constructor(
        private val createInsertion: CreateInsertion,
        private val getInsertion: GetInsertion,
        private val updateInsertion: UpdateInsertion
) : ViewModel() {
    private val _insertion = MutableStateFlow<InsertionView?>(null)
    val insertion: StateFlow<InsertionView?> = _insertion

    fun createInsertion(@NonNull title: String, @NonNull description: String, @NonNull subject: String,
                        @NonNull city: String, @NonNull state: String, @NonNull country: String): Flow<Insertion>
        = createInsertion(CreateInsertion.Params(title, description, subject, city, state, country)).onEach {
            _insertion.value = InsertionEntityToUIMapper.map(it)
        }

    fun getInsertion(insertionId: String): Flow<InsertionView> {
        return getInsertion(GetInsertion.Params(insertionId))
                .map { ins ->
                    InsertionEntityToUIMapper.map(ins).also { _insertion.value = it }
                }
    }

    fun updateInsertion(insertionId: String, status: InsertionStatus? = null, title: String? = null, description: String? = null, subject: String? = null,
                        city: String? = null, state: String? = null, country: String? = null): Flow<InsertionView>
        = updateInsertion(UpdateInsertion.Params(insertionId, status, title, description, subject, city, state, country))
            .map { ins ->
                InsertionEntityToUIMapper.map(ins).also { _insertion.value = it }
            }
}