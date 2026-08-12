package dev.yuyuyuyuyu.locationremover.ui.locationRemover

import dev.yuyuyuyuyu.locationremover.image.ExifRemover
import dev.yuyuyuyuyu.locationremover.image.ImageDownloader
import dev.yuyuyuyuyu.locationremover.image.ImagePicker
import dev.yuyuyuyuyu.locationremover.image.ImageSharer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

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
    fun `should remove the location from the picture`() = runTest {
        // Arrange
        val viewModel = createViewModel()
        viewModel.onSelectImageButtonClicked()

        // Act
        viewModel.onRemoveExifButtonClicked()

        // Assert
        assertContentEquals(EXIF_REMOVED_JPEG, viewModel.uiState.value.exifRemovedJpeg)
    }

    @Test
    fun `should download the picture the location was removed from`() = runTest {
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
        imageSharer: ImageSharer = FakeImageSharer(canShare = true)
    ) = LocationRemoverViewModel(imagePicker, exifRemover, imageDownloader, imageSharer)

    private companion object {
        private val PICKED_JPEG = byteArrayOf(1, 2, 3)
        private val EXIF_REMOVED_JPEG = byteArrayOf(4, 5, 6)
    }
}

private class FakeImagePicker(private val jpeg: ByteArray?) : ImagePicker {
    override suspend fun pickJpegOrNull(): ByteArray? = jpeg
}

private class FakeExifRemover(private val exifRemovedJpeg: ByteArray) : ExifRemover {
    override suspend fun removeExif(jpeg: ByteArray): ByteArray = exifRemovedJpeg
}

private class FakeImageDownloader : ImageDownloader {
    var downloadedJpeg: ByteArray? = null
        private set
    var downloadedFileName: String? = null
        private set

    override fun download(jpeg: ByteArray, fileName: String) {
        downloadedJpeg = jpeg
        downloadedFileName = fileName
    }
}

private class FakeImageSharer(private val canShare: Boolean) : ImageSharer {
    override suspend fun tryShare(jpeg: ByteArray, fileName: String): Boolean = canShare
}
