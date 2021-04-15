package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.exception.conversation.CreateConversationError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateConversation @Inject constructor(
        private val identityRepository: IdentityRepository,
        private val conversationRepository: ConversationRepository
) : UseCase<CreateConversation.Params, Conversation>() {

    override fun run(params: Params): Flow<Conversation> = flow {
        identityRepository.fetchUserAttributes().collect { user ->
            if (user.id != params.tutorId) {
                emitAll(conversationRepository.createConversation(user.id, params.tutorId))
            } else {
                throw CreateConversationError.SameUser()
            }
        }
    }

    data class Params(val tutorId: String)
}