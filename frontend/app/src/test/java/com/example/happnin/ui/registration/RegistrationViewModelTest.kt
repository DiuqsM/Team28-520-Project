package com.example.happnin.ui.registration

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegistrationViewModelTest {

    private lateinit var viewModel: RegistrationViewModel

    @Before
    fun setup() {
        viewModel = RegistrationViewModel()
    }

    @Test
    fun initialState_hasNoRegisteredEvents() {
        assertTrue(viewModel.registeredEventIds.value.isEmpty())
    }

    @Test
    fun register_addsEventIdToSet() {
        viewModel.register("event-001")
        assertTrue(viewModel.registeredEventIds.value.contains("event-001"))
    }

    @Test
    fun register_multipleDistinctEvents_addsAll() {
        viewModel.register("event-001")
        viewModel.register("event-002")
        viewModel.register("event-003")
        assertEquals(setOf("event-001", "event-002", "event-003"), viewModel.registeredEventIds.value)
    }

    @Test
    fun register_sameEventTwice_doesNotDuplicate() {
        viewModel.register("event-001")
        viewModel.register("event-001")
        assertEquals(1, viewModel.registeredEventIds.value.size)
    }

    @Test
    fun isRegistered_returnsTrueForRegisteredEvent() {
        viewModel.register("event-001")
        assertTrue(viewModel.isRegistered("event-001"))
    }

    @Test
    fun isRegistered_returnsFalseForUnregisteredEvent() {
        assertFalse(viewModel.isRegistered("event-999"))
    }

    @Test
    fun isRegistered_returnsFalseBeforeAnyRegistration() {
        assertFalse(viewModel.isRegistered("event-001"))
    }
}
