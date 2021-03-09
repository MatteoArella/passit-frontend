package com.github.passit.core.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class UseCase<in Params: Any, out R: Any> {

    protected abstract fun run(params: Params) : Flow<R>

    operator fun invoke(params: Params): Flow<R> = run(params).flowOn(Dispatchers.IO)
}
