package com.example.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.example.model.SolidWallpaperColor

data class ExpressiveThemeTokens(
    val colorScheme: ColorScheme,
    val solidWallpaperBackground: Color,
    val cardSurface: Color,
    val cardBorder: Color,
    val pillBackground: Color,
    val pillBorder: Color,
    val omnibarBackground: Color,
    val omnibarBorder: Color,
    val actionPillColor: Color,
    val onActionPillColor: Color,
    val accentLilac: Color,
    val onCardTextColor: Color,
    val onCardMutedColor: Color,
    val sectionTitleColor: Color,
    val isDark: Boolean,
    val isWallpaperBright: Boolean
)

object ExpressivePalettes {

    fun getTokens(
        context: Context,
        isDark: Boolean,
        solidColor: SolidWallpaperColor = SolidWallpaperColor.PHONE_DYNAMIC
    ): ExpressiveThemeTokens {
        val hasDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        // 1. Resolve startup wallpaper background color
        val solidBg: Color = when (solidColor) {
            SolidWallpaperColor.PHONE_DYNAMIC -> {
                PhoneWallpaperColorExtractor.getPhoneWallpaperColor(context, isDark)
            }
            else -> {
                Color(if (isDark) solidColor.darkHex else solidColor.lightHex)
            }
        }

        // 2. Compute exact relative luminance of the active startup wallpaper
        val luminance = ColorUtils.calculateLuminance(solidBg.toArgb()).toFloat()
        // If luminance > 0.40, wallpaper is bright; otherwise dark
        val isWallpaperBright = luminance > 0.40f

        // 3. Construct base Material 3 ColorScheme
        val baseScheme = if (isDark) {
            if (hasDynamic) {
                dynamicDarkColorScheme(context)
            } else {
                darkColorScheme(
                    primary = Color(0xFFFF8588), // Soft coral pill
                    onPrimary = Color(0xFF420B1A),
                    primaryContainer = Color(0xFF5A1D2C),
                    onPrimaryContainer = Color(0xFFFFD9DF),
                    secondary = Color(0xFFD1B5FF), // Lilac digits/accent
                    onSecondary = Color(0xFF381566),
                    secondaryContainer = Color(0xFF4F2C7E),
                    onSecondaryContainer = Color(0xFFEEDBFF),
                    tertiary = Color(0xFFFFB4A8),
                    background = solidBg,
                    onBackground = Color(0xFFF7EEFF),
                    surface = Color(0xFF34184C),
                    onSurface = Color(0xFFF7EEFF)
                )
            }
        } else {
            if (hasDynamic) {
                dynamicLightColorScheme(context)
            } else {
                lightColorScheme(
                    primary = Color(0xFFFF8588),
                    onPrimary = Color(0xFF420B1A),
                    primaryContainer = Color(0xFFFFD9DF),
                    onPrimaryContainer = Color(0xFF3D0716),
                    secondary = Color(0xFF6F43A8),
                    onSecondary = Color(0xFFFFFFFF),
                    secondaryContainer = Color(0xFFEEDBFF),
                    onSecondaryContainer = Color(0xFF280053),
                    background = solidBg,
                    onBackground = Color(0xFF1E1724),
                    surface = Color(0xFF221133),
                    onSurface = Color(0xFFF8F2FF)
                )
            }
        }

        // 4. INVERSE LIGHTNESS CONTRAST RULE:
        // "make the ui color darker when the browser startup wallpaper is bright"
        // "make the ui color lighter if the browser startup wallpaper has dark color like in the image"
        return if (isWallpaperBright) {
            // Bright wallpaper -> UI elements are DARK (rich deep tones, not stark black)
            val derivedDarkCard = PhoneWallpaperColorExtractor.deriveUiCardColor(solidBg, isWallpaperBright = true)
            val darkPill = PhoneWallpaperColorExtractor.derivePillColor(solidBg, isWallpaperBright = true)

            ExpressiveThemeTokens(
                colorScheme = baseScheme,
                solidWallpaperBackground = solidBg,
                cardSurface = derivedDarkCard,
                cardBorder = derivedDarkCard.copy(alpha = 0.85f),
                pillBackground = darkPill,
                pillBorder = darkPill.copy(alpha = 0.85f),
                omnibarBackground = derivedDarkCard,
                omnibarBorder = derivedDarkCard.copy(alpha = 0.9f),
                actionPillColor = Color(0xFFFF8588),
                onActionPillColor = Color(0xFF420B1A),
                accentLilac = Color(0xFFD1B5FF),
                onCardTextColor = Color(0xFFF8F2FF),
                onCardMutedColor = Color(0xFFC7B6D4),
                sectionTitleColor = Color(0xFF1E0C30), // Dark high-contrast title on top of bright wallpaper
                isDark = isDark,
                isWallpaperBright = true
            )
        } else {
            // Dark wallpaper (like in the Google Clock reference image) -> UI elements are LIGHTER
            val derivedLightCard = PhoneWallpaperColorExtractor.deriveUiCardColor(solidBg, isWallpaperBright = false)
            val lightPill = PhoneWallpaperColorExtractor.derivePillColor(solidBg, isWallpaperBright = false)

            ExpressiveThemeTokens(
                colorScheme = baseScheme,
                solidWallpaperBackground = solidBg,
                cardSurface = derivedLightCard,
                cardBorder = derivedLightCard.copy(alpha = 0.85f),
                pillBackground = lightPill,
                pillBorder = lightPill.copy(alpha = 0.85f),
                omnibarBackground = derivedLightCard,
                omnibarBorder = derivedLightCard.copy(alpha = 0.9f),
                actionPillColor = Color(0xFFFF8588), // Coral "Pause" pill from image
                onActionPillColor = Color(0xFF420B1A),
                accentLilac = Color(0xFFD1B5FF), // Lilac digits/accent from image
                onCardTextColor = Color(0xFFF7EEFF),
                onCardMutedColor = Color(0xFFD0BFDD),
                sectionTitleColor = Color(0xFFD1B5FF), // Light lilac title on top of dark wallpaper
                isDark = isDark,
                isWallpaperBright = false
            )
        }
    }
}
