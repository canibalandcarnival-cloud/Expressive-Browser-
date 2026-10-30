package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookmarkItem
import com.example.model.FavoriteItem
import com.example.model.HistoryItem
import com.example.model.ReadingItem
import com.example.model.ThemeMode
import com.example.model.TopNavSegment
import com.example.ui.theme.LocalExpressiveTokens
import com.example.ui.theme.LocalInteractionFeedback
import com.example.viewmodel.BrowserUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StartPageView(
    uiState: BrowserUiState,
    onSegmentSelected: (TopNavSegment) -> Unit,
    onFavoriteClick: (FavoriteItem) -> Unit,
    onReadingItemClick: (ReadingItem) -> Unit,
    onToggleReadingItemRead: (ReadingItem) -> Unit,
    onBookmarkClick: (BookmarkItem) -> Unit,
    onHistoryItemClick: (HistoryItem) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onTabsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onToggleThemeMode: () -> Unit,
    onShowAllFavorites: () -> Unit,
    onAddFavorite: (String, String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val feedback = LocalInteractionFeedback.current
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var showAddDialog by remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    // Smooth fluid animation when solid wallpaper color changes
    val animatedBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = tokens.solidWallpaperBackground,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 350),
        label = "wallpaper_bg_anim"
    )

    // SOLID STARTUP WALLPAPER following phone wallpaper & dynamic color selection
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBgColor)
    ) {
        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarPadding + 10.dp)
        ) {
            // Top Bar with Solid Dark Pill Segment Bar + Light/Dark toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Segment Bar
                TopSegmentBar(
                    selectedSegment = uiState.selectedSegment,
                    onSegmentSelected = onSegmentSelected,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("top_segment_bar")
                )

                // Quick Light/Dark Theme toggle pill button
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(tokens.pillBackground)
                        .border(1.dp, tokens.pillBorder, CircleShape)
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = onToggleThemeMode
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (uiState.themeMode == ThemeMode.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Toggle Light/Dark Theme",
                        tint = tokens.accentLilac,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body Area animated between Segments with smooth elastic scroll
            AnimatedContent(
                targetState = uiState.selectedSegment,
                transitionSpec = { fadeIn(ExpressiveBouncySpring) togetherWith fadeOut() },
                label = "segment_content_transition",
                modifier = Modifier.weight(1f)
            ) { segment ->
                when (segment) {
                    TopNavSegment.FAVORITES -> {
                        LazyColumn(
                            state = scrollState,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(22.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                FavoriteGrid(
                                    favorites = uiState.favorites,
                                    onFavoriteClick = onFavoriteClick,
                                    onShowAllClick = onShowAllFavorites,
                                    onAddFavoriteClick = { showAddDialog = true }
                                )
                            }

                            item {
                                ReadingListSection(
                                    readingItems = uiState.readingList,
                                    onItemClick = onReadingItemClick,
                                    onShowAllClick = { onSegmentSelected(TopNavSegment.READING_LIST) },
                                    onToggleRead = onToggleReadingItemRead
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(96.dp)) // Safe padding for bottom Omnibar
                            }
                        }
                    }

                    TopNavSegment.BOOKMARKS -> {
                        BookmarksView(
                            bookmarks = uiState.bookmarks,
                            onBookmarkClick = onBookmarkClick
                        )
                    }

                    TopNavSegment.READING_LIST -> {
                        FullReadingListView(
                            items = uiState.readingList,
                            onItemClick = onReadingItemClick,
                            onToggleRead = onToggleReadingItemRead
                        )
                    }

                    TopNavSegment.HISTORY -> {
                        HistoryListView(
                            history = uiState.history,
                            onItemClick = onHistoryItemClick,
                            onClearHistory = onClearHistory
                        )
                    }
                }
            }
        }

        // Floating Bottom Omnibar Docked at Screen Bottom (Solid Dark M3 Expressive, no highlights)
        FloatingOmnibar(
            urlQuery = uiState.searchQuery,
            onQueryChange = onQueryChange,
            onSubmit = onSearchSubmit,
            canGoBack = false,
            onBackClick = { /* No back on start page */ },
            tabCount = uiState.tabs.size,
            onTabsClick = onTabsClick,
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .testTag("floating_omnibar")
        )
    }

    if (showAddDialog) {
        AddFavoriteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url ->
                onAddFavorite(title, url)
                feedback.playSuccess()
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BookmarksView(
    bookmarks: List<BookmarkItem>,
    onBookmarkClick: (BookmarkItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Bookmarks (${bookmarks.size})",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = tokens.sectionTitleColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            items(bookmarks, key = { it.id }) { bm ->
                Surface(
                    color = tokens.cardSurface, // Solid Dark M3 surface
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 3.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, tokens.cardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bouncyClickable(
                            soundType = FeedbackSoundType.POP,
                            onClick = { onBookmarkClick(bm) }
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = bm.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.onCardTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = bm.url,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.onCardMutedColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullReadingListView(
    items: List<ReadingItem>,
    onItemClick: (ReadingItem) -> Unit,
    onToggleRead: (ReadingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Saved Articles (${items.size})",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = tokens.sectionTitleColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            items(items, key = { it.id }) { item ->
                ReadingListItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    onToggleRead = { onToggleRead(item) }
                )
            }
        }
    }
}

@Composable
fun HistoryListView(
    history: List<HistoryItem>,
    onItemClick: (HistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Browsing History",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = tokens.sectionTitleColor
            )

            if (history.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear All", color = tokens.actionPillColor)
                }
            }
        }

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = tokens.onCardMutedColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No history yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.onCardMutedColor
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 110.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    Surface(
                        color = tokens.cardSurface, // Solid Dark M3 surface
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, tokens.cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .bouncyClickable(
                                soundType = FeedbackSoundType.CLICK,
                                onClick = { onItemClick(item) }
                            )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.onCardTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.domain,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.accentLilac
                                )
                                Text(
                                    text = dateFormat.format(Date(item.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.onCardMutedColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddFavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String) -> Unit
) {
    val tokens = LocalExpressiveTokens.current
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Favorite", fontWeight = FontWeight.Bold, color = tokens.onCardTextColor)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", color = tokens.onCardMutedColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = tokens.cardSurface,
                        unfocusedContainerColor = tokens.cardSurface,
                        focusedTextColor = tokens.onCardTextColor,
                        unfocusedTextColor = tokens.onCardTextColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL (e.g. wikipedia.org)", color = tokens.onCardMutedColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = tokens.cardSurface,
                        unfocusedContainerColor = tokens.cardSurface,
                        focusedTextColor = tokens.onCardTextColor,
                        unfocusedTextColor = tokens.onCardTextColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (url.isNotBlank()) onConfirm(title.ifBlank { url }, url) },
                enabled = url.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.actionPillColor,
                    contentColor = tokens.onActionPillColor
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = tokens.onCardMutedColor)
            }
        },
        containerColor = tokens.cardSurface,
        shape = RoundedCornerShape(24.dp)
    )
}
