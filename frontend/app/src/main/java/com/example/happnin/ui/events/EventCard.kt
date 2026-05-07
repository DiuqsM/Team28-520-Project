package com.example.happnin.ui.events

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happnin.data.Event
import com.example.happnin.data.EventStatus
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.registration.RegistrationButton
import com.example.happnin.ui.theme.HappnInDark
import com.example.happnin.ui.theme.HappnInPurple
import com.example.happnin.ui.theme.HappnInTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun EventCard(
    event: Event,
    modifier: Modifier = Modifier,
    isRegistered: Boolean = false,  // wired from RegistrationViewModel in MainActivity
    onSeeMoreClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(8.dp),
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            EventMetaRow(
                icon = { CalendarIcon(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onSurface) },
                value = event.toDateRangeLabel(),
            )
            EventMetaRow(
                icon = { MapPinIcon(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onSurface) },
                value = event.location,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EventActionButton(
                text = "See More",
                color = HappnInDark,
                onClick = onSeeMoreClick,
            )
            Spacer(modifier = Modifier.width(10.dp))
            // Registration flow hook — RegistrationButton handles Register ↔ Registered toggle
            when {
                isRegistered ->
                    RegistrationButton(isRegistered = true, onRegisterClick = {})
                event.status == EventStatus.OPEN ->
                    RegistrationButton(isRegistered = false, onRegisterClick = onRegisterClick)
                else ->
                    EventActionButton(text = event.status.label, color = HappnInDark, onClick = {})
            }
            Spacer(modifier = Modifier.width(10.dp))
            AvatarGroup(
                count = event.registrationCount,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EventMetaRow(
    icon: @Composable () -> Unit,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        icon()
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EventActionButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
) {
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
            maxLines = 1,
        )
    }
}

@Composable
private fun AvatarGroup(
    count: Int,
    modifier: Modifier = Modifier,
) {
    if (count <= 0) return

    val avatarColors = listOf(
        Color(0xFFE1A04C),
        Color(0xFFD7B7C6),
        Color(0xFFA9E56F),
    )
    val avatarLabels = listOf("A", "J", "M")
    val shownAvatars = minOf(count, 3)
    val overflow = count - shownAvatars

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(shownAvatars) { index ->
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(avatarColors[index], CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = avatarLabels[index],
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .height(21.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$overflow",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF757575),
                )
            }
        }
    }
}

@Composable
private fun CalendarIcon(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.8.dp.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(size.width * 0.16f, size.height * 0.20f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.68f, size.height * 0.64f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.8.dp.toPx()),
            style = Stroke(strokeWidth),
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.16f, size.height * 0.38f),
            end = Offset(size.width * 0.84f, size.height * 0.38f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(color, Offset(size.width * 0.32f, size.height * 0.12f), Offset(size.width * 0.32f, size.height * 0.28f), strokeWidth, StrokeCap.Round)
        drawLine(color, Offset(size.width * 0.68f, size.height * 0.12f), Offset(size.width * 0.68f, size.height * 0.28f), strokeWidth, StrokeCap.Round)
    }
}

@Composable
private fun MapPinIcon(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.8.dp.toPx()
        // Teardrop: arc from bottom-left, over the top, to bottom-right, then lines converging to a point
        val cx = size.width * 0.50f
        val cy = size.height * 0.38f
        val r = size.minDimension * 0.28f
        drawArc(
            color = color,
            startAngle = 30f,
            sweepAngle = 300f,
            useCenter = false,
            topLeft = Offset(cx - r, cy - r),
            size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
        )
        // Left side down to tip
        drawLine(
            color = color,
            start = Offset(cx - r * kotlin.math.cos(Math.toRadians(30.0)).toFloat(), cy + r * kotlin.math.sin(Math.toRadians(30.0)).toFloat()),
            end = Offset(cx, size.height * 0.88f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        // Right side down to tip
        drawLine(
            color = color,
            start = Offset(cx + r * kotlin.math.cos(Math.toRadians(30.0)).toFloat(), cy + r * kotlin.math.sin(Math.toRadians(30.0)).toFloat()),
            end = Offset(cx, size.height * 0.88f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        // Inner dot
        drawCircle(
            color = color,
            radius = size.minDimension * 0.07f,
            center = Offset(cx, cy),
        )
    }
}

private fun Event.toDateRangeLabel(): String {
    return "${startsAt.toDateLabel()}, ${startsAt.toTimeLabel()}-${endsAt.toTimeLabel()}"
}

private fun LocalDateTime.toDateLabel(): String {
    val month = month.name.lowercase().replaceFirstChar { it.uppercase() }
    return "$month $dayOfMonth"
}

private fun LocalDateTime.toTimeLabel(): String {
    val suffix = if (hour >= 12) "pm" else "am"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minutePadded = minute.toString().padStart(2, '0')
    return "$hour12:$minutePadded$suffix"
}

@Preview(showBackground = true)
@Composable
private fun EventCardPreview() {
    HappnInTheme {
        Column(
            modifier = Modifier
                .width(312.dp)
                .padding(16.dp),
        ) {
            EventCard(event = FakeEventRepository.events.first())
            Spacer(modifier = Modifier.height(12.dp))
            EventCard(event = FakeEventRepository.events.last())
        }
    }
}
