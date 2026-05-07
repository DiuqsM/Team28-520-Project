package com.example.happnin

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.auth.LoginScreen
import com.example.happnin.ui.events.EventsScreen
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
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    // ── Shared registration state ─────────────────────────────────────────────────
    // Scoped to HappnInApp so registered state is consistent across all tabs.
    val registrationViewModel: RegistrationViewModel = viewModel()
    val registeredEventIds by registrationViewModel.registeredEventIds.collectAsState()

    val events = remember { FakeEventRepository.events }

    if (!isLoggedIn) {
        // Login screen — no bottom nav
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
        ) { padding ->
            LoginScreen(
                onLoginSuccess = { isLoggedIn = true },
                modifier = Modifier.padding(padding),
            )
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.White,
            bottomBar = {
                HappnInBottomBar(
                    currentDestination = currentDestination,
                    onDestinationClick = { currentDestination = it },
                )
            },
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HOME -> EventsScreen(
                    events = events,
                    modifier = Modifier.padding(innerPadding),
                    registeredEventIds = registeredEventIds,
                    onRegisterClick = { event -> registrationViewModel.register(event.id) },
                )
                AppDestinations.FAVORITES -> MyEventsScreen(
                    registrationViewModel = registrationViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
                AppDestinations.PROFILE -> MyProfileScreen(
                    registrationViewModel = registrationViewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

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

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = displayText)
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
