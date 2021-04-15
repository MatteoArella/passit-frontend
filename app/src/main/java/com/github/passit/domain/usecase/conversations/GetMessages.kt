package com.github.passit.domain.usecase.conversations

import androidx.paging.PagingData
import androidx.paging.map
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Message
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetMessages @Inject constructor(
        private val identityRepository: IdentityRepository,
        private val conversationRepository: ConversationRepository
) : UseCase<GetMessages.Params, PagingData<Message>>() {

    override fun run(params: Params): Flow<PagingData<Message>> = flow {
        identityRepository.fetchUserAttributes().collect { user ->
            emitAll(conversationRepository.getMessages(params.conversationId).map { page ->
                page.map { message ->
                    if (message.author == null) {
                        Message.SentMessage(message)
                    }
                    else if (user.id == message.author!!.id) {
                        Message.SentMessage(message)
                    } else {
                        Message.ReceivedMessage(message)
                    }
                }
            })
        }
    }


    data class Params(val conversationId: String)
}