package com.bharath.musicplayerforbaby.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.bharath.musicplayerforbaby.data.signInData.SignInResult
import com.bharath.musicplayerforbaby.presentation.state.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _isSigningIn = MutableStateFlow(false)
    val isSingingIn = _isSigningIn.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun updateSigningInReq(boolean: Boolean) {
        _isSigningIn.update { boolean }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}