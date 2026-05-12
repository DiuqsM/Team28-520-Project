package com.example.happnin.ui.registration

import com.example.happnin.MainDispatcherRule
import com.example.happnin.Registration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: RegistrationViewModel

    @Before
    fun setup() {
        viewModel = RegistrationViewModel(FakeRegistrationRepository())
    }

    @Test
    fun initialState_hasNoRegisteredEvents() {
        assertTrue(viewModel.registeredEventIds.value.isEmpty())
    }

    @Test
    fun register_addsEventIdToSet() = runTest {
        viewModel.register("event-001")
        advanceUntilIdle()
        assertTrue(viewModel.registeredEventIds.value.contains("event-001"))
    }

    @Test
    fun register_multipleDistinctEvents_addsAll() = runTest {
        viewModel.register("event-001")
        viewModel.register("event-002")
        viewModel.register("event-003")
        advanceUntilIdle()
        assertEquals(setOf("event-001", "event-002", "event-003"), viewModel.registeredEventIds.value)
    }

    @Test
    fun register_sameEventTwice_doesNotDuplicate() = runTest {
        viewModel.register("event-001")
        viewModel.register("event-001")
        advanceUntilIdle()
        assertEquals(1, viewModel.registeredEventIds.value.size)
    }

    @Test
    fun unregister_removesEventIdFromSet() = runTest {
        viewModel.register("event-001")
        advanceUntilIdle()
        viewModel.unregister("event-001")
        advanceUntilIdle()
        assertFalse(viewModel.registeredEventIds.value.contains("event-001"))
    }

    @Test
    fun isRegistered_returnsTrueForRegisteredEvent() = runTest {
        viewModel.register("event-001")
        advanceUntilIdle()
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

    private class FakeRegistrationRepository : RegistrationRepository {
        override suspend fun getRegisteredEventIds(): Set<String> = emptySet()

        override suspend fun createRegistration(eventId: String): Registration {
            return Registration(
                id = "registration-$eventId",
                userId = "user-001",
                eventId = eventId,
            )
        }

        override suspend fun deleteRegistrationForEvent(eventId: String) = Unit
    }
}
