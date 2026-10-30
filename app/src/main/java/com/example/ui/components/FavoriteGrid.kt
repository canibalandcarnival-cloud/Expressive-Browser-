package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FavoriteItem
import com.example.ui.theme.LocalExpressiveTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FavoriteGrid(
    favorites: List<FavoriteItem>,
    onFavoriteClick: (FavoriteItem) -> Unit,
    onShowAllClick: () -> Unit,
    onAddFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Favorites",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = tokens.sectionTitleColor
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .bouncyClickable(
                        soundType = FeedbackSoundType.CLICK,
                        onClick = onShowAllClick
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = tokens.actionPillColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Show All Favorites",
                    tint = tokens.actionPillColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4-Column Grid with Circular/Round Icons
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            favorites.take(8).forEach { item ->
                FavoriteTile(
                    item = item,
                    onClick = { onFavoriteClick(item) },
                    modifier = Modifier.testTag("fav_${item.id}")
                )
            }
        }
    }
}

@Composable
fun FavoriteTile(
    item: FavoriteItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current

    Column(
        modifier = modifier
            .width(76.dp)
            .bouncyClickable(
                soundType = FeedbackSoundType.POP,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ROUND Circular Icon Container (CircleShape)
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .background(Color(item.backgroundHex))
                .border(
                    width = 1.dp,
                    color = tokens.pillBorder,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                item.iconName == "drop" -> {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = item.title,
                        tint = Color(item.foregroundHex),
                        modifier = Modifier.size(30.dp)
                    )
                }
                item.iconName == "pinterest" -> {
                    Text(
                        text = "P",
                        color = Color(item.foregroundHex),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
                else -> {
                    Text(
                        text = item.symbol,
                        color = Color(item.foregroundHex),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title with clear legibility against either dark or light solid wallpaper
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                lineHeight = 14.sp
            ),
            color = if (tokens.isWallpaperBright) Color(0xFF1C0A2B) else tokens.onCardTextColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}
