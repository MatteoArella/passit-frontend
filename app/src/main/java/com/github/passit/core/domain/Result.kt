package com.github.passit.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

sealed class Result<out E, out S> {
    class Success<out S>(val successData: S) : Result<Nothing, S>()
    class Error<out E>(val error: E) : Result<E, Nothing>()

    sealed class State : Result<Nothing, Nothing>() {
        class Loading(val value: Double = 0.0) : State()
        object Loaded : State()
    }

    fun onSuccess(block: S.(S) -> Unit): Result<E, S> {
        if (this is Success) {
            block(this.successData, this.successData)
        }
        return this
    }

    fun onError(block: E.(E) -> Unit): Result<E, S> {
        if (this is Error) {
            block(this.error, this.error)
        }
        return this
    }

    fun onStateLoading(block: State.Loading.(State.Loading) -> Unit): Result<E, S> {
        if (this is State.Loading) {
            block(this, this)
        }
        return this
    }

    fun onStateLoaded(block: State.Loaded.(State.Loaded) -> Unit): Result<E, S> {
        if (this is State.Loaded) {
            block(this, this)
        }
        return this
    }
}

fun <E, S> Flow<Result<E, S>>.onSuccess(block: S.(S) -> Unit): Flow<Result<E, S>> {
    onEach { it.onSuccess(block) }
    return this
}
