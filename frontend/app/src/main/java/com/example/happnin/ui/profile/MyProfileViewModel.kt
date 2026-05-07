package com.example.happnin.ui.profile

import androidx.lifecycle.ViewModel
import com.example.happnin.data.MockData
import com.example.happnin.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MyProfileUiState(
    val user: User = MockData.currentUser,
    val isLoading: Boolean = false,
)

class MyProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    // TODO: Ava/Srijan - connect to GET /users/me API here to load the real user
}
