package com.github.passit.domain.repository

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import com.github.passit.data.repository.auth.*
import com.github.passit.domain.model.SignUpUserAttributes
import com.github.passit.domain.model.User
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlin.jvm.Throws

interface IdentityRepository {
    val currentUser: LiveData<User?>

    suspend fun fetchUserAttributes(): User

    suspend fun fetchAuthSession(): AuthSessionResult

    @Throws(SignInError::class)
    suspend fun signIn(@NonNull email: String, @NonNull password: String): AuthSignInResult

    suspend fun signInWithGoogle(@NonNull context: Activity): AuthSignInResult

    suspend fun handleFederatedSignInResponse(@NonNull data: Intent)

    suspend fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): AuthSignUpResult

    suspend fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): AuthSignUpResult

    suspend fun resendConfirmationCode(@NonNull email: String): AuthSignUpResult

    suspend fun resetPassword(@NonNull email: String): AuthResetPasswordResult

    suspend fun confirmResetPassword(@NonNull newPassword: String, @NonNull confirmationCode: String)

    suspend fun updateUserAttribute(@NonNull attribute: UserAttribute, @NonNull value: String)

    suspend fun signOut()
}