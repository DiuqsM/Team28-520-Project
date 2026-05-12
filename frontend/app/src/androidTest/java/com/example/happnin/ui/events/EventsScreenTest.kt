package com.example.happnin.ui.events

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.theme.HappnInTheme
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── State rendering tests ────────────────────────────────────────────────────

    @Test
    fun eventsScreen_loadingState_showsProgressIndicator() {
        composeTestRule.setContent {
            HappnInTheme { EventsScreen(uiState = EventsUiState.Loading) }
        }
        composeTestRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)
        ).assertIsDisplayed()
    }

    @Test
    fun eventsScreen_errorState_showsErrorMessage() {
        composeTestRule.setContent {
            HappnInTheme { EventsScreen(uiState = EventsUiState.Error("Network error")) }
        }
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_errorState_showsRetryButton() {
        composeTestRule.setContent {
            HappnInTheme { EventsScreen(uiState = EventsUiState.Error("Network error")) }
        }
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_errorState_clickRetry_invokesOnRetry() {
        var retried = false
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(
                    uiState = EventsUiState.Error("Network error"),
                    onRetry = { retried = true },
                )
            }
        }
        composeTestRule.onNodeWithText("Retry").performClick()
        assertTrue(retried)
    }

    @Test
    fun eventsScreen_successState_showsExploreHeader() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithText("Explore").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_successState_showsEventsNearbyLabel() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithText("Events Nearby").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_successState_showsFirstEventTitle() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithText("Five Colleges Open Mic Night").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_emptySuccessState_showsNoEventsMessage() {
        composeTestRule.setContent {
            HappnInTheme { EventsScreen(uiState = EventsUiState.Success(emptyList())) }
        }
        composeTestRule.onNodeWithText("No events nearby.").assertIsDisplayed()
    }

    // ── Interaction tests ────────────────────────────────────────────────────────

    @Test
    fun eventsScreen_clickSearchButton_revealsSearchField() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithTag("SearchButton").performClick()
        composeTestRule.onNodeWithText("Search events").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_clickFilterButton_revealsFilterPanel() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithTag("FilterButton").performClick()
        composeTestRule.onNodeWithText("Filter").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_clickSortButton_revealsSortPanel() {
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
            }
        }
        composeTestRule.onNodeWithTag("SortButton").performClick()
        composeTestRule.onNodeWithText("Sort").assertIsDisplayed()
    }

    @Test
    fun eventsScreen_clickEventSeeMore_invokesOnEventClick() {
        var eventClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(
                    uiState = EventsUiState.Success(FakeEventRepository.events),
                    onEventClick = { eventClicked = true },
                )
            }
        }
        composeTestRule.onAllNodesWithText("See More")[0].performClick()
        assertTrue(eventClicked)
    }

    @Test
    fun eventsScreen_clickRegisterOnOpenEvent_invokesOnRegisterClick() {
        var registerClicked = false
        composeTestRule.setContent {
            HappnInTheme {
                EventsScreen(
                    uiState = EventsUiState.Success(FakeEventRepository.events),
                    onRegisterClick = { registerClicked = true },
                )
            }
        }
        composeTestRule.onAllNodesWithText("Register")[0].performClick()
        assertTrue(registerClicked)
    }
}
