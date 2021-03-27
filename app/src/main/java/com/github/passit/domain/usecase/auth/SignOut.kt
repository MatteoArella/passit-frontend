package com.github.passit.domain.usecase.auth

import com.github.passit.core.domain.UseCase
import com.github.passit.domain.repository.ConversationRepository
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.repository.InsertionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignOut @Inject constructor(
        private val identityRepository: IdentityRepository,
        private val insertionRepository: InsertionRepository,
        private val conversationRepository: ConversationRepository
) : UseCase<SignOut.Params, Unit>() {

    override fun run(params: Params): Flow<Unit> = flow {
        identityRepository.signOut().collect {
            insertionRepository.deleteInsertions().collect()
            conversationRepository.deleteConversations().collect()
            emit(Unit)
        }
    }

    class Params
}