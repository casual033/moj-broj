package com.mojbroj.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MojBrojFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenIsVisible() {
        composeRule.onNodeWithText("Moj Broj").assertIsDisplayed()
        composeRule.onNodeWithText("Nova igra").assertIsDisplayed()
    }
}
