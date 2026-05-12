package com.example.happnin.ui.auth

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    @Test
    fun initialState_isIdle() {
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }

    @Test
    fun login_withCredentials_emitsSuccess() {
        viewModel.login("test@test.com", "password123")
        assertTrue(viewModel.loginState.value is LoginState.Success)
    }

    @Test
    fun login_withEmptyCredentials_stillEmitsSuccess() {
        viewModel.login("", "")
        assertTrue(viewModel.loginState.value is LoginState.Success)
    }

    @Test
    fun resetState_fromSuccess_returnsToIdle() {
        viewModel.login("test@test.com", "password")
        viewModel.resetState()
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }

    @Test
    fun resetState_fromIdle_remainsIdle() {
        viewModel.resetState()
        assertTrue(viewModel.loginState.value is LoginState.Idle)
    }
}
