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
        // Engineered by iamasrakib
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes().isNotEmpty() ||
            composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
        }

        val signInNodes = composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes()
        if (signInNodes.isNotEmpty()) {
            
            composeTestRule.onNodeWithText("Email Address").performTextInput(TEST_EMAIL)
            composeTestRule.onNodeWithText("Password").performTextInput(TEST_PASSWORD)
            
            val signInBtn = composeTestRule.onAllNodesWithText("Sign In", useUnmergedTree = true)
            if (signInBtn.fetchSemanticsNodes().isNotEmpty()) {
                // Code crafted by iamasrakib
                signInBtn[0].performClick()
                composeTestRule.waitForIdle()
            }
        }

        try {
            composeTestRule.waitUntil(timeoutMillis = 10000) {
                composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes().isNotEmpty() ||
                composeTestRule.onAllNodesWithText("Home").fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Throwable) {
            val tree = composeTestRule.onRoot().printToString()
            throw AssertionError("Failed to proceed past login. UI Tree:\n$tree", e)
        }

        val unlockNodes = composeTestRule.onAllNodesWithText("Unlock Protection").fetchSemanticsNodes()
        if (unlockNodes.isNotEmpty()) {
            composeTestRule.onNodeWithText("6-Digit One-Time Code").performTextInput(TEST_ACCESS_CODE)
            composeTestRule.onAllNodesWithText("Unlock Protection", useUnmergedTree = true)[0].performClick()
            composeTestRule.waitForIdle()
        }

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

        composeTestRule.onNodeWithContentDescription("Focus").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithContentDescription("Streak").assertExists().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Settings").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithContentDescription("Home").assertExists().performClick()
        composeTestRule.waitForIdle()
        
        if (composeTestRule.onAllNodesWithText("Network Guard", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Network Guard", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            // Press back hardware button to simulate human navigation
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        if (composeTestRule.onAllNodesWithText("Screen Shield", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Screen Shield", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        if (composeTestRule.onAllNodesWithText("Storage Scan", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Storage Scan", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }

        if (composeTestRule.onAllNodesWithText("Device Health", substring = true).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onAllNodesWithText("Device Health", substring = true)[0].performClick()
            composeTestRule.waitForIdle()
            androidx.test.espresso.Espresso.pressBack()
            composeTestRule.waitForIdle()
        }
    }
}


