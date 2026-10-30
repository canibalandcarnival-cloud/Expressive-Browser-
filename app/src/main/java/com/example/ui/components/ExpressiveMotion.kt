package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.example.ui.theme.LocalInteractionFeedback
import kotlinx.coroutines.launch

val ExpressiveBouncySpring = spring<Float>(
    dampingRatio = 0.62f,
    stiffness = 380f
)

val PunchyFluidSpring = spring<Float>(
    dampingRatio = 0.7f,
    stiffness = 450f
)

enum class FeedbackSoundType {
    CLICK,
    POP,
    SWOOSH,
    TAB_ACTION,
    SUCCESS
}

/**
 * Expressive bouncy clickable modifier that scales smoothly down on press and rebounds
 * with spring physics, paired with zero-latency audio SFX and haptic feedback.
 */
fun Modifier.bouncyClickable(
    enabled: Boolean = true,
    pressedScale: Float = 0.92f,
    soundType: FeedbackSoundType = FeedbackSoundType.CLICK,
    onClick: () -> Unit
): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    val feedback = LocalInteractionFeedback.current

    this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                scope.launch {
                    scale.animateTo(pressedScale, spring(dampingRatio = 0.8f, stiffness = 600f))
                }
                val up = waitForUpOrCancellation()
                scope.launch {
                    scale.animateTo(1f, ExpressiveBouncySpring)
                }
                if (up != null) {
                    when (soundType) {
                        FeedbackSoundType.CLICK -> feedback.playClick()
                        FeedbackSoundType.POP -> feedback.playPop()
                        FeedbackSoundType.SWOOSH -> feedback.playSwoosh()
                        FeedbackSoundType.TAB_ACTION -> feedback.playTabSwitch()
                        FeedbackSoundType.SUCCESS -> feedback.playSuccess()
                    }
                    onClick()
                }
            }
        }
}

/**
 * Shimmer gradient overlay for loading indicators and expressive card accents
 */
fun Modifier.expressiveShimmer(
    colors: List<Color> = listOf(
        Color.Transparent,
        Color.White.copy(alpha = 0.45f),
        Color.Transparent
    )
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_anim"
    )

    this.drawWithContent {
        drawContent()
        val brush = Brush.horizontalGradient(
            colors = colors,
            startX = translateAnim - 200f,
            endX = translateAnim + 200f
        )
        drawRect(brush = brush, blendMode = BlendMode.SrcAtop)
    }
}
