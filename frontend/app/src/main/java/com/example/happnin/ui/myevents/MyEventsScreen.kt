package com.example.happnin.ui.myevents

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happnin.data.Event
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.registration.RegistrationViewModel
import com.example.happnin.ui.theme.HappnInDark
import com.example.happnin.ui.theme.HappnInPurple
import com.example.happnin.ui.theme.HappnInTheme
import kotlinx.datetime.LocalDateTime

// Teammate hook: pass real navigation lambdas from MainActivity when
// Event Detail (Sami) and Chat (Sami) screens are ready.
@Composable
fun MyEventsScreen(
    registrationViewModel: RegistrationViewModel,
    events: List<Event> = FakeEventRepository.events,
    modifier: Modifier = Modifier,
    onSeeMoreClick: (Event) -> Unit = {},
    onChatClick: (Event) -> Unit = {},
) {
    val registeredIds by registrationViewModel.registeredEventIds.collectAsState()

    // Derive the list of events the user has registered for
    val myEvents = remember(events, registeredIds) {
        events.filter { it.id in registeredIds }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // ── Header ──────────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "My Events",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            // TODO: implement filter/sort for My Events
            IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
                HamburgerIcon(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // ── Event list or empty state ────────────────────────────────────────────
        if (myEvents.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No registered events yet.\nHead to Explore to find one!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(items = myEvents, key = { it.id }) { event ->
                    MyEventCard(
                        event = event,
                        onSeeMoreClick = { onSeeMoreClick(event) },
                        onChatClick = { onChatClick(event) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MyEventCard(
    event: Event,
    modifier: Modifier = Modifier,
    onSeeMoreClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = event.toDateRangeLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            MyEventActionButton(text = "See More", color = HappnInDark, onClick = onSeeMoreClick)
            Spacer(modifier = Modifier.width(10.dp))
            // TODO: Sami - connect Chat navigation here
            MyEventActionButton(text = "Chat", color = HappnInPurple, onClick = onChatClick)
        }
    }
}

@Composable
private fun MyEventActionButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(30.dp)
            .background(color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

@Composable
private fun HamburgerIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val sw = 2.dp.toPx()
        listOf(0.28f, 0.50f, 0.72f).forEach { yFrac ->
            drawLine(color, Offset(size.width * 0.16f, size.height * yFrac),
                Offset(size.width * 0.84f, size.height * yFrac), sw, StrokeCap.Round)
        }
    }
}

// TODO: centralise with EventCard.kt date helpers into a shared DateUtils.kt
private fun Event.toDateRangeLabel() =
    "${startsAt.toDateLabel()}, ${startsAt.toTimeLabel()}–${endsAt.toTimeLabel()}"

private fun LocalDateTime.toDateLabel(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month $dayOfMonth"
}

private fun LocalDateTime.toTimeLabel(): String {
    val suffix = if (hour >= 12) "pm" else "am"
    val hour12 = when { hour == 0 -> 12; hour > 12 -> hour - 12; else -> hour }
    return "$hour12:${minute.toString().padStart(2, '0')}$suffix"
}

@Preview(showBackground = true)
@Composable
private fun MyEventsScreenPreview() {
    HappnInTheme {
        MyEventsScreen(registrationViewModel = remember { RegistrationViewModel() })
    }
}
