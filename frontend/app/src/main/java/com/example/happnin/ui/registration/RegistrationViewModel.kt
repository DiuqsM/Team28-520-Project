package com.example.happnin.ui.registration

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegistrationViewModel : ViewModel() {
    private val _registeredEventIds = MutableStateFlow<Set<String>>(emptySet())
    val registeredEventIds: StateFlow<Set<String>> = _registeredEventIds.asStateFlow()

    fun register(eventId: String) {
        // TODO: Connect to registration API when backend is ready
        // TODO: Srijan/Ava - POST /registrations with { user_id, event_id } body
        // On success: add eventId to _registeredEventIds
        // On failure: surface an error (e.g. event full, already registered)
        _registeredEventIds.value = _registeredEventIds.value + eventId
    }

    fun isRegistered(eventId: String): Boolean =
        _registeredEventIds.value.contains(eventId)
}
