package com.github.passit.ui.models.auth

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.passit.core.domain.EncryptedData
import com.github.passit.domain.model.auth.*
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.auth.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream
import java.net.URL

class AuthViewModel @ViewModelInject constructor(
    identityRepository: IdentityRepository,
    private val signIn: SignIn,
    private val signInWithGoogle: SignInWithGoogle,
    private val handleFederatedSignInResponse: HandleFederatedSignInResponse,
    private val signUp: SignUp,
    private val confirmSignUp: ConfirmSignUp,
    private val resendConfirmationCode: ResendConfirmationCode,
    private val fetchUserAttributes: FetchUserAttributes,
    private val fetchAuthSession: FetchAuthSession,
    private val resetPassword: ResetPassword,
    private val confirmResetPassword: ConfirmResetPassword,
    private val changeUserPicture: ChangeUserPicture,
    private val signOut: SignOut
) : ViewModel() {

    val currentUser: StateFlow<User?> = identityRepository.currentUser

    val email = MutableLiveData<String?>()
    val password = MutableLiveData<EncryptedData?>()

    fun fetchUserAttributes(): Flow<User> =
            fetchUserAttributes(FetchUserAttributes.Params())

    fun fetchAuthSession(): Flow<AuthSession> =
            fetchAuthSession(FetchAuthSession.Params())

    fun signIn(@NonNull email: String, @NonNull password: String): Flow<AuthSignIn> =
            signIn(SignIn.Params(email, password))

    fun signInWithGoogle(@NonNull context: Activity): Flow<AuthSignIn> =
            signInWithGoogle(SignInWithGoogle.Params(context))

    fun handleFederatedSignInResponse(@NonNull data: Intent): Flow<Unit> =
            handleFederatedSignInResponse(HandleFederatedSignInResponse.Params(data))

    fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): Flow<AuthSignUp> =
            signUp(SignUp.Params(email, password, attributes))

    fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): Flow<AuthSignUp> =
            confirmSignUp(ConfirmSignUp.Params(email, confirmationCode))

    fun resendConfirmationCode(@NonNull email: String): Flow<AuthSignUp> =
            resendConfirmationCode(ResendConfirmationCode.Params(email))

    fun resetPassword(@NonNull email: String): Flow<AuthResetPassword> =
            resetPassword(ResetPassword.Params(email))

    fun confirmResetPassword(@NonNull newPassword: String, @NonNull confirmationCode: String): Flow<Unit> =
            confirmResetPassword(ConfirmResetPassword.Params(newPassword, confirmationCode))

    fun changeUserPicture(@NonNull picture: InputStream): Flow<URL> =
            changeUserPicture(ChangeUserPicture.Params(picture))

    fun signOut(): Flow<Unit> = signOut(SignOut.Params())
}