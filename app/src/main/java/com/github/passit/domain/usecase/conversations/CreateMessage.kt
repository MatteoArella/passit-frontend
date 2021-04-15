package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Message
import com.github.passit.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CreateMessage @Inject constructor(
        private val conversationRepository: ConversationRepository
) : UseCase<CreateMessage.Params, Message>() {

    override fun run(params: Params): Flow<Message> =
            conversationRepository.createMessage(params.to, params.conversationId, params.content).map {
                Message.SentMessage(it)
            }

    data class Params(val to: String, val conversationId: String, val content: String)
}