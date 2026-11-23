package dev.yuyuyuyuyu.locationremover.spec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.Clipboard
import dev.yuyuyuyuyu.locationremover.testing.Downloads
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.NewTab
import dev.yuyuyuyuyu.locationremover.testing.ShareSheet
import dev.yuyuyuyuyu.locationremover.testing.awaitCopiedImage
import dev.yuyuyuyuyu.locationremover.testing.awaitDownloadedFile
import dev.yuyuyuyuyu.locationremover.testing.awaitSharedFile
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.clickMenuItem
import dev.yuyuyuyuyu.locationremover.testing.clickWithoutScrolling
import dev.yuyuyuyuyu.locationremover.testing.hasLocation
import dev.yuyuyuyuyu.locationremover.testing.isShown
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import dev.yuyuyuyuyu.locationremover.testing.scrollTo
import dev.yuyuyuyuyu.mycomposables.SimpleTopAppBarTestTags
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.copy
import locationremover.shared.generated.resources.download
import locationremover.shared.generated.resources.open_x
import locationremover.shared.generated.resources.post_to_x
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.share
import web.blob.byteArray
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class AppTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should post the picture the location was removed from to X`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.allow()
            NewTab.replace()
            setContent { App() }

            // Act
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.post_to_x)
            clickWithoutScrolling(Res.string.open_x)

            // Assert
            assertFalse(awaitCopiedImage().byteArray().hasLocation())
            assertEquals("https://x.com/intent/post", NewTab.openedUrlOrNull())
        }

    @Test
    fun `should copy the picture the location was removed from to the clipboard`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.allow()
            setContent { App() }

            // Act
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.copy)

            // Assert
            assertFalse(awaitCopiedImage().byteArray().hasLocation())
        }

    @Test
    fun `should download the picture the location was removed from`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Downloads.replace()
            setContent { App() }

            // Act
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.download)

            // Assert
            assertFalse(awaitDownloadedFile().byteArray().hasLocation())
        }

    @Test
    fun `should share the picture the location was removed from where the browser can share`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.complete()
            setContent { App() }

            // Act
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.share)

            // Assert
            assertFalse(awaitSharedFile().byteArray().hasLocation())
        }

    @Test
    fun `should show the licenses of the dependencies`() =
        runComposeUiTest {
            // Arrange
            setContent { App() }

            // Act
            clickMenuItem(SimpleTopAppBarTestTags.OPEN_SOURCE_LICENSES_BUTTON)
            scrollTo("kotlin-browser")

            // Assert
            assertTrue(isShown("kotlin-browser"))
            assertTrue(isShown("Apache License 2.0"))
        }
}
