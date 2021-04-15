package com.github.passit.ui.models.insertions

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.passit.domain.usecase.insertions.GetInsertion
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

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