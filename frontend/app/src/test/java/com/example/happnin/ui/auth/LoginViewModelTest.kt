package com.example.happnin.ui.auth

import com.example.happnin.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel(FakeAuthRepository())
    }

    @Test
    fun initialState_isIdle() {
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }

    @Test
    fun login_withCredentials_emitsSuccess() = runTest {
        viewModel.login("test@test.com", "password123")
        advanceUntilIdle()
        assertTrue(viewModel.loginState.value is LoginState.Success)
    }

    @Test
    fun login_withEmptyCredentials_emitsError() {
        viewModel.login("", "")
        assertTrue(viewModel.loginState.value is LoginState.Error)
    }

    @Test
    fun resetState_fromSuccess_returnsToIdle() = runTest {
        viewModel.login("test@test.com", "password")
        advanceUntilIdle()
        viewModel.resetState()
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }

    @Test
    fun resetState_fromIdle_remainsIdle() {
        viewModel.resetState()
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = Unit
    }
}
