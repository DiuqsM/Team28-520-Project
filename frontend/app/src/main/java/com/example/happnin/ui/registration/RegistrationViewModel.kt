package com.example.happnin.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val repository: RegistrationRepository = ApiRegistrationRepository(),
) : ViewModel() {
    private val _registeredEventIds = MutableStateFlow<Set<String>>(emptySet())
    val registeredEventIds: StateFlow<Set<String>> = _registeredEventIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun refreshRegistrations() {
        _registeredEventIds.value = emptySet()
        _errorMessage.value = null

        viewModelScope.launch {
            runCatching { repository.getRegisteredEventIds() }
                .onSuccess { _registeredEventIds.value = it }
                .onFailure { _errorMessage.value = it.message ?: "Could not load registrations." }
        }
    }

    fun register(eventId: String) {
        if (eventId in _registeredEventIds.value) return

        val previousIds = _registeredEventIds.value
        _errorMessage.value = null
        _registeredEventIds.value = _registeredEventIds.value + eventId

        viewModelScope.launch {
            runCatching { repository.createRegistration(eventId) }
                .onFailure {
                    _registeredEventIds.value = previousIds
                    _errorMessage.value = it.message ?: "Could not register for this event."
                }
        }
    }

    fun unregister(eventId: String) {
        if (eventId !in _registeredEventIds.value) return

        val previousIds = _registeredEventIds.value
        _errorMessage.value = null
        _registeredEventIds.value = _registeredEventIds.value - eventId

        viewModelScope.launch {
            runCatching { repository.deleteRegistrationForEvent(eventId) }
                .onFailure {
                    _registeredEventIds.value = previousIds
                    _errorMessage.value = it.message ?: "Could not cancel this registration."
                }
        }
    }

    fun isRegistered(eventId: String): Boolean =
        _registeredEventIds.value.contains(eventId)

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearRegistrations() {
        _registeredEventIds.value = emptySet()
        _errorMessage.value = null
    }
}
