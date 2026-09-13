package com.example.feature.shorts

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.designsystem.EmptyState
import com.example.core.designsystem.VideoPlayerView
import com.example.domain.model.ShortVideo
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandOrange

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(
    viewModel: ShortsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val shorts = uiState.shorts

    if (shorts.isEmpty()) {
        EmptyState(
            title = "No Shorts Available",
            description = "New shorts will appear here soon!",
            modifier = modifier.fillMaxSize()
        )
        return
    }

    val initialIndex = remember(shorts, uiState.initialShortId) {
        val found = shorts.indexOfFirst { it.id == uiState.initialShortId }
        if (found >= 0) found else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { shorts.size }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("shorts_screen")
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val short = shorts[pageIndex]
            val isCurrentPage = pagerState.currentPage == pageIndex
            val isLiked = uiState.likedShortIds.contains(short.id)

            Box(modifier = Modifier.fillMaxSize()) {
                // Video Player if currently active page, otherwise high res thumbnail preview
                if (isCurrentPage) {
                    VideoPlayerView(
                        videoUrl = short.videoUrl,
                        title = short.title,
                        modifier = Modifier.fillMaxSize(),
                        testTag = "short_player_${short.id}"
                    )
                } else {
                    AsyncImage(
                        model = short.thumbnailUrl,
                        contentDescription = short.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Vertical Gradient Scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Right Action Bar (Heart, Share, Audio mute)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 48.dp)
                ) {
                    // Like button
                    ShortActionButton(
                        icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        count = "${(short.likes + if (isLiked) 1 else 0) / 1000}K",
                        tint = if (isLiked) BrandOrange else Color.White,
                        onClick = { viewModel.toggleLike(short.id) },
                        testTag = "short_like_button_${short.id}"
                    )

                    // Share button
                    ShortActionButton(
                        icon = Icons.Default.Share,
                        count = "Share",
                        tint = Color.White,
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Watch this short by Hardik: ${short.title}\nhttps://youtube.com/shorts/${short.id}"
                                )
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Short"))
                        },
                        testTag = "short_share_button_${short.id}"
                    )

                    // Audio Mute toggle
                    ShortActionButton(
                        icon = if (uiState.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        count = if (uiState.isMuted) "Muted" else "Sound",
                        tint = Color.White,
                        onClick = { viewModel.toggleMute() },
                        testTag = "short_mute_button"
                    )
                }

                // Bottom Left Info Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(0.78f)
                        .padding(start = 16.dp, bottom = 48.dp)
                ) {
                    // Creator profile tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BrandOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "H",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = short.creatorHandle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = BrandAmber,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Caption
                    Text(
                        text = short.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Audio sound badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = short.soundTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShortActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "short_action_button"
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = count,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}
