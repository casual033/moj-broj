package com.mojbroj.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MojBrojFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenIsVisible() {
        composeRule.onNodeWithText("Moj broj").assertIsDisplayed()
        composeRule.onNodeWithText("Standardni mod").assertIsDisplayed()
        composeRule.onNodeWithText("Dečiji mod").assertIsDisplayed()
        composeRule.onNodeWithText("Dnevni izazov").assertIsDisplayed()
    }

    @Test
    fun standardModeStartsGame() {
        composeRule.onNodeWithText("Standardni mod").performClick()
        composeRule.onNodeWithText("TRAŽENI BROJ").assertIsDisplayed()
        composeRule.onNodeWithText("POTVRDI").assertIsDisplayed()
    }
}
