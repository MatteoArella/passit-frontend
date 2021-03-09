package com.github.passit.ui.models.insertions

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.passit.domain.usecase.insertions.GetInsertions
import com.github.passit.ui.mapper.InsertionEntityToUIMapper
import kotlinx.coroutines.flow.*

class GetInsertionsViewModel @ViewModelInject constructor(
        private val getInsertions: GetInsertions
) : ViewModel() {
    private var insertions: Flow<PagingData<InsertionView>>? = null

    var searchView: InsertionSearchView? = null

    fun getInsertions(subject: String, city: String, state: String, country: String): Flow<PagingData<InsertionView>> {
        if (insertions != null && subject == searchView?.subject && city == searchView?.city && state == searchView?.state && country == searchView?.country) {
            return insertions!!
        }
        this.searchView = InsertionSearchView(subject = subject, city = city, state = state, country = country)
        val newResult = getInsertions(GetInsertions.Params(subject, city, state, country))
                .map { page -> page.map { insertion -> InsertionEntityToUIMapper.map(insertion) } }
                .cachedIn(viewModelScope)
        insertions = newResult
        return newResult
    }
}