package com.example.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.*
import com.example.domain.model.Vlog
import com.example.ui.theme.BrandOrange

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToVlogDetail: (String) -> Unit,
    onNavigateToShorts: (String?) -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToVlogsList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                onSearchClick = onNavigateToSearch,
                onNotificationClick = onNavigateToNotifications,
                unreadNotificationCount = uiState.unreadNotificationsCount
            )
        },
        modifier = modifier.testTag("home_screen")
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingShimmer(modifier = Modifier.padding(innerPadding))
        } else if (uiState.errorMessage != null && uiState.featuredVlog == null) {
            ErrorState(
                message = uiState.errorMessage!!,
                onRetry = { viewModel.loadHomeData() },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 1. Featured Vlog Hero Banner
                uiState.featuredVlog?.let { featured ->
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            SectionHeader(
                                title = stringResource(R.string.featured_vlog),
                                onSeeAllClick = null
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FeaturedVlogCard(
                                vlog = featured,
                                onWatchClick = { onNavigateToVlogDetail(featured.id) },
                                testTag = "home_featured_card"
                            )
                        }
                    }
                }

                // 2. Latest Vlogs Carousel
                if (uiState.latestVlogs.isNotEmpty()) {
                    item {
                        Column {
                            SectionHeader(
                                title = stringResource(R.string.latest_vlogs),
                                onSeeAllClick = onNavigateToVlogsList,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(uiState.latestVlogs.take(6), key = { it.id }) { vlog ->
                                    Box(modifier = Modifier.width(280.dp)) {
                                        VlogCard(
                                            vlog = vlog,
                                            onClick = { onNavigateToVlogDetail(vlog.id) },
                                            testTag = "home_latest_vlog_${vlog.id}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Latest Shorts Carousel
                if (uiState.shorts.isNotEmpty()) {
                    item {
                        Column {
                            SectionHeader(
                                title = stringResource(R.string.latest_shorts),
                                onSeeAllClick = { onNavigateToShorts(null) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.shorts, key = { it.id }) { short ->
                                    ShortCard(
                                        short = short,
                                        onClick = { onNavigateToShorts(short.id) },
                                        testTag = "home_short_${short.id}"
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Popular Vlogs Vertical Cards
                if (uiState.popularVlogs.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = stringResource(R.string.popular_vlogs),
                            onSeeAllClick = onNavigateToVlogsList,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    items(uiState.popularVlogs.take(3), key = { "pop_${it.id}" }) { vlog ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            VlogCard(
                                vlog = vlog,
                                onClick = { onNavigateToVlogDetail(vlog.id) },
                                testTag = "home_popular_vlog_${vlog.id}"
                            )
                        }
                    }
                }

                // 5. Latest Photos Preview
                if (uiState.photos.isNotEmpty()) {
                    item {
                        Column {
                            SectionHeader(
                                title = stringResource(R.string.latest_photos),
                                onSeeAllClick = onNavigateToGallery,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(uiState.photos.take(5), key = { it.id }) { photo ->
                                    Box(modifier = Modifier.width(200.dp)) {
                                        PhotoCard(
                                            photo = photo,
                                            onClick = onNavigateToGallery,
                                            testTag = "home_photo_${photo.id}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Connect with Hardik (Social Links)
                if (uiState.socialLinks.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            SectionHeader(
                                title = stringResource(R.string.follow_hardik),
                                onSeeAllClick = null
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                uiState.socialLinks.chunked(2).forEach { rowLinks ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowLinks.forEach { link ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                SocialButton(
                                                    socialLink = link,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                        if (rowLinks.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (onSeeAllClick != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.view_all),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BrandOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
