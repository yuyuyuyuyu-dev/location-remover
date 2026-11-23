package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.Downloads
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.awaitDownloadedFile
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.download
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class DownloadingTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should download a JPEG file named image jpg`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            Downloads.replace()
            setContent { App() }
            click(Res.string.select_an_image)
            click(Res.string.remove_exif)

            // Act
            click(Res.string.download)

            // Assert
            val downloadedFile = awaitDownloadedFile()
            assertEquals("image.jpg", downloadedFile.name)
            assertEquals("image/jpeg", downloadedFile.type)
        }
}
