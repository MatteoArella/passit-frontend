package com.github.passit.ui.models.auth

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.github.passit.domain.model.auth.AuthSession
import com.github.passit.domain.model.auth.AuthResetPassword
import com.github.passit.domain.model.auth.AuthSignIn
import com.github.passit.domain.model.auth.AuthSignUp
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.domain.model.auth.User
import com.github.passit.domain.repository.IdentityRepository
import com.github.passit.domain.usecase.auth.*
import com.github.passit.core.domain.EncryptedData
import kotlinx.coroutines.flow.*
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

    val currentUser: LiveData<User?> = identityRepository.currentUser

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