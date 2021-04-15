package com.github.passit.ui.models.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.passit.domain.usecase.conversations.CreateMessage
import com.github.passit.ui.mapper.MessageEntityToUIMapper
import kotlinx.coroutines.flow.*

class SendMessageViewModel @ViewModelInject constructor(
        private val createMessage: CreateMessage
) : ViewModel() {
    private val _mess = MutableStateFlow<MessageView?>(null)
    val mess: StateFlow<MessageView?> get() = _mess

    fun createMessage(to: String, conversationId: String, content: String): Flow<MessageView> {
        return createMessage(CreateMessage.Params(to, conversationId, content))
                .map { MessageEntityToUIMapper.map(it) }
                .onEach {
                    _mess.value = it
                }
    }
}
