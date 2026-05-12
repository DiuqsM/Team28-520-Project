package com.example.happnin.ui.profile

import com.example.happnin.MainDispatcherRule
import com.example.happnin.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MyProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadCurrentUser_success_updatesUser() = runTest {
        val user = User(
            id = "user-123",
            email = "sam@example.com",
            name = "Sam Student",
            college = "UMass Amherst",
        )
        val viewModel = MyProfileViewModel(FakeProfileRepository(user))

        viewModel.loadCurrentUser()
        advanceUntilIdle()

        assertEquals(user, viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loadCurrentUser_failure_setsError() = runTest {
        val viewModel = MyProfileViewModel(
            FakeProfileRepository(error = IllegalStateException("Profile unavailable")),
        )

        viewModel.loadCurrentUser()
        advanceUntilIdle()

        assertEquals("Profile unavailable", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun reset_clearsUser() = runTest {
        val user = User(
            id = "user-123",
            email = "sam@example.com",
            name = "Sam Student",
            college = "UMass Amherst",
        )
        val viewModel = MyProfileViewModel(FakeProfileRepository(user))

        viewModel.loadCurrentUser()
        advanceUntilIdle()
        viewModel.reset()

        assertNull(viewModel.uiState.value.user)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun clearError_removesErrorMessage() = runTest {
        val viewModel = MyProfileViewModel(
            FakeProfileRepository(error = IllegalStateException("Profile unavailable")),
        )

        viewModel.loadCurrentUser()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.errorMessage != null)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    private class FakeProfileRepository(
        private val user: User? = null,
        private val error: Throwable? = null,
    ) : ProfileRepository {
        override suspend fun getCurrentUser(): User {
            error?.let { throw it }
            return requireNotNull(user)
        }
    }
}
