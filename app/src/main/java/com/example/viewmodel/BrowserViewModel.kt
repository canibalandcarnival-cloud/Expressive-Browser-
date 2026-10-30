package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.BookmarkItem
import com.example.model.FavoriteItem
import com.example.model.HistoryItem
import com.example.model.ReadingItem
import com.example.model.SolidWallpaperColor
import com.example.model.ThemeMode
import com.example.model.TopNavSegment
import com.example.model.UserAccount
import com.example.model.WebTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

data class BrowserUiState(
    val solidWallpaperColor: SolidWallpaperColor = SolidWallpaperColor.PHONE_DYNAMIC,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val selectedSegment: TopNavSegment = TopNavSegment.FAVORITES,
    val isOnboardingCompleted: Boolean = false, // Set to false so first launch triggers Arc-style login flow!
    val userAccount: UserAccount = UserAccount(),
    val tabs: List<WebTab> = listOf(
        WebTab(
            id = "tab_initial",
            url = "",
            title = "Start Page",
            previewColor = 0xFF2A1544
        )
    ),
    val activeTabId: String? = null, // null means start page is active
    val favorites: List<FavoriteItem> = defaultFavorites,
    val readingList: List<ReadingItem> = defaultReadingList,
    val bookmarks: List<BookmarkItem> = defaultBookmarks,
    val history: List<HistoryItem> = defaultHistory,
    val sfxEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val isDesktopMode: Boolean = false,
    val showTabSwitcher: Boolean = false,
    val showThemeSettings: Boolean = false,
    val showAllFavoritesDialog: Boolean = false,
    val showAddFavoriteDialog: Boolean = false,
    val searchQuery: String = ""
)

private val defaultFavorites = listOf(
    FavoriteItem(
        id = "fav_1",
        title = "Rit Dye – Fabric Dye...",
        url = "https://www.ritdye.com",
        symbol = "",
        backgroundHex = 0xFFFFDCE2,
        foregroundHex = 0xFF220A2B,
        iconName = "drop"
    ),
    FavoriteItem(
        id = "fav_2",
        title = "Beti-Jai: an historical fr...",
        url = "https://betijai.org",
        symbol = "CC/",
        backgroundHex = 0xFFFCD5CE,
        foregroundHex = 0xFF2B0E3E
    ),
    FavoriteItem(
        id = "fav_3",
        title = "Five Design-Driven Port...",
        url = "https://fivedesign.com",
        symbol = "S",
        backgroundHex = 0xFF121212,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_4",
        title = "Mush Studios | Home Dec...",
        url = "https://mushstudios.co",
        symbol = "M",
        backgroundHex = 0xFF6C757D,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_5",
        title = "Pinterest",
        url = "https://www.pinterest.com",
        symbol = "P",
        backgroundHex = 0xFFFFFFFF,
        foregroundHex = 0xFFE60023,
        iconName = "pinterest"
    ),
    FavoriteItem(
        id = "fav_6",
        title = "KOBU | Creative St...",
        url = "https://kobu.agency",
        symbol = "K",
        backgroundHex = 0xFFCCCCCC,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_7",
        title = "Process / Era Ceramics",
        url = "https://eraceramics.com",
        symbol = "EC",
        backgroundHex = 0xFF1B1B1B,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_8",
        title = "Curious and Wondrous T...",
        url = "https://curiousandwondrous.com",
        symbol = "CW",
        backgroundHex = 0xFF2E241A,
        foregroundHex = 0xFFE4C28D
    ),
    FavoriteItem(
        id = "fav_9",
        title = "Wikipedia",
        url = "https://www.wikipedia.org",
        symbol = "W",
        backgroundHex = 0xFFFFFFFF,
        foregroundHex = 0xFF000000
    ),
    FavoriteItem(
        id = "fav_10",
        title = "GitHub",
        url = "https://github.com",
        symbol = "GH",
        backgroundHex = 0xFF24292E,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_11",
        title = "Dribbble",
        url = "https://dribbble.com",
        symbol = "Dr",
        backgroundHex = 0xFFEA4C89,
        foregroundHex = 0xFFFFFFFF
    ),
    FavoriteItem(
        id = "fav_12",
        title = "Unsplash",
        url = "https://unsplash.com",
        symbol = "UN",
        backgroundHex = 0xFF000000,
        foregroundHex = 0xFFFFFFFF
    )
)

