package it.uniroma1.macc.project.ui.models.auth

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import it.uniroma1.macc.project.data.repository.auth.AuthSessionResult
import it.uniroma1.macc.project.data.repository.auth.AuthResetPasswordResult
import it.uniroma1.macc.project.data.repository.auth.AuthSignInResult
import it.uniroma1.macc.project.data.repository.auth.AuthSignUpResult
import it.uniroma1.macc.project.domain.model.SignUpUserAttributes
import it.uniroma1.macc.project.domain.model.User
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.auth.*
import it.uniroma1.macc.project.domain.usecase.core.Result
import it.uniroma1.macc.project.domain.usecase.exception.auth.SignInError
import it.uniroma1.macc.project.util.crypto.EncryptedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.URL

class AuthViewModel @ViewModelInject constructor(
    private val identityRepository: IdentityRepository,
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

    fun fetchUserAttributes(): Flow<Result<Error, User>> =
            fetchUserAttributes(FetchUserAttributes.Params())

    fun fetchAuthSession(): Flow<Result<Error, AuthSessionResult>> = fetchAuthSession(
        FetchAuthSession.Params())

    fun signIn(@NonNull email: String, @NonNull password: String): Flow<Result<SignInError, AuthSignInResult>> =
            signIn(SignIn.Params(email, password))

    fun signInWithGoogle(@NonNull context: Activity): Flow<Result<Error, AuthSignInResult>> =
            signInWithGoogle(SignInWithGoogle.Params(context))

    fun handleFederatedSignInResponse(@NonNull data: Intent): Flow<Result<Error, Unit>> =
            handleFederatedSignInResponse(HandleFederatedSignInResponse.Params(data))

    fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): Flow<Result<Error, AuthSignUpResult>> =
            signUp(SignUp.Params(email, password, attributes))

    fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): Flow<Result<Error, AuthSignUpResult>> =
            confirmSignUp(ConfirmSignUp.Params(email, confirmationCode))

    fun resendConfirmationCode(@NonNull email: String): Flow<Result<Error, AuthSignUpResult>> =
            resendConfirmationCode(ResendConfirmationCode.Params(email))

    fun resetPassword(@NonNull email: String): Flow<Result<Error, AuthResetPasswordResult>> =
            resetPassword(ResetPassword.Params(email))

    fun confirmResetPassword(@NonNull newPassword: String, @NonNull confirmationCode: String): Flow<Result<Error, Unit>> =
            confirmResetPassword(ConfirmResetPassword.Params(newPassword, confirmationCode))

    fun changeUserPicture(@NonNull picture: InputStream): Flow<Result<Error, URL>> =
            changeUserPicture(ChangeUserPicture.Params(picture))

    fun signOut(): Flow<Result<Error, Unit>> = signOut(SignOut.Params())
}