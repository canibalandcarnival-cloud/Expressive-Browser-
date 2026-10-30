package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.TopNavSegment
import com.example.ui.theme.LocalExpressiveTokens

@Composable
fun TopSegmentBar(
    selectedSegment: TopNavSegment,
    onSegmentSelected: (TopNavSegment) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val segments = TopNavSegment.values()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(27.dp))
            .clip(RoundedCornerShape(27.dp))
            .background(tokens.pillBackground) // Solid Dark M3 pill
            .border(width = 1.dp, color = tokens.pillBorder, shape = RoundedCornerShape(27.dp))
            .padding(horizontal = 6.dp, vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            segments.forEach { segment ->
                val isSelected = segment == selectedSegment
                val icon = when (segment) {
                    TopNavSegment.FAVORITES -> if (isSelected) Icons.Filled.GridView else Icons.Outlined.GridView
                    TopNavSegment.BOOKMARKS -> if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                    TopNavSegment.READING_LIST -> if (isSelected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
                    TopNavSegment.HISTORY -> if (isSelected) Icons.Filled.History else Icons.Outlined.History
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            if (isSelected) tokens.actionPillColor else Color.Transparent
                        )
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = { onSegmentSelected(segment) }
                        )
                        .testTag("segment_${segment.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = segment.title,
                        tint = if (isSelected) {
                            tokens.onActionPillColor
                        } else {
                            tokens.accentLilac.copy(alpha = 0.7f)
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
