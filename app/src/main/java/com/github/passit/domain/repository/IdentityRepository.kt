package com.github.passit.domain.repository

import androidx.annotation.NonNull
import com.github.passit.domain.model.auth.*
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IdentityRepository {
    val currentUser: StateFlow<User?>

    fun fetchUserAttributes(): Flow<User>

    fun fetchAuthSession(): Flow<AuthSession>

    @Throws(SignInError::class)
    fun signIn(@NonNull email: String, @NonNull password: String): Flow<AuthSignIn>

    fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): Flow<AuthSignUp>

    fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): Flow<AuthSignUp>

    fun resendConfirmationCode(@NonNull email: String): Flow<AuthSignUp>

    fun resetPassword(@NonNull email: String): Flow<AuthResetPassword>

    fun confirmResetPassword(@NonNull newPassword: String, @NonNull confirmationCode: String): Flow<Unit>

    fun updateUserAttribute(@NonNull attribute: UserAttribute, @NonNull value: String): Flow<Unit>

    fun signOut(): Flow<Unit>
}