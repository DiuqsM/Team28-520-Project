package com.example.happnin.ui.auth

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        viewModel = SignUpViewModel()
    }

    @Test
    fun initialState_isIdle() {
        assertTrue(viewModel.signUpState.value is SignUpState.Idle)
    }

    @Test
    fun register_blankFirstName_emitsErrorWithMessage() {
        viewModel.register("", "Last", "username", "email@test.com", "password")
        val state = viewModel.signUpState.value
        assertTrue(state is SignUpState.Error)
        assertEquals("All fields are required.", (state as SignUpState.Error).message)
    }

    @Test
    fun register_blankLastName_emitsError() {
        viewModel.register("First", "", "username", "email@test.com", "password")
        assertTrue(viewModel.signUpState.value is SignUpState.Error)
    }

    @Test
    fun register_blankUsername_emitsError() {
        viewModel.register("First", "Last", "", "email@test.com", "password")
        assertTrue(viewModel.signUpState.value is SignUpState.Error)
    }

    @Test
    fun register_blankEmail_emitsError() {
        viewModel.register("First", "Last", "username", "", "password")
        assertTrue(viewModel.signUpState.value is SignUpState.Error)
    }

    @Test
    fun register_blankPassword_emitsError() {
        viewModel.register("First", "Last", "username", "email@test.com", "")
        assertTrue(viewModel.signUpState.value is SignUpState.Error)
    }

    @Test
    fun register_allBlankFields_emitsErrorWithCorrectMessage() {
        viewModel.register("", "", "", "", "")
        val state = viewModel.signUpState.value
        assertTrue(state is SignUpState.Error)
        assertEquals("All fields are required.", (state as SignUpState.Error).message)
    }

    @Test
    fun resetState_fromError_returnsToIdle() {
        viewModel.register("", "Last", "username", "email@test.com", "password")
        viewModel.resetState()
        assertTrue(viewModel.signUpState.value is SignUpState.Idle)
    }
}
