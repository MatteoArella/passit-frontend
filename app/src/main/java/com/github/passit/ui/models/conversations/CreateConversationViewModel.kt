package com.github.passit.ui.models.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.passit.domain.usecase.conversations.CreateConversation
import com.github.passit.ui.mapper.ConversationEntityToUIMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CreateConversationViewModel @ViewModelInject constructor(
        private val createConversation: CreateConversation
) : ViewModel() {
    fun createConversation(tutorId: String): Flow<ConversationView> {
        return createConversation(CreateConversation.Params(tutorId))
                .map { ConversationEntityToUIMapper.map(it) }
    }
}
