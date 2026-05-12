package com.example.happnin.ui.events

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class EventDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val openEvent = FakeEventRepository.events.first() // "Five Colleges Open Mic Night", OPEN, Free
    private val fullEvent = FakeEventRepository.events.first { it.status == EventStatus.FULL }
    private val paidEvent = FakeEventRepository.events.first { it.price > 0.0 } // event-002: $5.00

    @Test
    fun eventDetailScreen_showsEventTitleInTopBar() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_openEvent_showsOpenStatusBadge() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Open").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_showsEventLocation() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Student Union Ballroom").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_showsEventDescription() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText(openEvent.description, substring = true).assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_freeEvent_showsFreePrice() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText("Free").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_paidEvent_showsFormattedPrice() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = paidEvent) }
        }
        composeTestRule.onNodeWithText("$5.00").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_openNotRegistered_showsRegisterButton() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent, isRegistered = false) }
        }
        composeTestRule.onNodeWithText("Register").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_registered_showsRegisteredButton() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent, isRegistered = true) }
        }
        composeTestRule.onNodeWithText("Registered").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_fullEvent_showsFullStatusBadge() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = fullEvent) }
        }
        composeTestRule.onNodeWithText("Full").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_showsBackButton() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun eventDetailScreen_clickBack_invokesOnBack() {
        var backCalled = false
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent, onBack = { backCalled = true }) }
        }
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assertTrue(backCalled)
    }

    @Test
    fun eventDetailScreen_clickRegister_invokesOnRegisterClick() {
        var registerCalled = false
        composeTestRule.setContent {
            HappnInTheme {
                EventDetailScreen(
                    event = openEvent,
                    isRegistered = false,
                    onRegisterClick = { registerCalled = true },
                )
            }
        }
        composeTestRule.onNodeWithText("Register").performClick()
        assertTrue(registerCalled)
    }

    @Test
    fun eventDetailScreen_showsCollegeWhenNotBlank() {
        composeTestRule.setContent {
            HappnInTheme { EventDetailScreen(event = openEvent) }
        }
        composeTestRule.onNodeWithText("UMass Amherst").assertIsDisplayed()
    }
}
