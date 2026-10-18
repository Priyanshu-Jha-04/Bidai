package com.example.bidai.data.repository

import com.example.bidai.data.model.SignupRequest
import com.example.bidai.data.remote.ApiService
import com.example.bidai.data.remote.AppResult
import com.example.bidai.data.remote.toFailure
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

//auth repository
class AuthRepository(
    private val auth: FirebaseAuth,
    private val api: ApiService,
) {
    val isLoggedIn get() = auth.currentUser != null
    val userName get() = auth.currentUser?.displayName.orEmpty()

    suspend fun login(email: String, password: String): AppResult<Unit> = authCall {
        val user = auth.signInWithEmailAndPassword(email.trim(), password).await().user!!
        try {
            //profile check
            val profile = api.login().user
            if (user.displayName != profile.name) {
                user.updateProfile(userProfileChangeRequest { displayName = profile.name }).await()
            }
        } catch (e: Exception) {
            auth.signOut()
            throw e
        }
    }

    //signup
    suspend fun signup(name: String, email: String, phone: String, password: String): AppResult<Unit> = authCall {
        val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user!!
        try {
            user.updateProfile(userProfileChangeRequest { displayName = name.trim() }).await()
            api.signup(SignupRequest(name.trim(), phone.trim()))
        } catch (e: Exception) {
            //rollback
            runCatching { user.delete().await() }
            auth.signOut()
            throw e
        }
    }

    fun logout() = auth.signOut()

    private suspend fun authCall(block: suspend () -> Unit): AppResult<Unit> = try {
        block()
        AppResult.Success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: FirebaseAuthUserCollisionException) {
        AppResult.Failure("Email already registered", fieldErrors = mapOf("email" to "Email already registered"))
    } catch (e: FirebaseAuthWeakPasswordException) {
        AppResult.Failure("Password is too weak", fieldErrors = mapOf("password" to "Password must be at least 6 characters"))
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        //error mapping
        AppResult.Failure("Incorrect email or password")
    } catch (e: FirebaseAuthInvalidUserException) {
        AppResult.Failure("Incorrect email or password")
    } catch (e: FirebaseTooManyRequestsException) {
        AppResult.Failure("Too many attempts. Please try again later.")
    } catch (e: FirebaseNetworkException) {
        AppResult.Failure("Can't reach Firebase. Check your connection and try again.")
    } catch (e: Exception) {
        e.toFailure()
    }
}
