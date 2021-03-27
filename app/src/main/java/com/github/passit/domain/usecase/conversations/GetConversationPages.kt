package com.github.passit.domain.usecase.conversations

import androidx.paging.PagingData
import com.github.passit.core.domain.UseCase
import com.github.passit.domain.model.Conversation
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetConversationPages @Inject constructor(
    private val identityRepository: IdentityRepository,
    private val conversationRepository: ConversationRepository
) : UseCase<GetConversationPages.Params, PagingData<Conversation>>() {

    override fun run(params: Params): Flow<PagingData<Conversation>> = flow {
        identityRepository.fetchUserAttributes().collect { user ->
            emitAll(conversationRepository.getConversationPages(user.id))
        }
    }

    class Params
}