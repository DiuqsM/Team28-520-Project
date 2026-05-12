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
class SignUpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signUpScreen_showsAppTitle() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("HappnIn").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsCreateAccountSubtitle() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Create your account").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsFirstNameField() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("First Name").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsLastNameField() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Last Name").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsUsernameField() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsEmailField() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsPasswordField() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsRegisterButton() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsLoginLink() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Already have an account? Log In").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_clickLoginLink_invokesOnLoginClick() {
        var loginClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                SignUpScreen(onSignUpSuccess = {}, onLoginClick = { loginClicked = true })
            }
        }
        composeTestRule.onNodeWithText("Already have an account? Log In").performClick()
        assertTrue(loginClicked)
    }

    @Test
    fun signUpScreen_clickRegisterWithEmptyFields_showsErrorMessage() {
        composeTestRule.setContent {
            HappnInTheme { SignUpScreen(onSignUpSuccess = {}, onLoginClick = {}) }
        }
        composeTestRule.onNodeWithText("Register").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("All fields are required.").assertIsDisplayed()
    }
}
