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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.events.EventsScreen
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

@PreviewScreenSizes
@Composable
fun HappnInApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val events = remember { FakeEventRepository.events }

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
            )

            AppDestinations.FAVORITES -> PlaceholderScreen(
                title = "My Events",
                modifier = Modifier.padding(innerPadding),
            )

            AppDestinations.PROFILE -> PlaceholderScreen(
                title = "My Profile",
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

enum class AppDestinations(
    val label: String,
) {
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
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center,
        ) {
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

@Composable
private fun CompassIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(
            color = color,
            radius = size.minDimension * 0.34f,
            center = Offset(size.width / 2f, size.height / 2f),
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.58f, size.height * 0.42f),
            end = Offset(size.width * 0.38f, size.height * 0.68f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(
            color = color,
            radius = 1.5.dp.toPx(),
            center = Offset(size.width * 0.58f, size.height * 0.42f),
        )
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
        drawArc(
            color = color,
            startAngle = 25f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.34f, size.height * 0.34f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.32f, size.height * 0.34f),
            style = stroke,
        )
    }
}

@Composable
private fun UserIcon(color: Color) {
    Canvas(modifier = Modifier.size(24.dp)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(
            color = color,
            radius = size.minDimension * 0.16f,
            center = Offset(size.width / 2f, size.height * 0.34f),
            style = stroke,
        )
        drawArc(
            color = color,
            startAngle = 205f,
            sweepAngle = 130f,
            useCenter = false,
            topLeft = Offset(size.width * 0.25f, size.height * 0.50f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.34f),
            style = stroke,
        )
    }
}

@Composable
fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = title)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HappnInTheme {
        PlaceholderScreen("Profile")
    }
}
