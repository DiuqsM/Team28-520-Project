package com.example.happnin.ui.events

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import com.example.happnin.data.Event
import com.example.happnin.data.FakeEventRepository
import com.example.happnin.ui.theme.HappnInTheme

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    modifier: Modifier = Modifier,
    onEventClick: (Event) -> Unit = {},
    registeredEventIds: Set<String> = emptySet(),
    onRegisterClick: (Event) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    var isSearchOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isFilterOpen by rememberSaveable { mutableStateOf(false) }
    var isSortOpen by rememberSaveable { mutableStateOf(false) }
    var expandedFilter by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ExploreHeader(
            onSearchClick = { isSearchOpen = !isSearchOpen },
        )
        if (isSearchOpen) {
            SearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
            )
        }
        EventsLabel(
            onFilterClick = {
                isFilterOpen = !isFilterOpen
                if (isFilterOpen) isSortOpen = false
            },
            onSortClick = {
                isSortOpen = !isSortOpen
                if (isSortOpen) isFilterOpen = false
            },
        )
        if (isFilterOpen) {
            FilterPanel(
                expandedFilter = expandedFilter,
                onFilterClick = { filter ->
                    expandedFilter = if (expandedFilter == filter) null else filter
                },
            )
        }
        if (isSortOpen) {
            OptionPanel(
                title = "Sort",
                items = listOf("Soonest", "Most popular", "Closest"),
            )
        }

        when (uiState) {
            is EventsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EventsUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
            is EventsUiState.Success -> {
                val events = uiState.events
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (events.isEmpty()) {
                        item {
                            Text(
                                text = "No events nearby.",
                                modifier = Modifier.padding(vertical = 24.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    } else {
                        items(
                            items = events,
                            key = { event -> event.id },
                        ) { event ->
                            EventCard(
                                event = event,
                                isRegistered = registeredEventIds.contains(event.id),
                                onSeeMoreClick = { onEventClick(event) },
                                onRegisterClick = { onRegisterClick(event) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExploreHeader(
    onSearchClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Explore",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier.size(32.dp),
        ) {
            SearchIcon(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = {
            Text(text = "Search events")
        },
    )
}

@Composable
private fun EventsLabel(
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Events Nearby",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(32.dp),
            ) {
                FilterIcon(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onSortClick,
                modifier = Modifier.size(32.dp),
            ) {
                LayersIcon(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun FilterPanel(
    expandedFilter: String?,
    onFilterClick: (String) -> Unit,
) {
    OptionPanel(
        title = "Filter",
        items = listOf("College", "Date", "Free events"),
        onItemClick = onFilterClick,
    )

    when (expandedFilter) {
        "College" -> OptionPanel(
            title = "College",
            items = listOf("UMass Amherst", "Amherst College", "Smith College", "Mount Holyoke", "Hampshire College"),
        )

        "Date" -> OptionPanel(
            title = "Date",
            items = listOf("Today", "This week", "Weekend", "Upcoming"),
        )
    }
}

@Composable
private fun OptionPanel(
    title: String,
    items: List<String>,
    onItemClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(8.dp),
            )
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable { onItemClick(item) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchIcon(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
        drawCircle(
            color = color,
            radius = size.minDimension * 0.29f,
            center = Offset(size.width * 0.44f, size.height * 0.44f),
            style = stroke,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.64f, size.height * 0.64f),
            end = Offset(size.width * 0.86f, size.height * 0.86f),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun FilterIcon(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * 0.16f, size.height * 0.22f)
            lineTo(size.width * 0.84f, size.height * 0.22f)
            lineTo(size.width * 0.58f, size.height * 0.52f)
            lineTo(size.width * 0.58f, size.height * 0.82f)
            lineTo(size.width * 0.42f, size.height * 0.74f)
            lineTo(size.width * 0.42f, size.height * 0.52f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun LayersIcon(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        fun drawLayer(y: Float) {
            val left = Offset(size.width * 0.18f, y)
            val top = Offset(size.width * 0.50f, y - size.height * 0.14f)
            val right = Offset(size.width * 0.82f, y)
            val bottom = Offset(size.width * 0.50f, y + size.height * 0.14f)
            drawLine(color, left, top, strokeWidth, StrokeCap.Round)
            drawLine(color, top, right, strokeWidth, StrokeCap.Round)
            drawLine(color, right, bottom, strokeWidth, StrokeCap.Round)
            drawLine(color, bottom, left, strokeWidth, StrokeCap.Round)
        }
        drawLayer(size.height * 0.34f)
        drawLayer(size.height * 0.50f)
        drawLayer(size.height * 0.66f)
    }
}

@Preview(showBackground = true)
@Composable
private fun EventsScreenPreview() {
    HappnInTheme {
        EventsScreen(uiState = EventsUiState.Success(FakeEventRepository.events))
    }
}
