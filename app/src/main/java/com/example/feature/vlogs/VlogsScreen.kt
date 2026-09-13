package com.example.feature.vlogs

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.*
import com.example.domain.model.VlogSortOrder
import com.example.ui.theme.BrandOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VlogsScreen(
    viewModel: VlogsViewModel,
    onNavigateToVlogDetail: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.browse_vlogs),
                onSearchClick = onNavigateToSearch
            )
        },
        modifier = modifier.testTag("vlogs_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // In-page search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_vlogs_hint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("vlogs_search_input")
            )

            // Category Chips Row with Sort Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                // Sort Dropdown Button
                Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                    Surface(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.height(38.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = BrandOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.sortOrder == VlogSortOrder.NEWEST) "Newest" else "Popular",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest Uploads") },
                            leadingIcon = {
                                if (uiState.sortOrder == VlogSortOrder.NEWEST) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandOrange)
                                }
                            },
                            onClick = {
                                viewModel.setSortOrder(VlogSortOrder.NEWEST)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Most Popular") },
                            leadingIcon = {
                                if (uiState.sortOrder == VlogSortOrder.POPULAR) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = BrandOrange)
                                }
                            },
                            onClick = {
                                viewModel.setSortOrder(VlogSortOrder.POPULAR)
                                showSortMenu = false
                            }
                        )
                    }
                }

                // Horizontal scrollable categories
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(end = 16.dp)
                ) {
                    uiState.categories.forEach { category ->
                        CategoryChip(
                            name = category.name,
                            isSelected = uiState.selectedCategoryId == category.id,
                            onClick = { viewModel.selectCategory(category.id) },
                            count = if (category.count > 0) category.count else null,
                            testTag = "category_chip_${category.id}"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Vlogs List
            if (uiState.isLoading) {
                LoadingShimmer()
            } else if (uiState.filteredVlogs.isEmpty()) {
                EmptyState(
                    title = stringResource(R.string.no_vlogs_found),
                    description = stringResource(R.string.no_vlogs_description),
                    actionText = "Show All Vlogs",
                    onActionClick = {
                        viewModel.selectCategory("cat_all")
                        viewModel.updateSearchQuery("")
                    },
                    testTag = "vlogs_empty_state"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredVlogs, key = { it.id }) { vlog ->
                        val isBookmarked = uiState.bookmarkedVlogIds.contains(vlog.id)
                        VlogCard(
                            vlog = vlog,
                            isBookmarked = isBookmarked,
                            onBookmarkToggle = { viewModel.toggleBookmark(vlog) },
                            onClick = { onNavigateToVlogDetail(vlog.id) },
                            testTag = "vlog_item_${vlog.id}"
                        )
                    }
                }
            }
        }
    }
}
