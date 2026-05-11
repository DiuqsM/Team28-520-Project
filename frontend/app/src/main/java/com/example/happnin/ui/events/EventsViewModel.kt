package com.example.happnin.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happnin.ApiClient
import com.example.happnin.data.Event
import com.example.happnin.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<Event>) : EventsUiState()
    data class Error(val message: String) : EventsUiState()
}

class EventsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EventsUiState>(EventsUiState.Loading)
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.value = EventsUiState.Loading
            try {
                val response = ApiClient.retrofitService.getEvents()
                _uiState.value = EventsUiState.Success(response.data.map { it.toDomain() })
            } catch (e: Exception) {
                _uiState.value = EventsUiState.Error("Could not load events. Check your connection and try again.")
            }
        }
    }
}
