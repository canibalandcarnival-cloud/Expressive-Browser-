package com.example.ui.theme

import android.app.WallpaperColors
import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

/**
 * Extracts and adapts the live user phone wallpaper color for the browser's solid startup wallpaper.
 *
 * Uses WallpaperManager (API 27+) and Material You dynamic color schemes (API 31+) to read
 * the phone wallpaper palette and derives an elegant solid tone that matches the device wallpaper.
 */
object PhoneWallpaperColorExtractor {

    /**
     * Extracts phone wallpaper color and tones it appropriately for dark or light mode.
     */
    fun getPhoneWallpaperColor(context: Context, isDark: Boolean): Color {
        // Method 1: Check WallpaperManager WallpaperColors (API 27+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            try {
                val wm = WallpaperManager.getInstance(context)
                val wpColors: WallpaperColors? = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
                if (wpColors != null) {
                    val primaryColor = wpColors.primaryColor
                    if (primaryColor != null) {
                        return adjustColorForWallpaperBackground(primaryColor.toArgb(), isDark)
                    }
                }
            } catch (_: Exception) {
                // Gracefully fallback
            }
        }

        // Method 2: Android 12+ (API 31+) Material You dynamic colors synthesized from wallpaper
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                // Extract rich chroma from primary/tertiary rather than desaturated grey/black surfaceContainer
                val dynamicColor = if (isDark) {
                    androidx.compose.material3.dynamicDarkColorScheme(context).primary
                } else {
                    androidx.compose.material3.dynamicLightColorScheme(context).primary
                }
                return adjustColorForWallpaperBackground(dynamicColor.toArgb(), isDark)
            } catch (_: Exception) {
                // Gracefully fallback
            }
        }

        // Method 3: Beautiful plum/lilac default matching reference screenshot (no stark black or white)
        return if (isDark) Color(0xFF2A1244) else Color(0xFFD8BFFF)
    }

    /**
     * Derives a solid startup wallpaper background tone from any raw ARGB color,
     * preserving its hue while adjusting saturation and lightness to create
     * a rich, chromatic backdrop without harsh black or white tints.
     */
    fun adjustColorForWallpaperBackground(rawArgb: Int, isDark: Boolean): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(rawArgb, hsl)

        val targetLightness = if (isDark) {
            0.18f // Rich deep tone (e.g. #2A1244 plum), not stark black (0.12f was too dark)
        } else {
            0.75f // Rich colorful pastel (e.g. #D8BFFF lilac), not washed out white (0.88f was too bright)
        }
        val targetSaturation = (hsl[1] * 0.95f).coerceIn(0.40f, 0.80f)

        hsl[1] = targetSaturation
        hsl[2] = targetLightness
        return Color(ColorUtils.HSLToColor(hsl))
    }

    /**
     * Derives contrasting elevated UI card color from a wallpaper background color.
     * When wallpaper is dark: returns a LIGHTER elevated container.
     * When wallpaper is bright: returns a DARK contrasting container.
     */
    fun deriveUiCardColor(wallpaperBg: Color, isWallpaperBright: Boolean): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(wallpaperBg.toArgb(), hsl)

        return if (isWallpaperBright) {
            // Wallpaper is bright -> UI container is DARK (rich deep tone, not stark black)
            hsl[2] = 0.20f
            hsl[1] = (hsl[1] * 0.95f).coerceIn(0.35f, 0.70f)
            Color(ColorUtils.HSLToColor(hsl))
        } else {
            // Wallpaper is dark -> UI container is LIGHTER than wallpaper
            hsl[2] = (hsl[2] + 0.10f).coerceIn(0.24f, 0.35f)
            hsl[1] = (hsl[1] * 0.95f).coerceIn(0.35f, 0.70f)
            Color(ColorUtils.HSLToColor(hsl))
        }
    }

    /**
     * Derives pill container background color for segment bars and floating omnibars.
     */
    fun derivePillColor(wallpaperBg: Color, isWallpaperBright: Boolean): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(wallpaperBg.toArgb(), hsl)

        return if (isWallpaperBright) {
            hsl[2] = 0.16f
            hsl[1] = (hsl[1] * 0.95f).coerceIn(0.35f, 0.70f)
            Color(ColorUtils.HSLToColor(hsl))
        } else {
            hsl[2] = (hsl[2] + 0.06f).coerceIn(0.20f, 0.30f)
            hsl[1] = (hsl[1] * 0.95f).coerceIn(0.35f, 0.70f)
            Color(ColorUtils.HSLToColor(hsl))
        }
    }
}
