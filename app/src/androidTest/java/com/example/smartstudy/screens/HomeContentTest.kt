package com.example.smartstudy.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class HomeContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeContent_rendersCorrectly() {
        composeTestRule.setContent {
            HomeContent()
        }

        // Check if "Welcome Back" is displayed
        composeTestRule.onNodeWithText("Welcome Back 👋").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vishwa").assertIsDisplayed()

        // Check Study Streak
        composeTestRule.onNodeWithText("🔥 Study Streak").assertIsDisplayed()
        composeTestRule.onNodeWithText("7 Days").assertIsDisplayed()

        // Check User Info Card
        composeTestRule.onNodeWithText("B.Tech - CSE").assertIsDisplayed()
        composeTestRule.onNodeWithText("SIMATS University").assertIsDisplayed()

        // Check Stat Cards
        composeTestRule.onNodeWithText("Courses").assertIsDisplayed()
        composeTestRule.onNodeWithText("4").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tests").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Avg Score").assertIsDisplayed()
        composeTestRule.onNodeWithText("85%").assertIsDisplayed()

        // Check Progress
        composeTestRule.onNodeWithText("📚 Course Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("72% Completed").assertIsDisplayed()

        // Check Quick Access
        composeTestRule.onNodeWithText("Quick Access").assertIsDisplayed()
        composeTestRule.onNodeWithText("AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Progress").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rank").assertIsDisplayed()

        // Check AI Prediction
        composeTestRule.onNodeWithText("AI Prediction Score").assertIsDisplayed()

        // Check Today's Plan
        composeTestRule.onNodeWithText("Today's Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("Practice DSA Problems").assertIsDisplayed()
        composeTestRule.onNodeWithText("Java Interview Questions").assertIsDisplayed()
    }

    @Test
    fun quickCard_rendersCorrectly() {
        composeTestRule.setContent {
            QuickCard(title = "Test Title", emoji = "🧪")
        }

        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("🧪").assertIsDisplayed()
    }

    @Test
    fun sectionCard_rendersCorrectly() {
        composeTestRule.setContent {
            SectionCard(title = "Test Section", subtitle = "Test Subtitle")
        }

        composeTestRule.onNodeWithText("Test Section").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Subtitle").assertIsDisplayed()
    }

    @Test
    fun planCard_rendersCorrectly() {
        composeTestRule.setContent {
            PlanCard(title = "Test Plan", time = "10:00 AM")
        }

        composeTestRule.onNodeWithText("Test Plan").assertIsDisplayed()
        composeTestRule.onNodeWithText("10:00 AM").assertIsDisplayed()
    }

    @Test
    fun statCard_rendersCorrectly() {
        composeTestRule.setContent {
            StatCard(title = "Points", value = "100")
        }

        composeTestRule.onNodeWithText("Points").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }
}
