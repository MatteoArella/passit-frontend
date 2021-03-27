package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubscribeNewConversations @Inject constructor(
        private val identityRepository: IdentityRepository,
        private val conversationRepository: ConversationRepository
) : UseCase<SubscribeNewConversations.Params, Conversation>() {

    override fun run(params: Params): Flow<Conversation> = flow {
        identityRepository.fetchUserAttributes().collect { user ->
            emitAll(conversationRepository.subscribeConversations(user.id))
        }
    }

    class Params
}