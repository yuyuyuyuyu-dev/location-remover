package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.NewTab
import dev.yuyuyuyuyu.locationremover.testing.clickWithoutScrolling
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.source_code
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
            clickWithoutScrolling(Res.string.source_code)

            // Assert
            assertEquals("https://github.com/yuyuyuyuyu-dev/location-remover", NewTab.openedUrlOrNull())
        }
}
