package com.example.happnin.ui.myevents

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MyEventsUiState(
    val isLoading: Boolean = false,
    val sortOrder: String = "Soonest", // stub — filter/sort not yet implemented
)

class MyEventsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    // TODO: Ava/Srijan - connect to GET /users/{id}/registrations API here to refresh the list
    fun refresh() {
        // no-op until backend is ready
    }
}
