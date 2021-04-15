package com.github.passit.domain.usecase.exception.conversation

sealed class CreateConversationError(val error: Exception? = null) : Error(error) {
    class SameUser(error: Exception? = null) : CreateConversationError(error)
}