package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SolidWallpaperColor
import com.example.model.TopNavSegment
import com.example.viewmodel.BrowserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Prism", appName)
  }

  @Test
  fun `viewmodel starts with dynamic phone wallpaper and favorites segment`() {
    val viewModel = BrowserViewModel()
    val state = viewModel.uiState.value

    assertEquals(SolidWallpaperColor.PHONE_DYNAMIC, state.solidWallpaperColor)
    assertEquals(TopNavSegment.FAVORITES, state.selectedSegment)
    assertTrue(state.favorites.isNotEmpty())
    assertTrue(state.readingList.isNotEmpty())

    viewModel.cycleWallpaperColor()
    val cycledState = viewModel.uiState.value
    assertEquals(SolidWallpaperColor.DEEP_PLUM, cycledState.solidWallpaperColor)
  }

  @Test
  fun `navigation formats urls and records history`() {
    val viewModel = BrowserViewModel()
    viewModel.navigateTo("eraceramics.com")

    val state = viewModel.uiState.value
    assertNotNull(state.activeTabId)
    val activeTab = viewModel.getActiveTab()
    assertEquals("https://eraceramics.com", activeTab?.url)
    assertTrue(state.history.any { it.url == "https://eraceramics.com" })
  }
}
