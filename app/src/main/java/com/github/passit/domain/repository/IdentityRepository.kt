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
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

interface IdentityRepository {
    val currentUser: LiveData<User?>

    fun fetchUserAttributes(): Flow<User>

    fun fetchAuthSession(): Flow<AuthSession>

    @Throws(SignInError::class)
    fun signIn(@NonNull email: String, @NonNull password: String): Flow<AuthSignIn>

    fun signInWithGoogle(@NonNull context: Activity): Flow<AuthSignIn>

    fun handleFederatedSignInResponse(@NonNull data: Intent): Flow<Unit>

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