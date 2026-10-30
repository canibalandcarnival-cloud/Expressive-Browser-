package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalExpressiveTokens

@Composable
fun FloatingOmnibar(
    urlQuery: String,
    onQueryChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    canGoBack: Boolean,
    onBackClick: () -> Unit,
    tabCount: Int,
    onTabsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isLoading: Boolean = false,
    onReloadClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        color = Color.Transparent,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .padding(bottom = maxOf(navBarBottomPadding, 12.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Circular Back Button (Solid dark, no neon glow)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(tokens.omnibarBackground)
                    .border(
                        width = 1.dp,
                        color = tokens.pillBorder,
                        shape = CircleShape
                    )
                    .bouncyClickable(
                        soundType = FeedbackSoundType.CLICK,
                        onClick = onBackClick
                    )
                    .testTag("omnibar_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) tokens.onCardTextColor else tokens.onCardMutedColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Central Rounded Omnibar Address Pill (Solid Dark M3 Expressive)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .background(tokens.omnibarBackground)
                    .border(
                        width = 1.dp,
                        color = if (isFocused) tokens.actionPillColor else tokens.omnibarBorder,
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLoading) Icons.Default.Refresh else Icons.Default.Search,
                        contentDescription = "Search",
                        tint = tokens.accentLilac,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = urlQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { isFocused = it.isFocused }
                            .testTag("omnibar_input"),
                        singleLine = true,
                        textStyle = TextStyle(
                            color = tokens.onCardTextColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(tokens.actionPillColor),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                focusManager.clearFocus()
                                onSubmit(urlQuery)
                            }
                        ),
                        decorationBox = { innerTextField ->
                            if (urlQuery.isEmpty() && !isFocused) {
                                Text(
                                    text = "Search or enter website",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.5.sp
                                    ),
                                    color = tokens.onCardMutedColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                    )

                    // Clear / Mic button
                    if (urlQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = tokens.onCardMutedColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice search",
                            tint = tokens.onCardMutedColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Tabs Switcher Button (Solid Dark M3)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.omnibarBackground)
                    .border(
                        width = 1.dp,
                        color = tokens.pillBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .bouncyClickable(
                        soundType = FeedbackSoundType.TAB_ACTION,
                        onClick = onTabsClick
                    )
                    .testTag("omnibar_tabs_button"),
                contentAlignment = Alignment.Center
            ) {
                // Tab count inside double square icon
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .border(
                            width = 2.dp,
                            color = tokens.accentLilac,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$tabCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = tokens.accentLilac
                    )
                }
            }

            // Theme & Settings Pill Button (Solid Coral pill from screenshot)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
                    .clip(CircleShape)
                    .background(tokens.actionPillColor)
                    .bouncyClickable(
                        soundType = FeedbackSoundType.POP,
                        onClick = onSettingsClick
                    )
                    .testTag("omnibar_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Themes and Settings",
                    tint = tokens.onActionPillColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
