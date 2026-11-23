package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.NewTab
import dev.yuyuyuyuyu.locationremover.testing.clickMenuItem
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import dev.yuyuyuyuyu.mycomposables.SimpleTopAppBarTestTags
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class SourceCodeTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should open the source code`() =
        runComposeUiTest {
            // Arrange
            NewTab.replace()
            setContent { App() }

            // Act
            clickMenuItem(SimpleTopAppBarTestTags.SOURCE_CODE_BUTTON)

            // Assert
            assertEquals("https://github.com/yuyuyuyuyu-dev/location-remover", NewTab.openedUrlOrNull())
        }
}
