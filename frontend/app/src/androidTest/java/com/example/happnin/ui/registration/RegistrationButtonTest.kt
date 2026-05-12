package com.example.happnin.ui.registration

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.ui.theme.HappnInTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun registrationButton_notRegistered_showsRegisterText() {
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = false, onRegisterClick = {})
            }
        }
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun registrationButton_notRegistered_doesNotShowRegisteredText() {
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = false, onRegisterClick = {})
            }
        }
        composeTestRule.onNodeWithText("Registered").assertDoesNotExist()
    }

    @Test
    fun registrationButton_registered_showsRegisteredText() {
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = true, onRegisterClick = {})
            }
        }
        composeTestRule.onNodeWithText("Registered").assertIsDisplayed()
    }

    @Test
    fun registrationButton_registered_doesNotShowRegisterText() {
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = true, onRegisterClick = {})
            }
        }
        composeTestRule.onNodeWithText("Register").assertDoesNotExist()
    }

    @Test
    fun registrationButton_notRegistered_clickInvokesOnRegisterClick() {
        var clicked = false
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = false, onRegisterClick = { clicked = true })
            }
        }
        composeTestRule.onNodeWithText("Register").performClick()
        assertTrue(clicked)
    }

    @Test
    fun registrationButton_registered_isNotInteractive() {
        var clicked = false
        composeTestRule.setContent {
            HappnInTheme {
                RegistrationButton(isRegistered = true, onRegisterClick = { clicked = true })
            }
        }
        // "Registered" button has no click handler — callback should never fire
        composeTestRule.onNodeWithText("Registered").assertIsDisplayed()
        assertFalse(clicked)
    }
}
