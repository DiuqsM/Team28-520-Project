package com.example.happnin.ui.events

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.data.EventStatus
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.theme.HappnInTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val openEvent = FakeEventRepository.events.first()       // OPEN
    private val fullEvent = FakeEventRepository.events.first { it.status == EventStatus.FULL }

    @Test
    fun eventCard_showsEventTitle() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night").assertIsDisplayed()
    }

    @Test
    fun eventCard_showsLocation() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Student Union Ballroom").assertIsDisplayed()
    }

    @Test
    fun eventCard_showsSeeMoreButton() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent) }
        }
        composeTestRule.onNodeWithText("See More").assertIsDisplayed()
    }

    @Test
    fun eventCard_openEvent_notRegistered_showsRegisterButton() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent, isRegistered = false) }
        }
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun eventCard_registeredEvent_showsRegisteredButton() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent, isRegistered = true) }
        }
        composeTestRule.onNodeWithText("Registered").assertIsDisplayed()
    }

    @Test
    fun eventCard_fullEvent_showsFullStatusLabel() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = fullEvent, isRegistered = false) }
        }
        composeTestRule.onNodeWithText("Full").assertIsDisplayed()
    }

    @Test
    fun eventCard_clickSeeMore_invokesOnSeeMoreClick() {
        var seeMoreClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                EventCard(event = openEvent, onSeeMoreClick = { seeMoreClicked = true })
            }
        }
        composeTestRule.onNodeWithText("See More").performClick()
        assertTrue(seeMoreClicked)
    }

    @Test
    fun eventCard_clickRegister_invokesOnRegisterClick() {
        var registerClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                EventCard(
                    event = openEvent,
                    isRegistered = false,
                    onRegisterClick = { registerClicked = true },
                )
            }
        }
        composeTestRule.onNodeWithText("Register").performClick()
        assertTrue(registerClicked)
    }

    @Test
    fun eventCard_showsDateInfo() {
        composeTestRule.setContent {
            HappnInTheme { EventCard(event = openEvent) }
        }
        // Event starts April 30 at 7:00pm - verify date portion appears
        composeTestRule.onNodeWithText("April 30", substring = true).assertIsDisplayed()
    }
}
