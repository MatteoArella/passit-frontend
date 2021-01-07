package it.uniroma1.macc.project.domain.usecase.exception.auth

import java.lang.Exception

sealed class SignInError(val error: Exception? = null) : Error(error) {
    class WrongCredentials(error: Exception) : SignInError(error)
    class Unconfirmed(error: Exception) : SignInError(error)
}