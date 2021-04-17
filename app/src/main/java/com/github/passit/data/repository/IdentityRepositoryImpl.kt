package com.github.passit.data.repository

import androidx.annotation.NonNull
import com.github.passit.data.datasource.local.UserLocalDataSource
import com.github.passit.data.datasource.remote.IdentityRemoteDataSource
import com.github.passit.data.repository.mapper.*
import com.github.passit.domain.model.auth.*
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.*
import java.net.URL
import javax.inject.Inject

class IdentityRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val identityRemoteDataSource: IdentityRemoteDataSource
) : IdentityRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser get() = _currentUser.asStateFlow()

    override fun fetchUserAttributes(): Flow<User> = flow {
        // if currentUser exists return it immediately
        _currentUser.value?.let {
            emit(it)
            return@flow
        }
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        userLocalDataSource.createUser(UserRemoteToLocalMapper.map(userRemoteData))
        val user = UserRemoteToEntityMapper.map(userRemoteData)
        _currentUser.value = user
        emit(user)
    }

    override fun fetchAuthSession(): Flow<AuthSession> = flow {
        val authSession = identityRemoteDataSource.fetchAuthSession()
        emit(AuthSessionRemoteToEntityMapper.map(authSession))
    }

    @Throws(SignInError::class)
    override fun signIn(@NonNull email: String, @NonNull password: String): Flow<AuthSignIn> = flow {
        val authSignInRemoteData = identityRemoteDataSource.signIn(email, password)
        emit(AuthSignInRemoteToEntityMapper.map(authSignInRemoteData))
    }

    override fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): Flow<AuthSignUp> = flow {
        val authSignUpRemoteData = identityRemoteDataSource.signUp(email, password, attributes)
        emit(AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData))
    }

    override fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): Flow<AuthSignUp> = flow {
        val authSignUpRemoteData = identityRemoteDataSource.confirmSignUp(email, confirmationCode)
        emit(AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData))
    }

    override fun resendConfirmationCode(@NonNull email: String): Flow<AuthSignUp> = flow {
        val authSignUpRemoteData = identityRemoteDataSource.resendConfirmationCode(email)
        emit(AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData))
    }

    override fun resetPassword(@NonNull email: String): Flow<AuthResetPassword> = flow {
        val authResetPasswordRemoteData = identityRemoteDataSource.resetPassword(email)
        emit(AuthResetPasswordRemoteToEntityMapper.map(authResetPasswordRemoteData))
    }

    override fun confirmResetPassword(newPassword: String, confirmationCode: String): Flow<Unit> = flow {
        identityRemoteDataSource.confirmResetPassword(newPassword, confirmationCode)
        emit(Unit)
    }

    override fun updateUserAttribute(attribute: UserAttribute, value: String): Flow<Unit> = flow {
        identityRemoteDataSource.updateUserAttribute(attribute, value)
        currentUser.value?.let { user ->
            // make a copy for stateflow conflation
            user.copy().also {
                when (attribute) {
                    UserAttribute.EMAIL -> it.email = value
                    UserAttribute.FAMILY_NAME -> it.familyName = value
                    UserAttribute.GIVEN_NAME -> it.givenName = value
                    UserAttribute.PHONE -> it.phoneNumber = value
                    UserAttribute.PICTURE -> it.picture = runCatching { URL(value) }.getOrNull()
                }
                _currentUser.value = it
            }
        }
        emit(Unit)
    }

    override fun signOut(): Flow<Unit> = flow {
        identityRemoteDataSource.signOut()
        userLocalDataSource.cleanUsers()
        _currentUser.value = null
        emit(Unit)
    }
}

