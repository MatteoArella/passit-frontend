package com.github.passit.domain.usecase.exception.auth

sealed class SignInError(val error: Exception? = null) : Error(error) {
    class WrongCredentials(error: Exception) : SignInError(error)
    class Unconfirmed(error: Exception) : SignInError(error)
}