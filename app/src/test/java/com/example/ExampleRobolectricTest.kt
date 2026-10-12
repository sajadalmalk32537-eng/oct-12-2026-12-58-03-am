package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("طمأنينة", appName)
  }

  @Test
  fun `verify prayer alarm settings load defaults`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val settings = com.example.alarm.PrayerAlarmScheduler.loadSettings(context)
    org.junit.Assert.assertTrue(settings.isNotEmpty())
    val fajr = settings.find { it.prayerId == "fajr" }
    org.junit.Assert.assertNotNull(fajr)
    assertEquals("الفجر", fajr?.prayerName)
  }
}
