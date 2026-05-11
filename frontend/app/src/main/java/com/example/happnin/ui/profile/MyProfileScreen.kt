package com.example.happnin.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happnin.data.Event
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.registration.RegistrationViewModel
import com.example.happnin.ui.theme.HappnInDark
import com.example.happnin.ui.theme.HappnInPurple
import com.example.happnin.ui.theme.HappnInTheme
import kotlinx.datetime.LocalDateTime

// Teammate hook: pass real navigation lambdas when Create Event (Ava), Add Friends (Ava),
// and Event Detail (Sami) screens are ready.
@Composable
fun MyProfileScreen(
    registrationViewModel: RegistrationViewModel,
    events: List<Event> = FakeEventRepository.events,
    modifier: Modifier = Modifier,
    profileViewModel: MyProfileViewModel = viewModel(),
    onCreateEventClick: () -> Unit = {},  // TODO: Ava - wire Create Event screen
    onAddFriendsClick: () -> Unit = {},   // TODO: Ava - wire Add Friends screen
    onSettingsClick: () -> Unit = {},     // TODO: implement Settings screen
    onEventClick: (Event) -> Unit = {},   // TODO: Sami - wire Event Detail screen
    onLogOut: () -> Unit = {},
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val registeredIds by registrationViewModel.registeredEventIds.collectAsState()
    val user = profileState.user

    val myEvents = remember(events, registeredIds) {
        events.filter { it.id in registeredIds }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        // ── Top bar ─────────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "My Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                IconButton(onClick = onSettingsClick, modifier = Modifier.size(32.dp)) {
                    GearIcon(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // ── Avatar + user details ────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                // TODO: Ava/Srijan - load real image from user.profilePhotoUrl (e.g. Coil) when available
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(HappnInPurple),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = user.name.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(user.college, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(user.role.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // ── Action buttons ───────────────────────────────────────────────────────
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProfileActionButton("Create Event", HappnInPurple, Modifier.weight(1f), onCreateEventClick)
                ProfileActionButton("Add Friends", HappnInDark, Modifier.weight(1f), onAddFriendsClick)
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onLogOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(44.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            ) {
                Text("Log Out", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Registered events section ────────────────────────────────────────────
        item {
            Text(
                text = "Registered Events",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        if (myEvents.isEmpty()) {
            item {
                Text(
                    text = "No registered events yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        } else {
            items(items = myEvents, key = { it.id }) { event ->
                ProfileEventCard(
                    event = event,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { onEventClick(event) },
                )
            }
        }
    }
}

@Composable
private fun ProfileActionButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(44.dp)
            .background(color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

@Composable
private fun ProfileEventCard(event: Event, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(event.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("${event.startsAt.toDateLabel()} · ${event.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun GearIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val sw = 1.8.dp.toPx()
        val stroke = Stroke(sw, cap = StrokeCap.Round)
        val cx = size.width / 2f; val cy = size.height / 2f
        drawCircle(color, size.minDimension * 0.18f, Offset(cx, cy), style = stroke)
        drawCircle(color, size.minDimension * 0.28f, Offset(cx, cy), style = stroke)
        for (i in 0 until 8) {
            val angle = Math.toRadians(i * 45.0)
            val r1 = size.minDimension * 0.32f; val r2 = size.minDimension * 0.46f
            drawLine(color, Offset(cx + (r1 * kotlin.math.cos(angle)).toFloat(), cy + (r1 * kotlin.math.sin(angle)).toFloat()),
                Offset(cx + (r2 * kotlin.math.cos(angle)).toFloat(), cy + (r2 * kotlin.math.sin(angle)).toFloat()), sw, StrokeCap.Round)
        }
    }
}

private fun LocalDateTime.toDateLabel(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month $dayOfMonth"
}

@Preview(showBackground = true)
@Composable
private fun MyProfileScreenPreview() {
    HappnInTheme {
        MyProfileScreen(registrationViewModel = remember { RegistrationViewModel() })
    }
}
