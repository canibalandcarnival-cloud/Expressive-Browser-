package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.example.audio.InteractionFeedback
import com.example.model.SolidWallpaperColor
import com.example.model.ThemeMode

val LocalExpressiveTokens = compositionLocalOf<ExpressiveThemeTokens> {
    error("ExpressiveThemeTokens not provided")
}

val LocalInteractionFeedback = compositionLocalOf<InteractionFeedback> {
    error("InteractionFeedback not provided")
}

@Composable
fun ExpressiveBrowserTheme(
    solidWallpaperColor: SolidWallpaperColor = SolidWallpaperColor.PHONE_DYNAMIC,
    themeMode: ThemeMode = ThemeMode.DARK,
    interactionFeedback: InteractionFeedback? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val feedback = interactionFeedback ?: InteractionFeedback(context)
    val systemInDark = isSystemInDarkTheme()

    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> systemInDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val tokens = ExpressivePalettes.getTokens(
        context = context,
        isDark = isDark,
        solidColor = solidWallpaperColor
    )

    CompositionLocalProvider(
        LocalExpressiveTokens provides tokens,
        LocalInteractionFeedback provides feedback
    ) {
        MaterialTheme(
            colorScheme = tokens.colorScheme,
            typography = Typography,
            content = content
        )
    }
}
