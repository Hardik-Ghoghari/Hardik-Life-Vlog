package com.example.feature.vlogdetail

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.*
import com.example.domain.model.Vlog
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VlogDetailScreen(
    viewModel: VlogDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToVlogDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val vlog = uiState.vlog
    var showReportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = vlog?.categoryName ?: "Vlog",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        modifier = modifier.testTag("vlog_detail_screen")
    ) { innerPadding ->
        if (vlog == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. Video Player with Media3 ExoPlayer
                item {
                    VideoPlayerView(
                        videoUrl = vlog.videoUrl,
                        title = vlog.title,
                        initialProgressSeconds = uiState.initialProgressSeconds,
                        onProgressUpdate = { cur, total ->
                            viewModel.updateWatchProgress(cur, total)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    )
                }

                // 2. Title & Stats
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = vlog.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                lineHeight = 26.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandAmber.copy(alpha = 0.15f),
                                contentColor = BrandAmber
                            ) {
                                Text(
                                    text = vlog.categoryName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${vlog.views / 1000}K views",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = vlog.publishedAt,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3. Action Buttons Row (Like, Bookmark, Share, YouTube, Report)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like Button
                        VlogActionButton(
                            icon = if (uiState.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            label = "${vlog.likes / 1000}K",
                            isHighlighted = uiState.isLiked,
                            highlightColor = BrandOrange,
                            onClick = { viewModel.toggleLike() },
                            testTag = "detail_like_button"
                        )

                        // Bookmark / Save
                        VlogActionButton(
                            icon = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            label = if (uiState.isBookmarked) stringResource(R.string.saved_button) else stringResource(R.string.save_button),
                            isHighlighted = uiState.isBookmarked,
                            highlightColor = BrandAmber,
                            onClick = { viewModel.toggleBookmark() },
                            testTag = "detail_save_button"
                        )

                        // Share
                        VlogActionButton(
                            icon = Icons.Default.Share,
                            label = stringResource(R.string.share_button),
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Watch Hardik's latest vlog: ${vlog.title}\nhardiklifevlog://vlog/${vlog.id}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Vlog"))
                            },
                            testTag = "detail_share_button"
                        )

                        // Open in YouTube
                        VlogActionButton(
                            icon = Icons.Default.PlayCircle,
                            label = "YouTube",
                            highlightColor = Color(0xFFFF0000),
                            onClick = {
                                try {
                                    val ytIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.youtube.com/@hardiklifevlog")
                                    )
                                    context.startActivity(ytIntent)
                                } catch (e: Exception) {
                                    // Ignored
                                }
                            },
                            testTag = "detail_youtube_button"
                        )

                        // Report Content
                        VlogActionButton(
                            icon = Icons.Outlined.Flag,
                            label = "Report",
                            onClick = { showReportDialog = true },
                            testTag = "detail_report_button"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 4. Creator Profile Badge (Quick Subscribe/Follow)
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrandOrange),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "H",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hardik",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "250K Subscribers • Official Vlogger",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://www.youtube.com/@hardiklifevlog?sub_confirmation=1")
                                        )
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Ignore
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Follow",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 5. Expandable Description
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { viewModel.toggleDescriptionExpanded() }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "About this vlog",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = vlog.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = if (uiState.isDescriptionExpanded) Int.MAX_VALUE else 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (uiState.isDescriptionExpanded) "Show Less" else "Read More...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 6. Related Vlogs Section
                if (uiState.relatedVlogs.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = stringResource(R.string.related_vlogs),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    items(uiState.relatedVlogs, key = { "rel_${it.id}" }) { relVlog ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            VlogCard(
                                vlog = relVlog,
                                onClick = { onNavigateToVlogDetail(relVlog.id) },
                                testTag = "related_vlog_${relVlog.id}"
                            )
                        }
                    }
                }
            }
        }
    }

    // Report Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Content", fontWeight = FontWeight.Bold) },
            text = {
                Text("Would you like to report this vlog for inappropriate content or copyright concerns? Our team reviews all reports within 24 hours.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.reportContent()
                        showReportDialog = false
                    }
                ) {
                    Text("Submit Report", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.isReported) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {}
        ) {
            Text(stringResource(R.string.report_submitted))
        }
    }
}

@Composable
fun VlogActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    highlightColor: Color = BrandOrange,
    testTag: String = "action_button"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isHighlighted) highlightColor.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isHighlighted) highlightColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isHighlighted) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
