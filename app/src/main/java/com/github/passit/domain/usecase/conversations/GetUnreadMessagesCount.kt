package com.github.passit.domain.usecase.conversations

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnreadMessagesCount @Inject constructor(
        private val conversationRepository: ConversationRepository
) : UseCase<GetUnreadMessagesCount.Params, Int>() {

    override fun run(params: Params): Flow<Int> =
            conversationRepository.getUnreadMessagesCount(params.conversationId)

    data class Params(val conversationId: String? = null)
}