package com.example.happnin.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()    // TODO: show spinner during real auth
    object Success : LoginState()
    data class Error(val message: String) : LoginState()  // TODO: surface real error messages
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        // TODO: Replace this block with real Supabase auth
        // Example:
        //   val result = supabaseClient.auth.signInWith(Email) {
        //       this.email = email
        //       this.password = password
        //   }
        //   On success: _loginState.value = LoginState.Success
        //   On failure: _loginState.value = LoginState.Error(result.errorMessage)

        // For now, just navigate directly:
        _loginState.value = LoginState.Success
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
