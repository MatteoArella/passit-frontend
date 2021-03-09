package com.github.passit.data.repository

import android.app.Activity
import android.content.Intent
import javax.inject.Inject
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.github.passit.data.datasource.local.UserLocalDataSource
import com.github.passit.data.datasource.remote.IdentityRemoteDataSource
import com.github.passit.data.repository.mapper.*
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.domain.model.auth.User
import com.github.passit.domain.model.auth.UserAttribute
import com.github.passit.domain.model.auth.AuthResetPassword
import com.github.passit.domain.model.auth.AuthSession
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URL
import kotlin.jvm.Throws

class IdentityRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val identityRemoteDataSource: IdentityRemoteDataSource
) : IdentityRepository {
    override val currentUser = MutableLiveData<User?>(null)

    override fun fetchUserAttributes(): Flow<User> = flow {
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        val user = UserRemoteToEntityMapper.map(userRemoteData)
        currentUser.postValue(user)
        emit(user)
    }

    override fun fetchAuthSession(): Flow<AuthSession> = flow {
        val authSession = identityRemoteDataSource.fetchAuthSession()
        emit(AuthSessionRemoteToEntityMapper.map(authSession))
    }

    @Throws(SignInError::class)
    override fun signIn(@NonNull email: String, @NonNull password: String): Flow<AuthSignIn> = flow {
        val authSignInRemoteData = identityRemoteDataSource.signIn(email, password)
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        userLocalDataSource.createUser(UserRemoteToLocalMapper.map(userRemoteData))
        emit(AuthSignInRemoteToEntityMapper.map(authSignInRemoteData))
    }

    override fun signInWithGoogle(@NonNull context: Activity): Flow<AuthSignIn> = flow {
        val authSignInRemoteData = identityRemoteDataSource.signInWithGoogle(context)
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        userLocalDataSource.createUser(UserRemoteToLocalMapper.map(userRemoteData))
        emit(AuthSignInRemoteToEntityMapper.map(authSignInRemoteData))
    }

    override fun handleFederatedSignInResponse(@NonNull data: Intent): Flow<Unit> = flow {
        identityRemoteDataSource.handleFederatedSignInResponse(data)
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
    }

    override fun updateUserAttribute(attribute: UserAttribute, value: String): Flow<Unit> = flow {
        identityRemoteDataSource.updateUserAttribute(attribute, value)
        currentUser.value?.let { user ->
            when (attribute) {
                UserAttribute.EMAIL -> user.email = value
                UserAttribute.FAMILY_NAME -> user.familyName = value
                UserAttribute.GIVEN_NAME -> user.givenName = value
                UserAttribute.PHONE -> user.phoneNumber = value
                UserAttribute.PICTURE -> user.picture = runCatching { URL(value) }.getOrNull()
            }
            currentUser.postValue(user)
        }
    }

    override fun signOut(): Flow<Unit> = flow {
        // TODO: clean db?
        identityRemoteDataSource.signOut()
    }
}

