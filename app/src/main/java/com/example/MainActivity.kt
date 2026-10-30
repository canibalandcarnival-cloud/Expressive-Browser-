package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.audio.InteractionFeedback
import com.example.model.FavoriteItem
import com.example.ui.components.AddFavoriteDialog
import com.example.ui.components.FavoriteTile
import com.example.ui.components.OnboardingLoginFlow
import com.example.ui.components.StartPageView
import com.example.ui.components.TabSwitcherView
import com.example.ui.components.ThemeAndSettingsSheet
import com.example.ui.components.WebViewContainer
import com.example.ui.theme.ExpressiveBrowserTheme
import com.example.ui.theme.LocalExpressiveTokens
import com.example.viewmodel.BrowserViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BrowserViewModel by viewModels()
    private lateinit var interactionFeedback: InteractionFeedback

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        interactionFeedback = InteractionFeedback(this)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(uiState.sfxEnabled) {
                interactionFeedback.sfxEnabled = uiState.sfxEnabled
            }
            LaunchedEffect(uiState.hapticsEnabled) {
                interactionFeedback.hapticsEnabled = uiState.hapticsEnabled
            }

            ExpressiveBrowserTheme(
                themeMode = uiState.themeMode,
                solidWallpaperColor = uiState.solidWallpaperColor,
                interactionFeedback = interactionFeedback
            ) {
                val themeSettingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val activeTab = viewModel.getActiveTab()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (!uiState.isOnboardingCompleted) {
                            // First time launch: Arc-style Login Flow with opening animation
                            OnboardingLoginFlow(
                                onComplete = { email ->
                                    viewModel.completeOnboarding(email)
                                },
                                onSkip = {
                                    viewModel.skipOnboarding()
                                }
                            )
                        } else {
                            // Main Browser Experience: Start Page vs Web View
                            AnimatedContent(
                                targetState = activeTab != null && activeTab.url.isNotBlank(),
                                transitionSpec = {
                                    if (targetState) {
                                        (slideInVertically(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioLowBouncy,
                                                stiffness = Spring.StiffnessMediumLow
                                            ),
                                            initialOffsetY = { it / 3 }
                                        ) + fadeIn()).togetherWith(fadeOut())
                                    } else {
                                        fadeIn().togetherWith(
                                            slideOutVertically(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                ),
                                                targetOffsetY = { it / 3 }
                                            ) + fadeOut()
                                        )
                                    }
                                },
                                label = "main_screen_transition",
                                modifier = Modifier.fillMaxSize()
                            ) { isWebViewActive ->
                                if (isWebViewActive && activeTab != null) {
                                    WebViewContainer(
                                        tab = activeTab,
                                        isDesktopMode = uiState.isDesktopMode,
                                        isBookmarked = viewModel.isCurrentTabBookmarked(),
                                        totalTabCount = uiState.tabs.size,
                                        onUrlChanged = { newUrl ->
                                            viewModel.updateTabDetails(url = newUrl, title = activeTab.title)
                                        },
                                        onTitleChanged = { newTitle ->
                                            viewModel.updateTabDetails(url = activeTab.url, title = newTitle)
                                        },
                                        onProgressChanged = { progress, isLoading ->
                                            viewModel.updateTabProgress(progress, isLoading)
                                        },
                                        onBackStateChanged = { canBack, canForward ->
                                            viewModel.updateBackForwardState(canBack, canForward)
                                        },
                                        onToggleBookmark = {
                                            viewModel.toggleCurrentBookmark()
                                            interactionFeedback.playSuccess()
                                        },
                                        onHomeClick = {
                                            viewModel.goHome()
                                        },
                                        onTabsClick = {
                                            viewModel.showTabSwitcher()
                                        },
                                        onSearchAgainClick = {
                                            viewModel.goHome()
                                        }
                                    )
                                } else {
                                    StartPageView(
                                        uiState = uiState,
                                        onSegmentSelected = { segment ->
                                            viewModel.selectSegment(segment)
                                        },
                                        onFavoriteClick = { fav ->
                                            interactionFeedback.playPop()
                                            viewModel.openFavorite(fav)
                                        },
                                        onReadingItemClick = { item ->
                                            interactionFeedback.playPop()
                                            viewModel.openReadingItem(item)
                                        },
                                        onToggleReadingItemRead = { item ->
                                            interactionFeedback.playSuccess()
                                            viewModel.toggleReadingItemRead(item)
                                        },
                                        onBookmarkClick = { bm ->
                                            interactionFeedback.playPop()
                                            viewModel.openBookmark(bm)
                                        },
                                        onHistoryItemClick = { hist ->
                                            interactionFeedback.playPop()
                                            viewModel.navigateTo(hist.url)
                                        },
                                        onSearchSubmit = { query ->
                                            interactionFeedback.playSwoosh()
                                            viewModel.navigateTo(query)
                                        },
                                        onQueryChange = { query ->
                                            viewModel.updateSearchQuery(query)
                                        },
                                        onTabsClick = {
                                            viewModel.showTabSwitcher()
                                        },
                                        onSettingsClick = {
                                            viewModel.setShowThemeSettings(true)
                                        },
                                        onToggleThemeMode = {
                                            viewModel.toggleThemeMode()
                                        },
                                        onShowAllFavorites = {
                                            viewModel.setShowAllFavorites(true)
                                        },
                                        onAddFavorite = { title, url ->
                                            viewModel.addFavorite(title, url)
                                        },
                                        onClearHistory = {
                                            interactionFeedback.playSuccess()
                                            viewModel.clearHistory()
                                        }
                                    )
                                }
                            }

                            // Tab Switcher Overlay matching Image 2
                            AnimatedVisibility(
                                visible = uiState.showTabSwitcher,
                                enter = slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    ),
                                    initialOffsetY = { it }
                                ) + fadeIn(),
                                exit = slideOutVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioNoBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    ),
                                    targetOffsetY = { it }
                                ) + fadeOut()
                            ) {
                                BackHandler {
                                    viewModel.hideTabSwitcher()
                                }
                                TabSwitcherView(
                                    tabs = uiState.tabs,
                                    activeTabId = uiState.activeTabId ?: "",
                                    onSelectTab = { tabId ->
                                        interactionFeedback.playTabSwitch()
                                        viewModel.selectTab(tabId)
                                    },
                                    onCloseTab = { tabId ->
                                        interactionFeedback.playClick()
                                        viewModel.closeTab(tabId)
                                    },
                                    onNewTab = { isPrivate ->
                                        interactionFeedback.playPop()
                                        viewModel.createNewTab(isPrivate)
                                    },
                                    onDone = {
                                        interactionFeedback.playClick()
                                        viewModel.hideTabSwitcher()
                                    }
                                )
                            }

                            // Theme & Settings Bottom Sheet
                            if (uiState.showThemeSettings) {
                                ThemeAndSettingsSheet(
                                    sheetState = themeSettingsSheetState,
                                    currentThemeMode = uiState.themeMode,
                                    onSelectThemeMode = { mode ->
                                        viewModel.setThemeMode(mode)
                                    },
                                    sfxEnabled = uiState.sfxEnabled,
                                    onToggleSfx = { enabled ->
                                        viewModel.toggleSfx(enabled)
                                    },
                                    hapticsEnabled = uiState.hapticsEnabled,
                                    onToggleHaptics = { enabled ->
                                        viewModel.toggleHaptics(enabled)
                                    },
                                    isDesktopMode = uiState.isDesktopMode,
                                    onToggleDesktopMode = { enabled ->
                                        viewModel.toggleDesktopMode(enabled)
                                    },
                                    onReplayOnboarding = {
                                        viewModel.replayOnboarding()
                                    },
                                    onClearHistory = {
                                        viewModel.clearHistory()
                                    },
                                    onDismiss = {
                                        viewModel.setShowThemeSettings(false)
                                    }
                                )
                            }

                            // Show All Favorites Sheet
                            if (uiState.showAllFavoritesDialog) {
                                AllFavoritesSheet(
                                    favorites = uiState.favorites,
                                    onFavoriteClick = { fav ->
                                        viewModel.setShowAllFavorites(false)
                                        viewModel.openFavorite(fav)
                                    },
                                    onAddClick = {
                                        viewModel.setShowAllFavorites(false)
                                        viewModel.setShowAddFavorite(true)
                                    },
                                    onDismiss = {
                                        viewModel.setShowAllFavorites(false)
                                    }
                                )
                            }

                            if (uiState.showAddFavoriteDialog) {
                                AddFavoriteDialog(
                                    onDismiss = { viewModel.setShowAddFavorite(false) },
                                    onConfirm = { title, url ->
                                        viewModel.addFavorite(title, url)
                                        interactionFeedback.playSuccess()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllFavoritesSheet(
    favorites: List<FavoriteItem>,
    onFavoriteClick: (FavoriteItem) -> Unit,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalExpressiveTokens.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "All Favorites (${favorites.size})",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = tokens.colorScheme.onSurface
                )

                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Favorite", tint = tokens.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                favorites.forEach { fav ->
                    FavoriteTile(
                        item = fav,
                        onClick = { onFavoriteClick(fav) },
                        modifier = Modifier.testTag("all_fav_${fav.id}")
                    )
                }
            }
        }
    }
}
