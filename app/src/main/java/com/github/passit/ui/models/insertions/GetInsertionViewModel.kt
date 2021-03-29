package com.github.passit.ui.models.insertions

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.passit.domain.usecase.insertions.GetInsertion
import com.github.passit.domain.usecase.insertions.GetInsertions
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.*

class GetInsertionViewModel @ViewModelInject constructor(
        private val getInsertion: GetInsertion
) : ViewModel() {
    private val _insertion = MutableStateFlow<InsertionView?>(null)
    val insertion: StateFlow<InsertionView?> = _insertion

    fun getInsertion(insertionId: String): Flow<InsertionView> {
        return getInsertion(GetInsertion.Params(insertionId))
                .map { ins ->
                    InsertionEntityToUIMapper.map(ins).also { _insertion.value = it }
                }
    }
}