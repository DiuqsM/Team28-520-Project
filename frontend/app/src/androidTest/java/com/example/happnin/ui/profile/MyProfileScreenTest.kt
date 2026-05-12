package com.example.happnin.ui.profile

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
class MyProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myProfileScreen_showsMyProfileHeader() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserName() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        // MockData.currentUser.name = "Alex Rivera"
        composeTestRule.onNodeWithText("Alex Rivera").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserCollege() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("UMass Amherst").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserRole() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Student").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsCreateEventButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Create Event").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsAddFriendsButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Add Friends").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsLogOutButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Log Out").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_noRegisteredEvents_showsNoEventsMessage() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("No registered events yet.").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_withRegisteredEvent_showsEventTitle() {
        val vm = RegistrationViewModel().apply { register("event-001") }
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    events = FakeEventRepository.events,
                )
            }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night", substring = true).assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_clickLogOut_invokesOnLogOut() {
        var loggedOut = false
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    events = emptyList(),
                    onLogOut = { loggedOut = true },
                )
            }
        }
        composeTestRule.onNodeWithText("Log Out").performClick()
        assertTrue(loggedOut)
    }

    @Test
    fun myProfileScreen_showsRegisteredEventsSection() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(registrationViewModel = vm, events = emptyList())
            }
        }
        composeTestRule.onNodeWithText("Registered Events").assertIsDisplayed()
    }
}
