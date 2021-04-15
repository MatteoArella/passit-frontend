package com.github.passit.ui.models.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.github.passit.core.extension.isSameDay
import com.github.passit.domain.usecase.conversations.GetMessages
import com.github.passit.ui.mapper.MessageEntityToUIMapper
import kotlinx.coroutines.flow.*
import java.util.*

class GetMessagesViewModel @ViewModelInject constructor(
        private val getMessages: GetMessages
) : ViewModel() {
    /*private val _mess = MutableStateFlow<PagingData<MessageView>?>(null)
    val mess: StateFlow<PagingData<MessageView>?> get() = _mess*/
    private var mess: Flow<PagingData<MessageView>>? = null

    fun getMessages(conversationId: String): Flow<PagingData<MessageView>> {
        //if (mess != null) return mess!!
        return getMessages(GetMessages.Params(conversationId))
                .map { page -> page.map { MessageEntityToUIMapper.map(it) } }
                .map {
                    it.insertSeparators { before, after ->
                        if (before == null) {
                            return@insertSeparators null
                        }
                        if (after == null) {
                            // we're at the beginning of the list (reverse layout)
                            return@insertSeparators before.message.createdAt?.let { date -> MessageView.SeparatorItem(date) }
                        }

                        if (!before.message.createdAt.isSameDay(after.message.createdAt)) {
                            MessageView.SeparatorItem(before.message.createdAt!!)
                        } else {
                            null
                        }
                    }
                }/*
                .onEach {
                    _mess.value = it
                }
                */.cachedIn(viewModelScope)
        //return mess!!
    }
}
