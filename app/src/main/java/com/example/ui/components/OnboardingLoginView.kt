package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OnboardingStep
import com.example.ui.theme.LocalInteractionFeedback
import kotlinx.coroutines.delay

@Composable
fun OnboardingLoginFlow(
    onComplete: (email: String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val feedback = LocalInteractionFeedback.current
    var currentStep by remember { mutableStateOf(OnboardingStep.SPLASH) }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Beautiful animated gradient background matching Image 1
    val gradientColors = listOf(
        Color(0xFF8672F7),
        Color(0xFF9E8DFF),
        Color(0xFF8CC5FE),
        Color(0xFFA6E3E9)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // App Opening Splash Animation
        if (currentStep == OnboardingStep.SPLASH) {
            AppOpeningAnimation(
                onAnimationFinished = {
                    feedback.playPop()
                    currentStep = OnboardingStep.HI_THERE
                }
            )
        } else {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (slideInHorizontally(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        initialOffsetX = { it }
                    ) + fadeIn()).togetherWith(
                        slideOutHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            targetOffsetX = { -it }
                        ) + fadeOut()
                    )
                },
                label = "onboarding_steps",
                modifier = Modifier.fillMaxSize()
            ) { step ->
                when (step) {
                    OnboardingStep.HI_THERE -> {
                        HiThereScreen(
                            onLetGo = {
                                feedback.playClick()
                                currentStep = OnboardingStep.HAVE_ACCOUNT_CHECK
                            },
                            statusBarPadding = statusBarPadding,
                            navBarBottomPadding = navBarBottomPadding
                        )
                    }

                    OnboardingStep.HAVE_ACCOUNT_CHECK -> {
                        AccountCheckScreen(
                            onYes = {
                                feedback.playClick()
                                currentStep = OnboardingStep.SIGN_IN
                            },
                            onNo = {
                                feedback.playSuccess()
                                onSkip()
                            },
                            statusBarPadding = statusBarPadding,
                            navBarBottomPadding = navBarBottomPadding
                        )
                    }

                    OnboardingStep.SIGN_IN -> {
                        SignInScreen(
                            email = emailInput,
                            onEmailChange = { emailInput = it },
                            password = passwordInput,
                            onPasswordChange = { passwordInput = it },
                            onContinue = {
                                feedback.playSuccess()
                                onComplete(emailInput.ifBlank { "User" })
                            },
                            onSkip = {
                                feedback.playClick()
                                onSkip()
                            },
                            statusBarPadding = statusBarPadding,
                            navBarBottomPadding = navBarBottomPadding
                        )
                    }

                    else -> Unit
                }
            }
        }
    }
}

/**
 * Opening Animation on App Launch:
 * Expressive glowing compass rotation + spring pop reveal
 */
@Composable
fun AppOpeningAnimation(
    onAnimationFinished: () -> Unit
) {
    val scale = remember { Animatable(0.3f) }
    val rotation = remember { Animatable(-90f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Staggered pop and spin
        alpha.animateTo(1f, tween(300))
        rotation.animateTo(0f, spring(dampingRatio = 0.65f, stiffness = 400f))
        scale.animateTo(1.1f, spring(dampingRatio = 0.55f, stiffness = 350f))
        scale.animateTo(1f, spring(dampingRatio = 0.8f, stiffness = 500f))
        delay(600)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value)
                .rotate(rotation.value)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(24.dp, CircleShape, ambientColor = Color(0xFF381463))
                    .clip(CircleShape)
                    .background(Color(0xFF26123D))
                    .border(3.dp, Color(0xFFD6BAFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.ic_prism_browser),
                    contentDescription = "Logo",
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Prism",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.White
            )

            Text(
                text = "Expressive Browser",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

/**
 * Step 1: "Hi there" screen with smiley face and "Let's go" (Matching image 1, left panel)
 */
@Composable
fun HiThereScreen(
    onLetGo: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    navBarBottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarPadding, bottom = navBarBottomPadding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Center Content: Smiley Face & "Hi there"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Stylized (= smile matching Arc mobile in Image 1
            Text(
                text = "= )",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 58.sp,
                    lineHeight = 64.sp
                ),
                color = Color.White,
                modifier = Modifier.rotate(90f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hi there",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White
            )
        }

        // Bottom Decorative Glyph & "Let's go" Pill Button (Matching Image 1)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Solid White Rounded Pill Button (Matching Image 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(12.dp, RoundedCornerShape(29.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color.White)
                    .bouncyClickable(
                        soundType = FeedbackSoundType.CLICK,
                        onClick = onLetGo
                    )
                    .testTag("lets_go_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Let's go",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color(0xFF6F52E8)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Step 2: "Do you have an account?" screen with "Yep!" and "No..." (Matching image 1, center panel)
 */
@Composable
fun AccountCheckScreen(
    onYes: () -> Unit,
    onNo: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    navBarBottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarPadding, bottom = navBarBottomPadding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Center: Question
        Text(
            text = "Do you have an\naccount?",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 38.sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        // Bottom Stacked Pill Buttons (Matching Image 1)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // "Yep!" Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(12.dp, RoundedCornerShape(29.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color.White)
                    .bouncyClickable(
                        soundType = FeedbackSoundType.CLICK,
                        onClick = onYes
                    )
                    .testTag("yep_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Yep!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color(0xFF6F52E8)
                )
            }

            // "No..." Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(12.dp, RoundedCornerShape(29.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color.White.copy(alpha = 0.88f))
                    .bouncyClickable(
                        soundType = FeedbackSoundType.CLICK,
                        onClick = onNo
                    )
                    .testTag("no_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color(0xFF6F52E8)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Step 3: "Sign in" screen with inputs and Continue (Matching image 1, right panel)
 */
@Composable
fun SignInScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    navBarBottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarPadding + 20.dp, bottom = navBarBottomPadding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // White rounded input pill 1: Email
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("Email", color = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1F1D2B),
                    unfocusedTextColor = Color(0xFF1F1D2B)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .testTag("signin_email")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // White rounded input pill 2: Password
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("Password", color = Color.Gray) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color(0xFF1F1D2B),
                    unfocusedTextColor = Color(0xFF1F1D2B)
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onContinue() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .testTag("signin_password")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // "Continue" White Pill Button (Matching Image 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .shadow(12.dp, RoundedCornerShape(29.dp), ambientColor = Color.Black.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(29.dp))
                    .background(Color.White)
                    .bouncyClickable(
                        soundType = FeedbackSoundType.SUCCESS,
                        onClick = onContinue
                    )
                    .testTag("continue_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = Color(0xFF6F52E8)
                )
            }
        }

        // Bottom Pills: "Forgot email", "Forgot password" (Matching Image 1) & Guest bypass
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = onSkip
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Forgot email",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = onSkip
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Forgot password",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = onSkip) {
                Text(
                    text = "Continue as Guest",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
