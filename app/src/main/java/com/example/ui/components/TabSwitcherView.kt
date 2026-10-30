package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WebTab
import com.example.ui.theme.LocalExpressiveTokens
import com.example.ui.theme.LocalInteractionFeedback

@Composable
fun TabSwitcherView(
    tabs: List<WebTab>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: (isPrivate: Boolean) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val feedback = LocalInteractionFeedback.current
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var isPrivateMode by remember { mutableStateOf(false) }

    val filteredTabs = tabs.filter { it.isPrivate == isPrivateMode }
    val displayTabs = if (filteredTabs.isEmpty()) tabs else filteredTabs

    // Solid dark backdrop matching image 2
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111116))
            .padding(top = statusBarPadding, bottom = navBarBottomPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Action Bar matching Image 2 (Search, Menu, More Options)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search icon circle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF23222B))
                            .bouncyClickable(
                                soundType = FeedbackSoundType.CLICK,
                                onClick = { feedback.playClick() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Tabs",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Menu hamburger circle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF23222B))
                            .bouncyClickable(
                                soundType = FeedbackSoundType.CLICK,
                                onClick = { feedback.playClick() }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Tabs Menu",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Right ••• button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF23222B))
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = { feedback.playClick() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Options",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Tabs Grid (2 Columns, matching image 2)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("tabs_grid")
            ) {
                items(displayTabs, key = { it.id }) { tab ->
                    val isActive = tab.id == activeTabId
                    TabCardV2(
                        tab = tab,
                        isActive = isActive,
                        onSelect = { onSelectTab(tab.id) },
                        onClose = { onCloseTab(tab.id) }
                    )
                }
            }
        }

        // Bottom Navigation Bar matching Image 2:
        // Left: Circular (+) New Tab
        // Center: Pill segment [ Private | N Tabs ]
        // Right: Circular (✓) Done
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Circular (+) Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF282732))
                        .bouncyClickable(
                            soundType = FeedbackSoundType.POP,
                            onClick = { onNewTab(isPrivateMode) }
                        )
                        .testTag("tabs_add_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Tab",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Center Pill Segment [ Private | N Tabs ]
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .shadow(8.dp, RoundedCornerShape(25.dp))
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFF23222C))
                        .border(1.dp, Color(0xFF383644), RoundedCornerShape(25.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Private segment
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .clip(RoundedCornerShape(21.dp))
                                .background(if (isPrivateMode) Color(0xFF3B394A) else Color.Transparent)
                                .bouncyClickable(
                                    soundType = FeedbackSoundType.CLICK,
                                    onClick = { isPrivateMode = true }
                                )
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Private",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                ),
                                color = if (isPrivateMode) Color.White else Color(0xFF9E9BAA)
                            )
                        }

                        // Regular Tabs segment
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .clip(RoundedCornerShape(21.dp))
                                .background(if (!isPrivateMode) Color(0xFF3B394A) else Color.Transparent)
                                .bouncyClickable(
                                    soundType = FeedbackSoundType.CLICK,
                                    onClick = { isPrivateMode = false }
                                )
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${tabs.size} Tabs",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                ),
                                color = if (!isPrivateMode) Color.White else Color(0xFF9E9BAA)
                            )
                        }
                    }
                }

                // Right Circular (✓) Done Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6)) // Vibrant blue accent from image 2
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = onDone
                        )
                        .testTag("tabs_check_done_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TabCardV2(
    tab: WebTab,
    isActive: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .bouncyClickable(
                soundType = FeedbackSoundType.POP,
                onClick = onSelect
            ),
        horizontalAlignment = Alignment.Start
    ) {
        // Tab Preview Card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shadow(
                    elevation = if (isActive) 14.dp else 6.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = if (isActive) Color(0xFF3B82F6).copy(alpha = 0.5f) else Color.Black
                )
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF24232C))
                .border(
                    width = if (isActive) 2.5.dp else 1.dp,
                    color = if (isActive) Color(0xFF3B82F6) else Color(0xFF3A3846),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Thumbnail / Body Preview
            Column(modifier = Modifier.fillMaxSize()) {
                // Card Top Bar with Title and Close X
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (tab.title.isNotBlank()) tab.title else if (tab.url.isNotBlank()) tab.url else "Start Page",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Close (X) circle button
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3D3B4A))
                            .bouncyClickable(
                                soundType = FeedbackSoundType.CLICK,
                                onClick = onClose
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close tab",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Simulated web canvas / realistic browser card body
                val bodyModifier = if (tab.url.isBlank()) {
                    Modifier.background(Brush.verticalGradient(listOf(Color(0xFF3B1E63), Color(0xFF1F0C35))))
                } else {
                    Modifier.background(Color.White)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .then(bodyModifier)
                ) {
                    if (tab.url.isBlank()) {
                        // Start Page preview representation
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.3f))
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(14.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(Color.White.copy(alpha = 0.4f))
                            )
                        }
                    } else {
                        // Webpage miniature layout preview
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1D1B20))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.95f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF79747E).copy(alpha = 0.35f))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF79747E).copy(alpha = 0.35f))
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            // Mini photo placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEDE0FF))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label under card with favicon circle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color(0xFF3B82F6) else Color(0xFF4A4858)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (tab.title.isNotBlank()) tab.title else if (tab.url.isNotBlank()) tab.url else "Start Page",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.5.sp
                ),
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
