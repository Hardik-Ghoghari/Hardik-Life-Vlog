package com.example.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.BrandOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onNavigateToVlogDetail: (String) -> Unit,
    onNavigateToShorts: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("All", "Vlogs", "Shorts", "Photos")

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.search_title),
                showBack = true,
                onBackClick = onBackClick
            )
        },
        modifier = modifier.testTag("search_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input
            OutlinedTextField(
                value = uiState.query,
                onValueChange = { viewModel.updateQuery(it) },
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = BrandOrange
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_input_field")
            )

            // Result Type Tabs
            if (uiState.query.isNotBlank()) {
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = BrandOrange
                ) {
                    tabs.forEachIndexed { index, tabName ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            text = {
                                Text(
                                    text = tabName,
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // Body
            if (uiState.query.isBlank()) {
                // Recent searches
                Column(modifier = Modifier.padding(16.dp)) {
                    if (uiState.recentSearches.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.recent_searches),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                Text(stringResource(R.string.clear_history), color = BrandOrange)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            uiState.recentSearches.forEach { term ->
                                Surface(
                                    onClick = { viewModel.selectRecentSearch(term) },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = term,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Search Results
                val hasResults = uiState.matchedVlogs.isNotEmpty() ||
                        uiState.matchedShorts.isNotEmpty() ||
                        uiState.matchedPhotos.isNotEmpty()

                if (!hasResults) {
                    EmptyState(
                        title = "No results for \"${uiState.query}\"",
                        description = stringResource(R.string.no_results_tip),
                        actionText = "Clear Search",
                        onActionClick = { viewModel.updateQuery("") }
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Vlogs Results
                        if (uiState.selectedTab == 0 || uiState.selectedTab == 1) {
                            if (uiState.matchedVlogs.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Vlogs (${uiState.matchedVlogs.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                items(uiState.matchedVlogs, key = { "sv_${it.id}" }) { vlog ->
                                    VlogCard(
                                        vlog = vlog,
                                        onClick = { onNavigateToVlogDetail(vlog.id) },
                                        testTag = "search_vlog_${vlog.id}"
                                    )
                                }
                            }
                        }

                        // Shorts Results
                        if (uiState.selectedTab == 0 || uiState.selectedTab == 2) {
                            if (uiState.matchedShorts.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Shorts (${uiState.matchedShorts.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                items(uiState.matchedShorts, key = { "ss_${it.id}" }) { short ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onNavigateToShorts(short.id) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FlashOn,
                                                contentDescription = null,
                                                tint = BrandOrange
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = short.title,
                                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${short.views / 1000}K views • ${short.soundTitle}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Photos Results
                        if (uiState.selectedTab == 0 || uiState.selectedTab == 3) {
                            if (uiState.matchedPhotos.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Photos (${uiState.matchedPhotos.size})",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                items(uiState.matchedPhotos, key = { "sp_${it.id}" }) { photo ->
                                    PhotoCard(
                                        photo = photo,
                                        onClick = {},
                                        testTag = "search_photo_${photo.id}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
