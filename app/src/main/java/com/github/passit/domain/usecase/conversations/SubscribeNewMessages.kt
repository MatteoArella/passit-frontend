package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Message
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubscribeNewMessages @Inject constructor(
        private val identityRepository: IdentityRepository,
        private val conversationRepository: ConversationRepository
) : UseCase<SubscribeNewMessages.Params, Message.ReceivedMessage>() {

    override fun run(params: Params): Flow<Message.ReceivedMessage> = flow {
        identityRepository.fetchUserAttributes().collect { user ->
            emitAll(conversationRepository.subscribeMessages(userId = user.id))
        }
    }

    class Params
}