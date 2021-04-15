package com.github.passit.ui.models.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.github.passit.domain.usecase.conversations.GetConversationPages
import com.github.passit.ui.mapper.ConversationEntityToUIMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class GetConversationsViewModel @ViewModelInject constructor(
        private val getConversationPages: GetConversationPages
) : ViewModel() {
    private val _convs = MutableStateFlow<PagingData<ConversationView>?>(null)
    val convs: StateFlow<PagingData<ConversationView>?> get() = _convs

    fun getConversations(): Flow<PagingData<ConversationView>> {
        return getConversationPages(GetConversationPages.Params())
                .map { page -> page.map { ConversationEntityToUIMapper.map(it) }.also { _convs.value = it } }
                .cachedIn(viewModelScope)
    }
}
