package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.ShareSheet
import dev.yuyuyuyuyu.locationremover.testing.awaitSettled
import dev.yuyuyuyuyu.locationremover.testing.awaitSharedFile
import dev.yuyuyuyuyu.locationremover.testing.awaitUntil
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.clickWithoutScrolling
import dev.yuyuyuyuyu.locationremover.testing.isShown
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.ok
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.share
import locationremover.shared.generated.resources.sharing_is_not_supported
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class SharingTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should share a JPEG file named image jpg`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.complete()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.share)

            // Assert
            val sharedFile = awaitSharedFile()
            assertEquals("image.jpg", sharedFile.name)
            assertEquals("image/jpeg", sharedFile.type)
        }

    @Test
    fun `should tell that sharing is not supported when the browser cannot share`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.beUnsupported()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.share)

            // Assert
            awaitUntil("sharing is told to be not supported") { isShown(Res.string.sharing_is_not_supported) }
        }

    @Test
    fun `should stop telling that sharing is not supported when OK is clicked`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.beUnsupported()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.share)

            // Act
            clickWithoutScrolling(Res.string.ok)

            // Assert
            awaitUntil("sharing is no longer told to be not supported") {
                !isShown(Res.string.sharing_is_not_supported)
            }
        }

    @Test
    fun `should not tell that sharing is not supported when the share sheet is dismissed`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.dismiss()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.share)
            awaitUntil("the share sheet is opened") { ShareSheet.openCount() == 1 }
            awaitSettled()

            // Assert
            assertFalse(isShown(Res.string.sharing_is_not_supported))
        }
}
