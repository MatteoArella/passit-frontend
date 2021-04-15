package com.github.passit.ui.models.conversations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.passit.domain.usecase.conversations.GetUnreadMessagesCount
import com.github.passit.domain.usecase.conversations.ResetUnreadMessagesCount
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class UnreadMessagesViewModel @ViewModelInject constructor(
    private val getUnreadMessagesCount: GetUnreadMessagesCount,
    private val resetUnreadMessagesCount: ResetUnreadMessagesCount
) : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> get() = _count

    private fun getUnreadMessagesCount(): Flow<Int> =
        getUnreadMessagesCount(GetUnreadMessagesCount.Params()).onEach { _count.value = it }

    private fun getUnreadMessagesCount(conversationId: String): Flow<Int> =
            getUnreadMessagesCount(GetUnreadMessagesCount.Params(conversationId)).onEach {
                _count.value += it
            }

    fun resetUnreadMessagesCount(conversationId: String): Flow<Unit> = flow {
        getUnreadMessagesCount(GetUnreadMessagesCount.Params(conversationId)).collect { count ->
            resetUnreadMessagesCount(ResetUnreadMessagesCount.Params(conversationId)).onEach {
                _count.value -= count
            }.collect {
                emit(Unit)
            }
        }
    }

    init {
        viewModelScope.launch {
            getUnreadMessagesCount().collect()
        }
    }
}
