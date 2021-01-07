package it.uniroma1.macc.project.domain.repository

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import com.amplifyframework.auth.AuthUserAttribute
import it.uniroma1.macc.project.data.repository.auth.*
import it.uniroma1.macc.project.domain.model.SignUpUserAttributes
import it.uniroma1.macc.project.domain.model.User
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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