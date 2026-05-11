package com.example.happnin.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happnin.data.SupabaseNetwork // Make sure this matches your client's package!
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

class SignUpViewModel : ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    fun register(firstName: String, lastName: String, username: String, email: String, password: String) {
        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            _signUpState.value = SignUpState.Error("All fields are required.")
            return
        }

        _signUpState.value = SignUpState.Loading

        viewModelScope.launch {
            try {
                SupabaseNetwork.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password

                    data = buildJsonObject {
                        put("first_name", firstName)
                        put("last_name", lastName)
                        put("username", username)
                    }
                }

                _signUpState.value = SignUpState.Success

            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "An unknown error occurred"
                _signUpState.value = SignUpState.Error(errorMessage)
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}
