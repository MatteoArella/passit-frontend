package com.github.passit.core.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlin.coroutines.CoroutineContext

abstract class UseCase<in Params: Any, out E: Error, out R: Any> : CoroutineScope {

    private val parentJob = SupervisorJob()
    private val mainDispatcher = Dispatchers.Main
    private val backgroundDispatcher = Dispatchers.IO

    override val coroutineContext: CoroutineContext
        get() = parentJob + mainDispatcher

    protected abstract suspend fun run(params: Params) : Result<E, R>

    operator fun invoke(params: Params): Flow<Result<E, R>> = channelFlow {
        launch {
            withContext(backgroundDispatcher) {
                send(Result.State.Loading())
                send(run(params))
                send(Result.State.Loaded)
            }
        }
    }
}
