package com.example.happnin.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happnin.data.SupabaseNetwork
import com.example.happnin.data.User
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

data class MyProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

interface ProfileRepository {
    suspend fun getCurrentUser(): User
}

class SupabaseProfileRepository : ProfileRepository {
    override suspend fun getCurrentUser(): User {
        val auth = SupabaseNetwork.client.auth
        auth.awaitInitialization()
        if (auth.currentUserOrNull() == null) {
            auth.loadFromStorage()
        }

        val authUser = auth.currentUserOrNull()
            ?: throw IllegalStateException("You must be signed in to view your profile.")

        return authUser.toDomainUser()
    }
}

class MyProfileViewModel(
    private val repository: ProfileRepository = SupabaseProfileRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    fun loadCurrentUser() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            runCatching { repository.getCurrentUser() }
                .onSuccess { user ->
                    _uiState.value = MyProfileUiState(user = user)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = it.message ?: "Could not load profile.",
                    )
                }
        }
    }

    fun reset() {
        _uiState.value = MyProfileUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

private fun UserInfo.toDomainUser(): User {
    val metadata = userMetadata
    val firstName = metadata.stringValue("first_name", "given_name")
    val lastName = metadata.stringValue("last_name", "family_name")
    val fullName = metadata.stringValue("full_name", "name")
    val username = metadata.stringValue("username", "preferred_username", "user_name")
    val displayName = fullName
        ?: listOfNotNull(firstName, lastName).joinToString(" ").takeIf { it.isNotBlank() }
        ?: username
        ?: email?.substringBefore("@")
        ?: "HappnIn User"

    return User(
        id = id,
        googleSub = metadata.stringValue("sub", "provider_id"),
        email = email.orEmpty(),
        name = displayName,
        profilePhotoUrl = metadata.stringValue("avatar_url", "picture", "profile_photo_url"),
        college = metadata.stringValue("college", "school", "affiliation") ?: "Five Colleges",
        role = metadata.stringValue("role") ?: "student",
        isSuspended = false,
    )
}

private fun JsonObject?.stringValue(vararg keys: String): String? {
    if (this == null) return null
    return keys.firstNotNullOfOrNull { key ->
        get(key)?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
    }
}