private val defaultReadingList = listOf(
    ReadingItem(
        id = "read_1",
        title = "Process / Era Ceramics",
        snippet = "Ceramics and dinnerware for the modern home crafted with timeless clay and glaze techniques.",
        domain = "eraceramics.com",
        url = "https://eraceramics.com",
        accentHex = 0xFFD8893A,
        readingTimeMinutes = 4
    ),
    ReadingItem(
        id = "read_2",
        title = "Wallflower | San Francisco Floral Design Studio",
        snippet = "San Francisco's favorite floral studio, weddings, botanical installations and events.",
        domain = "wallflowersanfrancisco.com",
        url = "https://wallflowersanfrancisco.com",
        accentHex = 0xFF8AA68F,
        readingTimeMinutes = 6
    ),
    ReadingItem(
        id = "read_3",
        title = "Jean-Michel Othoniel Explains the Hypnotic Potential of Flow...",
        snippet = "The French artist recasts the Brooklyn Botanic Garden with glass beads and kinetic sculptures.",
        domain = "surfacemag.com",
        url = "https://surfacemag.com",
        accentHex = 0xFF351C5C,
        readingTimeMinutes = 8
    ),
    ReadingItem(
        id = "read_4",
        title = "Material 3 Expressive: Fluid Bouncy Motions and Adaptivity",
        snippet = "A deep dive into Android's latest design guidelines, bouncy springs, and dynamic wallpaper palettes.",
        domain = "m3.material.io",
        url = "https://m3.material.io",
        accentHex = 0xFFFF7B89,
        readingTimeMinutes = 5
    )
)

private val defaultBookmarks = listOf(
    BookmarkItem(
        id = "bm_1",
        title = "Material Design 3 Documentation",
        url = "https://m3.material.io"
    ),
    BookmarkItem(
        id = "bm_2",
        title = "Kotlin Documentation",
        url = "https://kotlinlang.org"
    ),
    BookmarkItem(
        id = "bm_3",
        title = "Era Ceramics Studio",
        url = "https://eraceramics.com"
    ),
    BookmarkItem(
        id = "bm_4",
        title = "Rit Dye Craft Inspiration",
        url = "https://www.ritdye.com"
    )
)

private val defaultHistory = listOf(
    HistoryItem(
        id = "hist_1",
        title = "Material 3 Expressive UI Showcase",
        url = "https://m3.material.io",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
        domain = "m3.material.io"
    ),
    HistoryItem(
        id = "hist_2",
        title = "Era Ceramics Shop and Journal",
        url = "https://eraceramics.com",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
        domain = "eraceramics.com"
    ),
    HistoryItem(
        id = "hist_3",
        title = "Pinterest Inspiration Feed",
        url = "https://www.pinterest.com",
        timestamp = System.currentTimeMillis() - 1000 * 60 * 120,
        domain = "pinterest.com"
    )
)

class BrowserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun completeOnboarding(email: String) {
        _uiState.update {
            it.copy(
                isOnboardingCompleted = true,
                userAccount = UserAccount(email = email, name = email.substringBefore("@"), isLoggedIn = true)
            )
        }
    }

    fun skipOnboarding() {
        _uiState.update { it.copy(isOnboardingCompleted = true) }
    }

    fun replayOnboarding() {
        _uiState.update { it.copy(isOnboardingCompleted = false, showThemeSettings = false) }
    }

    fun selectSolidWallpaperColor(color: SolidWallpaperColor) {
        _uiState.update { it.copy(solidWallpaperColor = color) }
    }

    fun cycleWallpaperColor() {
        _uiState.update { state ->
            val allColors = SolidWallpaperColor.values()
            val currentIndex = allColors.indexOf(state.solidWallpaperColor)
            val nextIndex = (currentIndex + 1) % allColors.size
            state.copy(solidWallpaperColor = allColors[nextIndex])
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun toggleThemeMode() {
        _uiState.update {
            val next = if (it.themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
            it.copy(themeMode = next)
        }
    }

    fun selectSegment(segment: TopNavSegment) {
        _uiState.update { it.copy(selectedSegment = segment) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun navigateTo(rawInput: String) {
        val trimmed = rawInput.trim()
        if (trimmed.isBlank()) return

        val formattedUrl = when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${java.net.URLEncoder.encode(trimmed, "UTF-8")}"
        }

        val currentActiveId = _uiState.value.activeTabId
        if (currentActiveId == null) {
            val newTab = WebTab(
                id = UUID.randomUUID().toString(),
                url = formattedUrl,
                title = trimmed,
                isLoading = true
            )
            _uiState.update { state ->
                state.copy(
                    tabs = state.tabs.filter { it.url.isNotEmpty() } + newTab,
                    activeTabId = newTab.id,
                    searchQuery = formattedUrl
                )
            }
        } else {
            _uiState.update { state ->
                val updatedTabs = state.tabs.map {
                    if (it.id == currentActiveId) it.copy(url = formattedUrl, title = trimmed, isLoading = true) else it
                }
                state.copy(tabs = updatedTabs, searchQuery = formattedUrl)
            }
        }
        recordHistory(title = trimmed, url = formattedUrl)
    }

    fun openFavorite(item: FavoriteItem) {
        navigateTo(item.url)
    }

    fun openReadingItem(item: ReadingItem) {
        navigateTo(item.url)
    }

    fun toggleReadingItemRead(item: ReadingItem) {
        _uiState.update { state ->
            val updated = state.readingList.map {
                if (it.id == item.id) it.copy(isRead = !it.isRead) else it
            }
            state.copy(readingList = updated)
        }
    }

    fun openBookmark(item: BookmarkItem) {
        navigateTo(item.url)
    }

    fun toggleCurrentBookmark() {
        val currentTab = getActiveTab() ?: return
        if (currentTab.url.isEmpty()) return

        val isAlreadyBookmarked = _uiState.value.bookmarks.any { it.url == currentTab.url }
        if (isAlreadyBookmarked) {
            _uiState.update { state ->
                state.copy(bookmarks = state.bookmarks.filter { it.url != currentTab.url })
            }
        } else {
            val newBookmark = BookmarkItem(
                id = UUID.randomUUID().toString(),
                title = if (currentTab.title.isNotEmpty()) currentTab.title else currentTab.url,
                url = currentTab.url
            )
            _uiState.update { state ->
                state.copy(bookmarks = state.bookmarks + newBookmark)
            }
        }
    }

    fun isCurrentTabBookmarked(): Boolean {
        val currentTab = getActiveTab() ?: return false
        return _uiState.value.bookmarks.any { it.url == currentTab.url }
    }

    fun recordHistory(title: String, url: String) {
        if (url.isBlank() || url.startsWith("about:")) return
        val domain = try {
            java.net.URI(url).host ?: url
        } catch (_: Exception) {
            url
        }
        val item = HistoryItem(
            id = UUID.randomUUID().toString(),
            title = if (title.isNotEmpty()) title else domain,
            url = url,
            timestamp = System.currentTimeMillis(),
            domain = domain
        )
        _uiState.update { state ->
            state.copy(history = listOf(item) + state.history.filter { it.url != url })
        }
    }

    fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun goHome() {
        _uiState.update { it.copy(activeTabId = null, searchQuery = "") }
    }

    fun showTabSwitcher() {
        _uiState.update { it.copy(showTabSwitcher = true) }
    }

    fun hideTabSwitcher() {
        _uiState.update { it.copy(showTabSwitcher = false) }
    }

    fun selectTab(tabId: String) {
        _uiState.update { state ->
            val tab = state.tabs.find { it.id == tabId }
            state.copy(
                activeTabId = if (tab?.url.isNullOrEmpty()) null else tabId,
                showTabSwitcher = false,
                searchQuery = tab?.url ?: ""
            )
        }
    }

    fun closeTab(tabId: String) {
        _uiState.update { state ->
            val remaining = state.tabs.filter { it.id != tabId }
            val nextTabs = if (remaining.isEmpty()) {
                listOf(WebTab(id = UUID.randomUUID().toString(), url = "", title = "Start Page"))
            } else {
                remaining
            }
            val newActiveId = if (state.activeTabId == tabId) {
                nextTabs.firstOrNull { it.url.isNotEmpty() }?.id
            } else {
                state.activeTabId
            }
            state.copy(tabs = nextTabs, activeTabId = newActiveId)
        }
    }

    fun createNewTab(isPrivate: Boolean = false) {
        val newTab = WebTab(
            id = UUID.randomUUID().toString(),
            url = "",
            title = if (isPrivate) "Private Tab" else "Start Page",
            isPrivate = isPrivate
        )
        _uiState.update { state ->
            state.copy(
                tabs = state.tabs + newTab,
                activeTabId = null,
                showTabSwitcher = false,
                searchQuery = ""
            )
        }
    }

    fun updateTabProgress(progress: Int, isLoading: Boolean) {
        val currentId = _uiState.value.activeTabId ?: return
        val currentTab = _uiState.value.tabs.find { it.id == currentId } ?: return
        if (currentTab.progress == progress && currentTab.isLoading == isLoading) return
        _uiState.update { state ->
            val updated = state.tabs.map {
                if (it.id == currentId) it.copy(progress = progress, isLoading = isLoading) else it
            }
            state.copy(tabs = updated)
        }
    }

    fun updateTabDetails(url: String, title: String) {
        val currentId = _uiState.value.activeTabId ?: return
        val currentTab = _uiState.value.tabs.find { it.id == currentId }
        if (currentTab != null && currentTab.url == url && (title.isBlank() || currentTab.title == title)) {
            return
        }
        _uiState.update { state ->
            val updated = state.tabs.map {
                if (it.id == currentId) {
                    it.copy(
                        url = url,
                        title = if (title.isNotBlank()) title else it.title
                    )
                } else it
            }
            state.copy(tabs = updated, searchQuery = url)
        }
        if (title.isNotBlank()) {
            recordHistory(title = title, url = url)
        }
    }

    fun updateBackForwardState(canGoBack: Boolean, canGoForward: Boolean) {
        val currentId = _uiState.value.activeTabId ?: return
        val currentTab = _uiState.value.tabs.find { it.id == currentId } ?: return
        if (currentTab.canGoBack == canGoBack && currentTab.canGoForward == canGoForward) return
        _uiState.update { state ->
            val updated = state.tabs.map {
                if (it.id == currentId) it.copy(canGoBack = canGoBack, canGoForward = canGoForward) else it
            }
            state.copy(tabs = updated)
        }
    }

    fun toggleSfx(enabled: Boolean) {
        _uiState.update { it.copy(sfxEnabled = enabled) }
    }

    fun toggleHaptics(enabled: Boolean) {
        _uiState.update { it.copy(hapticsEnabled = enabled) }
    }

    fun toggleDesktopMode(enabled: Boolean) {
        _uiState.update { it.copy(isDesktopMode = enabled) }
    }

    fun setShowThemeSettings(show: Boolean) {
        _uiState.update { it.copy(showThemeSettings = show) }
    }

    fun setShowAllFavorites(show: Boolean) {
        _uiState.update { it.copy(showAllFavoritesDialog = show) }
    }

    fun setShowAddFavorite(show: Boolean) {
        _uiState.update { it.copy(showAddFavoriteDialog = show) }
    }

    fun addFavorite(title: String, url: String) {
        val domain = try {
            java.net.URI(url).host ?: url
        } catch (_: Exception) {
            url
        }
        val symbol = domain.take(2).uppercase()
        val item = FavoriteItem(
            id = UUID.randomUUID().toString(),
            title = title,
            url = if (url.startsWith("http")) url else "https://$url",
            symbol = symbol,
            backgroundHex = 0xFF512D85,
            foregroundHex = 0xFFF1E6FF
        )
        _uiState.update { it.copy(favorites = it.favorites + item, showAddFavoriteDialog = false) }
    }

    fun getActiveTab(): WebTab? {
        val activeId = _uiState.value.activeTabId ?: return null
        return _uiState.value.tabs.find { it.id == activeId }
    }
}
