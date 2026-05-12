package com.example.happnin.ui.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.ui.theme.HappnInTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_showsAppTitle() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("HappnIn").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsSubtitle() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("Discover events near you").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsEmailField() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsPasswordField() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsLogInButton() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsSignUpLink() {
        composeTestRule.setContent {
            HappnInTheme { LoginScreen(onLoginSuccess = {}) }
        }
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").assertIsDisplayed()
    }

    @Test
    fun loginScreen_clickLogIn_invokesOnLoginSuccess() {
        var successCalled = false
        composeTestRule.setContent {
            HappnInTheme {
                LoginScreen(onLoginSuccess = { successCalled = true })
            }
        }
        composeTestRule.onNodeWithText("Log In").performClick()
        composeTestRule.waitForIdle()
        assertTrue(successCalled)
    }

    @Test
    fun loginScreen_clickSignUpLink_invokesOnSignUpClick() {
        var signUpClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onSignUpClick = { signUpClicked = true },
                )
            }
        }
        composeTestRule.onNodeWithText("Don't have an account? Sign Up").performClick()
        assertTrue(signUpClicked)
    }
}
