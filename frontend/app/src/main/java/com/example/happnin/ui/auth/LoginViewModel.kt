package com.example.happnin.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()    // TODO: show spinner during real auth
    object Success : LoginState()
    data class Error(val message: String) : LoginState()  // TODO: surface real error messages
}

class LoginViewModel(
    private val authRepository: AuthRepository = SupabaseAuthRepository(),
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Email and password are required.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            runCatching { authRepository.login(email, password) }
                .onSuccess { _loginState.value = LoginState.Success }
                .onFailure {
                    _loginState.value = LoginState.Error(
                        it.localizedMessage ?: "Could not sign in."
                    )
                }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
