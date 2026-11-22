package dev.yuyuyuyuyu.locationremover.nonspec

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import dev.yuyuyuyuyu.locationremover.App
import dev.yuyuyuyuyu.locationremover.testing.FileDialog
import dev.yuyuyuyuyu.locationremover.testing.PICTURE_SIZE
import dev.yuyuyuyuyu.locationremover.testing.PictureSize
import dev.yuyuyuyuyu.locationremover.testing.ShareSheet
import dev.yuyuyuyuyu.locationremover.testing.awaitSharedFile
import dev.yuyuyuyuyu.locationremover.testing.click
import dev.yuyuyuyuyu.locationremover.testing.pictureRotatedClockwiseByExif
import dev.yuyuyuyuyu.locationremover.testing.pictureSize
import dev.yuyuyuyuyu.locationremover.testing.pictureWithLocation
import dev.yuyuyuyuyu.locationremover.testing.restoreBrowser
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.share
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class RemovingTest {
    @AfterTest
    fun restoreTheBrowser() = restoreBrowser()

    @Test
    fun `should keep the size of the picture`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureWithLocation())
            ShareSheet.complete()
            setContent { App() }
            click(Res.string.select_an_image)

            // Act
            click(Res.string.remove_exif)
            click(Res.string.share)

            // Assert
            assertEquals(PICTURE_SIZE, awaitSharedFile().pictureSize())
        }

    @Test
    fun `should keep the picture upright when Exif rotates it`() =
        runComposeUiTest {
            // Arrange
            FileDialog.choose(pictureRotatedClockwiseByExif())
            ShareSheet.complete()
            setContent { App() }
            click(Res.string.select_an_image)

            // Act
            click(Res.string.remove_exif)
            click(Res.string.share)

            // Assert
            assertEquals(
                PictureSize(width = PICTURE_SIZE.height, height = PICTURE_SIZE.width),
                awaitSharedFile().pictureSize(),
            )
        }
}
