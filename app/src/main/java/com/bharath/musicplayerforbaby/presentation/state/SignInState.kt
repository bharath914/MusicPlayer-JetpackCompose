package com.bharath.musicplayerforbaby.presentation.state

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)