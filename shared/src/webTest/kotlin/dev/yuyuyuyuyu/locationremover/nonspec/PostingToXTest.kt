package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.Clipboard
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.NewTab
import dev.yuyuyuyuyu.locationremover.testing.awaitSettled
import dev.yuyuyuyuyu.locationremover.testing.awaitUntil
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.clickWithoutScrolling
import dev.yuyuyuyuyu.locationremover.testing.isShown
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.cancel
import locationremover.shared.generated.resources.copying_failed
import locationremover.shared.generated.resources.image_copied
import locationremover.shared.generated.resources.post_to_x
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

@OptIn(ExperimentalTestApi::class)
class PostingToXTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should not open X when cancel is clicked`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.allow()
            NewTab.replace()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)
            click(Res.string.post_to_x)

            // Act
            clickWithoutScrolling(Res.string.cancel)

            // Assert
            awaitUntil("the dialog is closed") { !isShown(Res.string.image_copied) }
            assertNull(NewTab.openedUrlOrNull())
        }

    @Test
    fun `should tell that the picture cannot be posted to X when the browser cannot copy it`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Clipboard.refuse()
            NewTab.replace()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.post_to_x)

            // Assert
            awaitUntil("the failure is told") { isShown(Res.string.copying_failed) }
            awaitSettled()
            assertFalse(isShown(Res.string.image_copied))
            assertNull(NewTab.openedUrlOrNull())
        }
}
