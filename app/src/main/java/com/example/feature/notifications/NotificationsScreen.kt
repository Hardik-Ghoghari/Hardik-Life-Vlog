package com.example.feature.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.designsystem.AppTopBar
import com.example.core.designsystem.EmptyState
import com.example.data.repository.ContentRepository
import com.example.domain.model.AppNotification
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    contentRepository: ContentRepository,
    onBackClick: () -> Unit,
    onNavigateToVlogDetail: (String) -> Unit,
    onNavigateToShorts: (String?) -> Unit,
    onNavigateToGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by contentRepository.notifications.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.notifications_title),
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (notifications.any { !it.isRead }) {
                ExtendedFloatingActionButton(
                    onClick = { contentRepository.markAllNotificationsAsRead() },
                    icon = { Icon(Icons.Default.DoneAll, contentDescription = null) },
                    text = { Text("Mark all read") },
                    containerColor = BrandOrange,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.testTag("mark_all_read_fab")
                )
            }
        },
        modifier = modifier.testTag("notifications_screen")
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            EmptyState(
                title = "No Notifications",
                description = "You are all caught up with Hardik's updates!",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notification = notif,
                        onClick = {
                            contentRepository.markNotificationAsRead(notif.id)
                            when (notif.targetType) {
                                "vlog" -> if (notif.targetId.isNotEmpty()) onNavigateToVlogDetail(notif.targetId)
                                "short" -> onNavigateToShorts(notif.targetId.ifEmpty { null })
                                "gallery" -> onNavigateToGallery()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (!notification.isRead) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface,
        border = if (!notification.isRead)
            androidx.compose.foundation.BorderStroke(1.dp, BrandOrange.copy(alpha = 0.4f))
        else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.targetType) {
                            "vlog" -> BrandOrange.copy(alpha = 0.2f)
                            "short" -> BrandAmber.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.targetType) {
                        "vlog" -> Icons.Default.PlayCircle
                        "short" -> Icons.Default.FlashOn
                        "gallery" -> Icons.Default.PhotoCamera
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(BrandOrange)
                )
            }
        }
    }
}
