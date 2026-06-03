/* Copyright (c) 2026 iamasrakib. All rights reserved. */
package com.alhissn.shield

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FullJourneyTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val TEST_EMAIL = "iamasrakib@gmail.com"
    private val TEST_PASSWORD = "SELINA1r@"
    private val TEST_ACCESS_CODE = "0JISMB"

    @Test
    fun testAuthenticationAndDashboardJourney() {
        // Wait for Splash to finish
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
        }

        val signInNodes = composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes()
        if (signInNodes.isNotEmpty()) {
            
            // Fill email
            composeTestRule.onNodeWithText("Email Address").performTextInput(TEST_EMAIL)
            // Fill password
            composeTestRule.onNodeWithText("Password").performTextInput(TEST_PASSWORD)
            
            // Try to click Sign In now
            val signInBtn = composeTestRule.onAllNodesWithText("Sign In", useUnmergedTree = true)
            if (signInBtn.fetchSemanticsNodes().isNotEmpty()) {
                signInBtn[0].performClick()
                composeTestRule.waitForIdle()
            }
        }

        // 2. Check if we are on the Access Code screen
        try {
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            // DUMP THE UI TREE TO SEE THE ERROR MESSAGE
            val tree = composeTestRule.onRoot().printToString()
            throw AssertionError("Failed to proceed past login. UI Tree:\n$tree", e)
        }

        val unlockNodes = composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes()
        if (unlockNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText("6-Digit One-Time Code").performTextInput(TEST_ACCESS_CODE)
            composeTestRule.onAllNodesWithText("Unlock Protection", useUnmergedTree = true)[0].performClick()
            composeTestRule.waitForIdle()
        }

        // 3. Verify Dashboard is reached by checking for "AL HISSN SHIELD" or "Home" ContentDescription
        try {
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText("AL HISSN SHIELD", substring = true).fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodesWithContentDescription("Home").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            val tree = composeTestRule.onRoot().printToString()
            throw AssertionError("Access code rejected or failed to load Dashboard. UI Tree:\n$tree", e)
        }
        
        composeTestRule.onNodeWithContentDescription("Home").assertExists()

        // 4. Navigate to Focus tab
        composeTestRule.onNodeWithContentDescription("Focus").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        // 5. Navigate to Streak tab
        composeTestRule.onNodeWithContentDescription("Streak").assertExists().performClick()
        composeTestRule.waitForIdle()

        // 6. Navigate to Settings
        composeTestRule.onNodeWithContentDescription("Settings").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        // 7. Go back to Home and test Feature Cards
        composeTestRule.onNodeWithContentDescription("Home").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        // Click Network Guard
        if (composeTestRule.onAllNodesWithText("Network Guard", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Network Guard", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            // Press back hardware button to simulate human navigation
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        // Click Screen Shield
        if (composeTestRule.onAllNodesWithText("Screen Shield", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Screen Shield", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        // Click Storage Scan
        if (composeTestRule.onAllNodesWithText("Storage Scan", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Storage Scan", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        // Click Device Health
        if (composeTestRule.onAllNodesWithText("Device Health", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Device Health", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }
    }
}


