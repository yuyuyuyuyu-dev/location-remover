package dev.yuyuyuyuyu.locationremover.spec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.ShareSheet
import dev.yuyuyuyuyu.locationremover.testing.awaitSharedFile
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.hasLocation
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.share
import web.blob.byteArray
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class AppTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should share the picture the location was removed from`() =
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
}
