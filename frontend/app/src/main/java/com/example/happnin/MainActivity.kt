package com.example.happnin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happnin.ui.auth.LoginScreen
import com.example.happnin.ui.events.EventDetailScreen
import com.example.happnin.ui.auth.SignUpScreen
import com.example.happnin.ui.events.EventsScreen
import com.example.happnin.ui.events.EventsUiState
import com.example.happnin.ui.events.EventsViewModel
import com.example.happnin.ui.myevents.MyEventsScreen
import com.example.happnin.ui.profile.MyProfileScreen
import com.example.happnin.ui.registration.RegistrationViewModel
import com.example.happnin.ui.theme.HappnInPurple
import com.example.happnin.ui.theme.HappnInTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HappnInTheme {
                HappnInApp()
            }
        }
    }
}

@Composable
fun HappnInApp() {
    // ── Auth gate ────────────────────────────────────────────────────────────────
    // TODO: Ava/Srijan - replace with a real Supabase session check
    var authScreen by rememberSaveable { mutableStateOf(AuthScreen.LOGIN) }
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // ── Shared registration state ─────────────────────────────────────────────────
    val registrationViewModel: RegistrationViewModel = viewModel()
    val registeredEventIds by registrationViewModel.registeredEventIds.collectAsState()
    val registrationError by registrationViewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            registrationViewModel.refreshRegistrations()
        }
    }

    LaunchedEffect(registrationError) {
        val message = registrationError ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        registrationViewModel.clearError()
    }

    val eventsViewModel: EventsViewModel = viewModel()
    val eventsUiState by eventsViewModel.uiState.collectAsState()
    val loadedEvents = (eventsUiState as? EventsUiState.Success)?.events.orEmpty()

    var selectedEventId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedEvent = loadedEvents.firstOrNull { it.id == selectedEventId }

    if (!isLoggedIn) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            when (authScreen) {
                AuthScreen.LOGIN -> LoginScreen(
                    onLoginSuccess = { isLoggedIn = true },
                    onSignUpClick = { authScreen = AuthScreen.SIGN_UP },
                    modifier = Modifier.padding(padding),
                )
                AuthScreen.SIGN_UP -> SignUpScreen(
                    onSignUpSuccess = { isLoggedIn = true },
                    onLoginClick = { authScreen = AuthScreen.LOGIN },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    } else if (selectedEvent != null) {
        val event = selectedEvent
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            EventDetailScreen(
                event = event,
                isRegistered = registeredEventIds.contains(event.id),
                onBack = { selectedEventId = null },
                onRegisterClick = {
                    registrationViewModel.register(event.id)
                    selectedEventId = null
                },
                modifier = Modifier.padding(padding),
            )
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                HappnInBottomBar(
                    currentDestination = currentDestination,
                    onDestinationClick = { currentDestination = it },
                )
            },
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> EventsScreen(
                    uiState = eventsUiState,
                    modifier = Modifier.padding(innerPadding),
                    registeredEventIds = registeredEventIds,
                    onEventClick = { event -> selectedEventId = event.id },
                    onRegisterClick = { event -> registrationViewModel.register(event.id) },
                    onRetry = { eventsViewModel.loadEvents() },
                )
                AppDestinations.FAVORITES -> MyEventsScreen(
                    registrationViewModel = registrationViewModel,
                    events = loadedEvents,
                    modifier = Modifier.padding(innerPadding),
                )
                AppDestinations.PROFILE -> MyProfileScreen(
                    registrationViewModel = registrationViewModel,
                    events = loadedEvents,
                    modifier = Modifier.padding(innerPadding),
                    onLogOut = {
                        isLoggedIn = false
                        authScreen = AuthScreen.LOGIN
                    },
                )
            }
        }
    }
}

enum class AuthScreen { LOGIN, SIGN_UP }

enum class AppDestinations(val label: String) {
    HOME("Explore"),
    FAVORITES("My Events"),
    PROFILE("My Profile"),
}

@Composable
private fun HappnInBottomBar(
    currentDestination: AppDestinations,
    onDestinationClick: (AppDestinations) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .height(68.dp)
            .padding(horizontal = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppDestinations.entries.forEach { destination ->
            BottomNavItem(
                destination = destination,
                selected = destination == currentDestination,
                onClick = { onDestinationClick(destination) },
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title Screen Coming Soon!")
    }
}

@Composable
private fun BottomNavItem(
    destination: AppDestinations,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) HappnInPurple else Color(0xFF2C2C2C)

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            when (destination) {
                AppDestinations.HOME -> CompassIcon(color = color)
                AppDestinations.FAVORITES -> SmileIcon(color = color)
                AppDestinations.PROFILE -> UserIcon(color = color)
            }
        }
        Text(
            text = destination.label,
            color = color,
            fontWeight = FontWeight.SemiBold,
            style = androidx.compose.material3.MaterialTheme.typography.labelLarge,
        )
    }
}

// ── Bottom-nav icons (Canvas) ─────────────────────────────────────────────────

@Composable
private fun CompassIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color, size.minDimension * 0.34f, Offset(size.width / 2f, size.height / 2f), style = stroke)
        drawLine(color, Offset(size.width * 0.58f, size.height * 0.42f), Offset(size.width * 0.38f, size.height * 0.68f), 2.dp.toPx(), StrokeCap.Round)
        drawCircle(color, 1.5.dp.toPx(), Offset(size.width * 0.58f, size.height * 0.42f))
    }
}

@Composable
private fun SmileIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color, size.minDimension * 0.34f, center, style = stroke)
        drawCircle(color, 1.4.dp.toPx(), Offset(size.width * 0.39f, size.height * 0.43f))
        drawCircle(color, 1.4.dp.toPx(), Offset(size.width * 0.61f, size.height * 0.43f))
        drawArc(color, 25f, 130f, false,
            Offset(size.width * 0.34f, size.height * 0.34f),
            androidx.compose.ui.geometry.Size(size.width * 0.32f, size.height * 0.34f),
            style = stroke)
    }
}

@Composable
private fun UserIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(color, size.minDimension * 0.16f, Offset(size.width / 2f, size.height * 0.34f), style = stroke)
        drawArc(color, 205f, 130f, false,
            Offset(size.width * 0.25f, size.height * 0.50f),
            androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.34f),
            style = stroke)
    }
}
