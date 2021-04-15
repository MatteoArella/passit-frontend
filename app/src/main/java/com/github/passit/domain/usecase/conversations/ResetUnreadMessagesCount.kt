package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetUnreadMessagesCount @Inject constructor(
        private val conversationRepository: ConversationRepository
) : UseCase<ResetUnreadMessagesCount.Params, Unit>() {

    override fun run(params: Params): Flow<Unit> =
            conversationRepository.resetUnreadMessagesCount(params.conversationId)

    data class Params(val conversationId: String)
}