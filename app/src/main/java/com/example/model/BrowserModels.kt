package com.example.model

data class WebTab(
    val id: String,
    val url: String,
    val title: String,
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isPrivate: Boolean = false,
    val previewColor: Long = 0xFF2A1544
)

data class FavoriteItem(
    val id: String,
    val title: String,
    val url: String,
    val symbol: String, // e.g., "CC/", "S", "M", "K", "EC"
    val backgroundHex: Long, // Color hex
    val foregroundHex: Long,
    val isSystemIcon: Boolean = false,
    val iconName: String = "" // "pinterest", "drop", "compass", etc.
)

data class ReadingItem(
    val id: String,
    val title: String,
    val snippet: String,
    val domain: String,
    val url: String,
    val accentHex: Long,
    val isRead: Boolean = false,
    val readingTimeMinutes: Int = 4
)

data class HistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val timestamp: Long,
    val domain: String
)

data class BookmarkItem(
    val id: String,
    val title: String,
    val url: String,
    val folder: String = "Mobile Bookmarks",
    val dateAdded: Long = System.currentTimeMillis()
)

enum class TopNavSegment(val title: String) {
    FAVORITES("Favorites"),
    BOOKMARKS("Bookmarks"),
    READING_LIST("Reading List"),
    HISTORY("History")
}

enum class SolidWallpaperColor(
    val displayName: String,
    val darkHex: Long,
    val lightHex: Long
) {
    PHONE_DYNAMIC("Phone Wallpaper", 0xFF2A1244, 0xFFD8BFFF),
    DEEP_PLUM("Aubergine Plum", 0xFF2E134D, 0xFFD9BAFA),
    MIDNIGHT_INDIGO("Midnight Indigo", 0xFF191854, 0xFFC9D3FC),
    PINE_EMERALD("Emerald Canopy", 0xFF123422, 0xFFBEE4C3),
    SUNSET_CORAL("Sunset Rose", 0xFF421824, 0xFFFFC2CC),
    DESERT_AMBER("Warm Amber", 0xFF3D2511, 0xFFFED2AA),
    OCEAN_TWILIGHT("Ocean Cyan", 0xFF113247, 0xFFBAE0F6)
}

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

enum class OnboardingStep {
    SPLASH,
    HI_THERE,
    HAVE_ACCOUNT_CHECK,
    SIGN_IN,
    COMPLETED
}

data class UserAccount(
    val email: String = "",
    val name: String = "",
    val isLoggedIn: Boolean = false
)
