package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThemeMode
import com.example.ui.theme.LocalExpressiveTokens
import com.example.ui.theme.LocalInteractionFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeAndSettingsSheet(
    sheetState: SheetState,
    currentThemeMode: ThemeMode,
    onSelectThemeMode: (ThemeMode) -> Unit,
    sfxEnabled: Boolean,
    onToggleSfx: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onToggleHaptics: (Boolean) -> Unit,
    isDesktopMode: Boolean,
    onToggleDesktopMode: (Boolean) -> Unit,
    onReplayOnboarding: () -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val feedback = LocalInteractionFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.cardSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(44.dp)
                    .height(5.dp)
                    .clip(CircleShape)
                    .background(tokens.onCardMutedColor.copy(alpha = 0.4f))
            )
        },
        modifier = modifier.testTag("theme_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Themes & Solid Wallpaper",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = tokens.onCardTextColor
            )

            Text(
                text = "Solid Material 3 Expressive UI adapted to phone wallpaper",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.onCardMutedColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            // LIGHT MODE / DARK MODE SELECTOR
            Text(
                text = "COLOR SCHEME",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = tokens.accentLilac
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(tokens.pillBackground)
                    .border(1.dp, tokens.pillBorder, RoundedCornerShape(25.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    ThemeMode.DARK to "Dark Mode",
                    ThemeMode.LIGHT to "Light Mode"
                ).forEach { (mode, label) ->
                    val isSelected = currentThemeMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(21.dp))
                            .background(if (isSelected) tokens.actionPillColor else Color.Transparent)
                            .bouncyClickable(
                                soundType = FeedbackSoundType.CLICK,
                                onClick = {
                                    feedback.playClick()
                                    onSelectThemeMode(mode)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (mode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isSelected) tokens.onActionPillColor else tokens.onCardMutedColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) tokens.onActionPillColor else tokens.onCardMutedColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Audio SFX & Haptics Section
            Text(
                text = "INTERACTION FEEDBACK",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = tokens.accentLilac
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SFX Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.pillBackground)
                    .border(1.dp, tokens.pillBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "SFX",
                        tint = tokens.accentLilac,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Audio Sound Effects (SFX)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.onCardTextColor
                        )
                        Text(
                            text = "Pops, taps, and chime feedback",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onCardMutedColor
                        )
                    }
                }

                Switch(
                    checked = sfxEnabled,
                    onCheckedChange = {
                        onToggleSfx(it)
                        if (it) feedback.playSuccess()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.actionPillColor,
                        checkedTrackColor = tokens.actionPillColor.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Haptic Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.pillBackground)
                    .border(1.dp, tokens.pillBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Haptics",
                        tint = tokens.actionPillColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Haptic Vibration Feedback",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.onCardTextColor
                        )
                        Text(
                            text = "Tactile spring pulses on every press",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onCardMutedColor
                        )
                    }
                }

                Switch(
                    checked = hapticsEnabled,
                    onCheckedChange = {
                        onToggleHaptics(it)
                        if (it) feedback.playPop()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.actionPillColor,
                        checkedTrackColor = tokens.actionPillColor.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Desktop Site Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.pillBackground)
                    .border(1.dp, tokens.pillBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Laptop,
                        contentDescription = "Desktop Site",
                        tint = tokens.accentLilac,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Desktop Site Mode",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.onCardTextColor
                        )
                        Text(
                            text = "Request desktop view for websites",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.onCardMutedColor
                        )
                    }
                }

                Switch(
                    checked = isDesktopMode,
                    onCheckedChange = onToggleDesktopMode,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = tokens.actionPillColor,
                        checkedTrackColor = tokens.actionPillColor.copy(alpha = 0.4f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Replay Welcome / Sign In Flow Button (Coral pill)
            Button(
                onClick = onReplayOnboarding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tokens.actionPillColor,
                    contentColor = tokens.onActionPillColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Account & Arc-Style Sign In Flow", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clear history button
            Button(
                onClick = onClearHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5A1D2C),
                    contentColor = Color(0xFFFFD9DF)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Browsing History")
            }
        }
    }
}
