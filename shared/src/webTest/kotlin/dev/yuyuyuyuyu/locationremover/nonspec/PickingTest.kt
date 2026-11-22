package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.anotherPicture
import dev.yuyuyuyuyu.locationremover.testing.awaitSettled
import dev.yuyuyuyuyu.locationremover.testing.awaitUntil
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.isShown
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.share
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class PickingTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should not offer to remove Exif when the file dialog is cancelled`() =
        runComposeUiTest {
            // Arrange
            FileDialog.cancel()
            setContent { App() }

            // Act
            click(Res.string.select_an_image)
            awaitUntil("the file dialog is closed") { FileDialog.closeCount() == 1 }
            awaitSettled()

            // Assert
            assertFalse(isShown(Res.string.remove_exif))
        }

    @Test
    fun `should hide the previous result when another picture is selected`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            awaitUntil("the result is shown") { isShown(Res.string.share) }
            FileDialog.choose(anotherPicture())

            // Act
            click(Res.string.select_an_image)

            // Assert
            awaitUntil("the previous result is hidden") { !isShown(Res.string.share) }
        }
}
