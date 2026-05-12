package com.example.happnin.ui.myevents

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.registration.RegistrationViewModel
import com.example.happnin.ui.theme.HappnInTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyEventsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myEventsScreen_showsMyEventsHeader() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("My Events").assertIsDisplayed()
    }

    @Test
    fun myEventsScreen_noRegisteredEvents_showsEmptyStateMessage() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("No registered events yet.", substring = true).assertIsDisplayed()
    }

    @Test
    fun myEventsScreen_withRegisteredEvent_showsEventTitle() {
        val vm = RegistrationViewModel().apply { register("event-001") }
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night").assertIsDisplayed()
    }

    @Test
    fun myEventsScreen_withRegisteredEvent_showsSeeMoreButton() {
        val vm = RegistrationViewModel().apply { register("event-001") }
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("See More").assertIsDisplayed()
    }

    @Test
    fun myEventsScreen_withRegisteredEvent_showsChatButton() {
        val vm = RegistrationViewModel().apply { register("event-001") }
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("Chat").assertIsDisplayed()
    }

    @Test
    fun myEventsScreen_clickSeeMore_invokesOnSeeMoreClick() {
        var clicked = false
        val vm = RegistrationViewModel().apply { register("event-001") }
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                    onSeeMoreClick = { clicked = true },
                )
            }
        }
        composeTestRule.onNodeWithText("See More").performClick()
        assertTrue(clicked)
    }

    @Test
    fun myEventsScreen_multipleRegisteredEvents_showsAllTitles() {
        val vm = RegistrationViewModel().apply {
            register("event-001")
            register("event-002")
        }
        composeTestRule.setContent {
            HappnInTheme {
                MyEventsScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night").assertIsDisplayed()
        composeTestRule.onNodeWithText("Intro to Product Design Workshop").assertIsDisplayed()
    }
}
