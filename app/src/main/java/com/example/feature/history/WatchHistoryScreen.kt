package com.example.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.designsystem.AppTopBar
import com.example.core.designsystem.EmptyState
import com.example.data.repository.InteractionRepository
import com.example.domain.model.WatchHistoryItem
import com.example.ui.theme.BrandOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchHistoryScreen(
    interactionRepository: InteractionRepository,
    onBackClick: () -> Unit,
    onNavigateToVlogDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val historyItems by interactionRepository.watchHistory.collectAsState(initial = emptyList())
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.watch_history_title),
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (historyItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showClearDialog = true },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("clear_history_fab")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear History")
                }
            }
        },
        modifier = modifier.testTag("watch_history_screen")
    ) { innerPadding ->
        if (historyItems.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.empty_history),
                description = stringResource(R.string.empty_history_desc),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(historyItems, key = { it.contentId }) { item ->
                    WatchHistoryRow(
                        item = item,
                        onClick = { onNavigateToVlogDetail(item.contentId) }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear watch history?") },
            text = { Text("This will clear all your playback watch progress from this device.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            interactionRepository.clearWatchHistory()
                            showClearDialog = false
                        }
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WatchHistoryRow(
    item: WatchHistoryItem,
    onClick: () -> Unit
) {
    val progressFraction = if (item.totalDurationSeconds > 0) {
        (item.progressSeconds.toFloat() / item.totalDurationSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 110.dp, height = 75.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = item.thumbnailUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Progress bar at bottom of thumbnail
                LinearProgressIndicator(
                    progress = { progressFraction },
                    color = BrandOrange,
                    trackColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                val progressMins = item.progressSeconds / 60
                val progressSecs = item.progressSeconds % 60
                Text(
                    text = "Watched $progressMins:${String.format("%02d", progressSecs)} of ${item.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Resume",
                tint = BrandOrange,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
