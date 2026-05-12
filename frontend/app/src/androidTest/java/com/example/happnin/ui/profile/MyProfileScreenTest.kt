package com.example.happnin.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.data.User
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

    private val profileUser = User(
        id = "user-001",
        email = "alex@umass.edu",
        name = "Alex Rivera",
        college = "UMass Amherst",
        role = "student",
    )

    @Test
    fun myProfileScreen_showsMyProfileHeader() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("My Profile").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserName() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Alex Rivera").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserCollege() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("UMass Amherst").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsUserRole() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Student").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsCreateEventButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Create Event").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsAddFriendsButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Add Friends").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_showsLogOutButton() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
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
                    profileViewModel = rememberProfileViewModel(profileUser),
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
                    profileViewModel = rememberProfileViewModel(profileUser),
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
                    profileViewModel = rememberProfileViewModel(profileUser),
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
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = rememberProfileViewModel(profileUser),
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Registered Events").assertIsDisplayed()
    }

    @Test
    fun myProfileScreen_withoutUser_showsUnavailableState() {
        val vm = RegistrationViewModel()
        composeTestRule.setContent {
            HappnInTheme {
                MyProfileScreen(
                    registrationViewModel = vm,
                    profileViewModel = remember { MyProfileViewModel(FakeProfileRepository()) },
                    events = emptyList(),
                )
            }
        }
        composeTestRule.onNodeWithText("Profile unavailable").assertIsDisplayed()
    }

    @Composable
    private fun rememberProfileViewModel(user: User): MyProfileViewModel {
        val viewModel = remember(user) {
            MyProfileViewModel(FakeProfileRepository(user))
        }
        LaunchedEffect(viewModel) {
            viewModel.loadCurrentUser()
        }
        return viewModel
    }

    private class FakeProfileRepository(
        private val user: User? = null,
    ) : ProfileRepository {
        override suspend fun getCurrentUser(): User = requireNotNull(user)
    }
}
