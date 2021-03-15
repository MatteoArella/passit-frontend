package com.github.passit.domain.repository

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.domain.model.auth.User
import com.github.passit.domain.model.auth.UserAttribute
import com.github.passit.domain.model.auth.AuthResetPassword
import com.github.passit.domain.model.auth.AuthSession
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlin.jvm.Throws

interface IdentityRepository {
    val currentUser: LiveData<User?>

    suspend fun fetchUserAttributes(): User

    suspend fun fetchAuthSession(): AuthSession

    @Throws(SignInError::class)
    suspend fun signIn(@NonNull email: String, @NonNull password: String): AuthSignIn

    suspend fun signInWithGoogle(@NonNull context: Activity): AuthSignIn

    suspend fun handleFederatedSignInResponse(@NonNull data: Intent)

    suspend fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): AuthSignUp

    suspend fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): AuthSignUp

    suspend fun resendConfirmationCode(@NonNull email: String): AuthSignUp

    suspend fun resetPassword(@NonNull email: String): AuthResetPassword

    suspend fun confirmResetPassword(@NonNull newPassword: String, @NonNull confirmationCode: String)

    suspend fun updateUserAttribute(@NonNull attribute: UserAttribute, @NonNull value: String)

    suspend fun signOut()
}