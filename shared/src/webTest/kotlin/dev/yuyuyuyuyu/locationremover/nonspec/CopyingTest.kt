package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.Clipboard
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.awaitUntil
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.isShown
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.copied_to_clipboard
import locationremover.shared.generated.resources.copy
import locationremover.shared.generated.resources.copying_failed
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class CopyingTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should tell that the picture was copied to the clipboard`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.allow()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.copy)

            // Assert
            awaitUntil("the copy is told") { isShown(Res.string.copied_to_clipboard) }
        }

    @Test
    fun `should tell that the picture could not be copied when the browser refuses`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.refuse()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.copy)

            // Assert
            awaitUntil("the failure is told") { isShown(Res.string.copying_failed) }
            assertFalse(isShown(Res.string.copied_to_clipboard))
        }
}
