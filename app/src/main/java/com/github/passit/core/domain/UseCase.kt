package com.github.passit.core.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class UseCase<in Params: Any, out R: Any> {

    protected abstract fun run(params: Params) : Flow<R>

    operator fun invoke(params: Params): Flow<R> = run(params).flowOn(Dispatchers.IO)
}
