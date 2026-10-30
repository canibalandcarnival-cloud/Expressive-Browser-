package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.WebTab
import com.example.ui.theme.LocalExpressiveTokens
import com.example.ui.theme.LocalInteractionFeedback

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContainer(
    tab: WebTab,
    isDesktopMode: Boolean,
    isBookmarked: Boolean,
    totalTabCount: Int,
    onUrlChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onProgressChanged: (Int, Boolean) -> Unit,
    onBackStateChanged: (Boolean, Boolean) -> Unit,
    onToggleBookmark: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit,
    onSearchAgainClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalExpressiveTokens.current
    val feedback = LocalInteractionFeedback.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentDisplayUrl by remember(tab.url) { mutableStateOf(tab.url) }
    var currentDisplayTitle by remember(tab.title) { mutableStateOf(tab.title) }
    var lastRequestedUrl by remember(tab.id) { mutableStateOf(tab.url) }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BackHandler(enabled = true) {
        if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
            feedback.playClick()
        } else {
            onHomeClick()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.colorScheme.background)
            .padding(top = statusBarPadding)
    ) {
        // Expressive Solid Material 3 Top Bar
        Surface(
            color = tokens.omnibarBackground,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(50.dp)
                .border(width = 1.dp, color = tokens.omnibarBorder, shape = RoundedCornerShape(24.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tappable URL title block (allows editing search)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .bouncyClickable(
                            soundType = FeedbackSoundType.CLICK,
                            onClick = onSearchAgainClick
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = tokens.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (currentDisplayTitle.isNotBlank()) currentDisplayTitle else currentDisplayUrl,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            ),
                            color = tokens.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentDisplayUrl.removePrefix("https://").removePrefix("http://"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.5.sp
                            ),
                            color = tokens.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (tab.isLoading) {
                                webViewInstance?.stopLoading()
                            } else {
                                webViewInstance?.reload()
                            }
                            feedback.playClick()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (tab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = if (tab.isLoading) "Stop" else "Reload",
                            tint = tokens.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) tokens.actionPillColor else tokens.onCardTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Animated Progress Bar
        AnimatedVisibility(
            visible = tab.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val animatedProgress by animateFloatAsState(
                targetValue = (tab.progress.coerceIn(5, 100)) / 100f,
                label = "progress_anim"
            )
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = tokens.colorScheme.primary,
                trackColor = tokens.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        }

        // Web View Container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White) // Webpages have solid white canvas
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // Robust web settings for modern mobile browsing
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            loadsImagesAutomatically = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT

                            val mobileUA = "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                            val desktopUA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
                            userAgentString = if (isDesktopMode) desktopUA else mobileUA
                        }

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val reqUrl = request?.url?.toString() ?: return false
                                return if (reqUrl.startsWith("http://") || reqUrl.startsWith("https://")) {
                                    false
                                } else {
                                    try {
                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, request.url)
                                        context.startActivity(intent)
                                    } catch (_: Exception) { }
                                    true
                                }
                            }

                            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                                // Allow SSL navigation in sandbox environment
                                handler?.proceed()
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                url?.let {
                                    lastRequestedUrl = it
                                    currentDisplayUrl = it
                                    onUrlChanged(it)
                                }
                                onProgressChanged(20, true)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let {
                                    lastRequestedUrl = it
                                }
                                onProgressChanged(100, false)
                                onBackStateChanged(view?.canGoBack() == true, view?.canGoForward() == true)
                                view?.title?.let {
                                    currentDisplayTitle = it
                                    onTitleChanged(it)
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                onProgressChanged(newProgress, newProgress < 100)
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (!title.isNullOrBlank()) {
                                    currentDisplayTitle = title
                                    onTitleChanged(title)
                                }
                            }
                        }

                        if (tab.url.isNotBlank()) {
                            lastRequestedUrl = tab.url
                            loadUrl(tab.url)
                        }
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    // 1. Update user agent if desktop mode toggles
                    val mobileUA = "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36"
                    val desktopUA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
                    val expectedUA = if (isDesktopMode) desktopUA else mobileUA
                    if (webView.settings.userAgentString != expectedUA) {
                        webView.settings.userAgentString = expectedUA
                    }

                    // 2. Prevent infinite reload loop: Only load if tab.url changed to a genuinely new destination
                    if (tab.url.isNotBlank() && tab.url != lastRequestedUrl) {
                        val currentWvUrl = webView.url
                        val isSameNormalized = currentWvUrl != null && (
                            currentWvUrl == tab.url ||
                            currentWvUrl.trimEnd('/') == tab.url.trimEnd('/') ||
                            currentWvUrl.removePrefix("https://").removePrefix("http://").trimEnd('/') ==
                                tab.url.removePrefix("https://").removePrefix("http://").trimEnd('/')
                        )
                        if (!isSameNormalized) {
                            lastRequestedUrl = tab.url
                            webView.loadUrl(tab.url)
                        } else {
                            lastRequestedUrl = tab.url
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Solid Material 3 Expressive Bottom Navigation Bar
        Surface(
            color = tokens.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .padding(bottom = maxOf(navBarBottomPadding, 6.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        webViewInstance?.goBack()
                        feedback.playClick()
                    },
                    enabled = tab.canGoBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (tab.canGoBack) tokens.colorScheme.onSurface else tokens.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }

                IconButton(
                    onClick = {
                        webViewInstance?.goForward()
                        feedback.playClick()
                    },
                    enabled = tab.canGoForward
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (tab.canGoForward) tokens.colorScheme.onSurface else tokens.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }

                IconButton(
                    onClick = {
                        feedback.playSwoosh()
                        onHomeClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = tokens.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = {
                        feedback.playTabSwitch()
                        onTabsClick()
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .border(
                                width = 2.dp,
                                color = tokens.colorScheme.onSurface,
                                shape = RoundedCornerShape(7.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$totalTabCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = tokens.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
