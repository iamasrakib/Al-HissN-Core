/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.util.Log

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 26)
class E2EUITest {

    private lateinit var device: UiDevice
    private val appPackage = "com.alhissn.shield"
    private val launcherTimeout = 5000L

    @Before
    fun startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage: String = device.launcherPackageName
        assertNotNull(launcherPackage)
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            launcherTimeout
        )

        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(appPackage)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        device.wait(
            Until.hasObject(By.pkg(appPackage).depth(0)),
            launcherTimeout
        )
    }

    @Test
    fun testDeepAppInteractionAndNavigation() {
        val appHasLaunched = device.wait(Until.hasObject(By.pkg(appPackage)), 5000)
        assertTrue("App did not launch properly", appHasLaunched)

        // Try to click standard bottom nav tabs or menu items to stress test navigation
        val textsToFind = listOf("Network", "Settings", "Profile", "Home", "Scan", "Optimizer", "Shield", "Block")
        textsToFind.forEach { tabName ->
            val tab = device.findObject(By.textContains(tabName))
            if (tab != null) {
                Log.d("E2EUITest", "Clicking on $tabName")
                try {
                    tab.click()
                    device.waitForIdle()
                    Thread.sleep(500) // allow animation to finish
                } catch (e: Exception) {
                    Log.e("E2EUITest", "Failed to click on $tabName: ${e.message}")
                }
            }
        }
        
        // Cycle app to background and foreground
        device.pressHome()
        device.waitForIdle()
        Thread.sleep(1000)
        
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(appPackage)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        
        val appResumed = device.wait(Until.hasObject(By.pkg(appPackage)), 5000)
        assertTrue("App did not resume properly after backgrounding", appResumed)
    }
}


