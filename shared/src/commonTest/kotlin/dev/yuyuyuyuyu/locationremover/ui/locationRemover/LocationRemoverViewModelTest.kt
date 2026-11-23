package dev.yuyuyuyuyu.locationremover.ui.locationRemover

import androidx.compose.ui.platform.UriHandler
import dev.yuyuyuyuyu.locationremover.image.ExifRemover
import dev.yuyuyuyuyu.locationremover.image.ImageCopier
import dev.yuyuyuyuyu.locationremover.image.ImageDownloader
import dev.yuyuyuyuyu.locationremover.image.ImagePicker
import dev.yuyuyuyuyu.locationremover.image.ImageSharer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRemoverViewModelTest {
    @BeforeTest
    fun setUpMainDispatcher() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    @Test
    @Suppress("ForbiddenComment")
    fun `should remove the location from the picture`() {
        // TODO: The canvas removes the location, and a fake canvas would prove nothing. Write this
        //  against a real browser.
    }

    @Test
    @Suppress("ForbiddenComment")
    fun `should copy the picture the location was removed from to the clipboard`() {
        // TODO: Not implemented yet.
    }

    @Test
    fun `should post the picture the location was removed from to X`() =
        runTest {
            // Arrange
            val imageCopier = FakeImageCopier(canCopy = true)
            val uriHandler = FakeUriHandler()
            val viewModel = createViewModel(imageCopier = imageCopier, uriHandler = uriHandler)
            viewModel.onSelectImageButtonClicked()
            viewModel.onRemoveExifButtonClicked()

            // Act
            viewModel.onPostToXButtonClicked()
            viewModel.onOpenXButtonClicked()

            // Assert
            assertContentEquals(EXIF_REMOVED_JPEG, imageCopier.copiedJpeg)
            assertEquals("https://x.com/intent/post", uriHandler.openedUri)
        }

    @Test
    fun `should tell that the picture cannot be posted to X where the browser cannot copy it`() =
        runTest {
            // Arrange
            val uriHandler = FakeUriHandler()
            val viewModel =
                createViewModel(imageCopier = FakeImageCopier(canCopy = false), uriHandler = uriHandler)
            viewModel.onSelectImageButtonClicked()
            viewModel.onRemoveExifButtonClicked()

            // Act
            viewModel.onPostToXButtonClicked()

            // Assert
            assertTrue(viewModel.uiState.value.copyingIsNotSupportedDialogIsShown)
            assertNull(uriHandler.openedUri)
        }

    @Test
    fun `should download the picture the location was removed from`() =
        runTest {
            // Arrange
            val imageDownloader = FakeImageDownloader()
            val viewModel = createViewModel(imageDownloader = imageDownloader)
            viewModel.onSelectImageButtonClicked()
            viewModel.onRemoveExifButtonClicked()

            // Act
            viewModel.onDownloadButtonClicked()

            // Assert
            assertContentEquals(EXIF_REMOVED_JPEG, imageDownloader.downloadedJpeg)
            assertEquals("image.jpg", imageDownloader.downloadedFileName)
        }

    private fun createViewModel(
        imagePicker: ImagePicker = FakeImagePicker(PICKED_JPEG),
        exifRemover: ExifRemover = FakeExifRemover(EXIF_REMOVED_JPEG),
        imageDownloader: ImageDownloader = FakeImageDownloader(),
        imageSharer: ImageSharer = FakeImageSharer(canShare = true),
        imageCopier: ImageCopier = FakeImageCopier(canCopy = true),
        uriHandler: UriHandler = FakeUriHandler(),
    ) = LocationRemoverViewModel(imagePicker, exifRemover, imageDownloader, imageSharer, imageCopier, uriHandler)

    private companion object {
        private val PICKED_JPEG = byteArrayOf(1, 2, 3)
        private val EXIF_REMOVED_JPEG = byteArrayOf(4, 5, 6)
    }
}

private class FakeImagePicker(
    private val jpeg: ByteArray?,
) : ImagePicker {
    override suspend fun pickJpegOrNull(): ByteArray? = jpeg
}

private class FakeExifRemover(
    private val exifRemovedJpeg: ByteArray,
) : ExifRemover {
    override suspend fun removeExif(jpeg: ByteArray): ByteArray = exifRemovedJpeg
}

private class FakeImageDownloader : ImageDownloader {
    var downloadedJpeg: ByteArray? = null
        private set
    var downloadedFileName: String? = null
        private set

    override fun download(
        jpeg: ByteArray,
        fileName: String,
    ) {
        downloadedJpeg = jpeg
        downloadedFileName = fileName
    }
}

private class FakeImageSharer(
    private val canShare: Boolean,
) : ImageSharer {
    override suspend fun tryShare(
        jpeg: ByteArray,
        fileName: String,
    ): Boolean = canShare
}

private class FakeImageCopier(
    private val canCopy: Boolean,
) : ImageCopier {
    var copiedJpeg: ByteArray? = null
        private set

    override suspend fun tryCopy(jpeg: ByteArray): Boolean {
        if (canCopy) copiedJpeg = jpeg
        return canCopy
    }
}

private class FakeUriHandler : UriHandler {
    var openedUri: String? = null
        private set

    override fun openUri(uri: String) {
        openedUri = uri
    }
}
