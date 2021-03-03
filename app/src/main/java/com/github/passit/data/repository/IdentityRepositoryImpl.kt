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
import java.net.URL
import kotlin.jvm.Throws

class IdentityRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource,
    private val identityRemoteDataSource: IdentityRemoteDataSource
) : IdentityRepository {
    override val currentUser = MutableLiveData<User?>(null)

    override suspend fun fetchUserAttributes(): User {
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        val user = UserRemoteToEntityMapper.map(userRemoteData)
        currentUser.postValue(user)
        return user
    }

    override suspend fun fetchAuthSession(): AuthSession {
        val authSession = identityRemoteDataSource.fetchAuthSession()
        return AuthSessionRemoteToEntityMapper.map(authSession)
    }

    @Throws(SignInError::class)
    override suspend fun signIn(@NonNull email: String, @NonNull password: String): AuthSignIn {
        val authSignInRemoteData = identityRemoteDataSource.signIn(email, password)
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        userLocalDataSource.createUser(UserRemoteToLocalMapper.map(userRemoteData))
        return AuthSignInRemoteToEntityMapper.map(authSignInRemoteData)
    }

    override suspend fun signInWithGoogle(@NonNull context: Activity): AuthSignIn {
        val authSignInRemoteData = identityRemoteDataSource.signInWithGoogle(context)
        val userRemoteData = identityRemoteDataSource.fetchUserAttributes()
        userLocalDataSource.createUser(UserRemoteToLocalMapper.map(userRemoteData))
        return AuthSignInRemoteToEntityMapper.map(authSignInRemoteData)
    }

    override suspend fun handleFederatedSignInResponse(@NonNull data: Intent) {
        identityRemoteDataSource.handleFederatedSignInResponse(data)
    }

    override suspend fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): AuthSignUp {
        val authSignUpRemoteData = identityRemoteDataSource.signUp(email, password, attributes)
        return AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData)
    }

    override suspend fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): AuthSignUp {
        val authSignUpRemoteData = identityRemoteDataSource.confirmSignUp(email, confirmationCode)
        return AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData)
    }

    override suspend fun resendConfirmationCode(@NonNull email: String): AuthSignUp {
        val authSignUpRemoteData = identityRemoteDataSource.resendConfirmationCode(email)
        return AuthSignUpRemoteToEntityMapper.map(authSignUpRemoteData)
    }

    override suspend fun resetPassword(@NonNull email: String): AuthResetPassword {
        val authResetPasswordRemoteData = identityRemoteDataSource.resetPassword(email)
        return AuthResetPasswordRemoteToEntityMapper.map(authResetPasswordRemoteData)
    }

    override suspend fun confirmResetPassword(newPassword: String, confirmationCode: String) {
        identityRemoteDataSource.confirmResetPassword(newPassword, confirmationCode)
    }

    override suspend fun updateUserAttribute(attribute: UserAttribute, value: String) {
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

    override suspend fun signOut() {
        // TODO: clean db?
        identityRemoteDataSource.signOut()
    }
}

