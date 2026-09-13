package com.example.feature.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
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
import com.example.domain.model.BookmarkItem
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedVlogsScreen(
    interactionRepository: InteractionRepository,
    onBackClick: () -> Unit,
    onNavigateToVlogDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val bookmarks by interactionRepository.bookmarks.collectAsState(initial = emptyList())
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.saved_vlogs_title),
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (bookmarks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showClearDialog = true },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag("clear_bookmarks_fab")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All Bookmarks")
                }
            }
        },
        modifier = modifier.testTag("saved_vlogs_screen")
    ) { innerPadding ->
        if (bookmarks.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.empty_saved),
                description = stringResource(R.string.empty_saved_desc),
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
                items(bookmarks, key = { it.contentId }) { item ->
                    SavedVlogRow(
                        item = item,
                        onClick = { onNavigateToVlogDetail(item.contentId) },
                        onRemove = {
                            coroutineScope.launch {
                                interactionRepository.removeBookmark(item.contentId)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all saved vlogs?") },
            text = { Text("This will remove all videos from your saved bookmarks.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            interactionRepository.clearAllBookmarks()
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
fun SavedVlogRow(
    item: BookmarkItem,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
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
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = item.duration,
                        color = androidx.compose.ui.graphics.Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.labelSmall.copy(color = BrandAmber, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.BookmarkRemove,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
