package com.github.passit.ui.models.insertions

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.passit.domain.usecase.insertions.GetUserInsertions
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GetUserInsertionsViewModel @ViewModelInject constructor(
        private val getUserInsertions: GetUserInsertions
) : ViewModel() {
    private var insertions: Flow<PagingData<InsertionView>>? = null

    var searchView: UserInsertionSearchView? = null

    fun getUserInsertions(tutorId: String): Flow<PagingData<InsertionView>> {
        if (insertions != null && tutorId == searchView?.tutor) {
            return insertions!!
        }
        this.searchView = UserInsertionSearchView(tutor = tutorId)
        val newResult = getUserInsertions(GetUserInsertions.Params(tutorId))
                .map { page ->
                    page.map {
                        insertion -> InsertionEntityToUIMapper.map(insertion)
                        //.apply {
                            //Log.i("Insertion in ViewModel", "$this")
                        // }
                    }
                }
                .cachedIn(viewModelScope)

        insertions = newResult
        return newResult
    }
}