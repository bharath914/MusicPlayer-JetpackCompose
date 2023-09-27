package com.bharath.musicplayerforbaby.presentation.signIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bharath.musicplayerforbaby.MainActivity
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.data.signInData.SignInResult
import com.bharath.musicplayerforbaby.data.signInData.UserData
import com.bharath.musicplayerforbaby.presentation.viewmodel.SignInViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthClient(
    private val context: Context,
    private val oneTapClient: SignInClient,

) {
    private val auth = Firebase.auth

    @Inject
    lateinit var signInViewModel: SignInViewModel

    suspend fun signIn(): IntentSender? {

        val result = try {

            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()

        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }


    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken

        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)


        return try {

            val user = auth.signInWithCredential(googleCredentials).await().user

            signInViewModel.updateSigningInReq(true)
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName.toString(),
                        profilePictureUrl = photoUrl.toString(),
                        email = email
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = "Login Session Cancelled"
            )
        }


    }


    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName.toString(),
            profilePictureUrl = photoUrl.toString(),
            email = email
        )
    }


    private fun buildSignInRequest(): BeginSignInRequest {

        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.webClientId))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

}