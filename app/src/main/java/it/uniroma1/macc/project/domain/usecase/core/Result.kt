package it.uniroma1.macc.project.domain.usecase.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

suspend inline fun <E, S> Flow<Result<E, S>>.onSuccess(crossinline block: suspend S.(S) -> Unit): Flow<Result<E, S>> {
    onEach {
        if (it is Result.Success) {
            block(it.successData, it.successData)
        }
    }
    return this
}

suspend inline fun <E, S> Flow<Result<E, S>>.onStateLoading(crossinline block: suspend Result.State.Loading.(Result.State.Loading) -> Unit): Flow<Result<E, S>> {
    onEach {
        if (it is Result.State.Loading) {
            block(it, it)
        }
    }
    return this
}