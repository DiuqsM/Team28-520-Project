package com.example.happnin.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happnin.data.Event
import com.example.happnin.data.EventStatus
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.registration.RegistrationButton
import com.example.happnin.ui.theme.HappnInTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    isRegistered: Boolean = false,
    onBack: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = event.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            StatusBadge(status = event.status)

            DetailSection(label = "Date & Time", value = event.toDateRangeLabel())
            DetailSection(label = "Location", value = event.location)

            if (event.college.isNotBlank()) {
                DetailSection(label = "College", value = event.college)
            }

            if (event.description.isNotBlank()) {
                HorizontalDivider()
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                val priceLabel = if (event.price == 0.0) "Free" else "$%.2f".format(event.price)
                DetailSection(label = "Price", value = priceLabel)
                if (event.ageLimit != null) {
                    DetailSection(label = "Age Limit", value = "${event.ageLimit}+")
                }
            }

            if (event.capacity != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    DetailSection(label = "Capacity", value = "${event.capacity}")
                    DetailSection(label = "Registered", value = "${event.registrationCount}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isRegistered -> RegistrationButton(isRegistered = true, onRegisterClick = {})
                event.status == EventStatus.OPEN -> RegistrationButton(
                    isRegistered = false,
                    onRegisterClick = onRegisterClick,
                )
                else -> Text(
                    text = event.status.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusBadge(status: EventStatus) {
    val (bgColor, textColor) = when (status) {
        EventStatus.OPEN -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        EventStatus.FULL -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        EventStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    Text(
        text = status.label,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
    )
}

@Composable
private fun DetailSection(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailScreenPreview() {
    HappnInTheme {
        EventDetailScreen(event = FakeEventRepository.events.first())
    }
}
