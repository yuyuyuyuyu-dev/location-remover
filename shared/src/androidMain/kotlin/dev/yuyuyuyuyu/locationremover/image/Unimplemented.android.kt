package dev.yuyuyuyuyu.locationremover.image

// Location Remover ships to the web only. Android is still a target of the shared module, so it
// needs these actuals to compile — but nothing behind them is written, and an Android build that
// reaches one fails on the spot rather than pretending to work.

actual fun createImagePicker(): ImagePicker =
    object : ImagePicker {
        override suspend fun pickJpegOrNull(): ByteArray? {
            TODO("Picking an image is implemented for the web only.")
        }
    }

actual fun createExifRemover(): ExifRemover =
    object : ExifRemover {
        override suspend fun removeExif(jpeg: ByteArray): ByteArray {
            TODO("Removing Exif is implemented for the web only.")
        }
    }

actual fun createImageDownloader(): ImageDownloader =
    object : ImageDownloader {
        override fun download(
            jpeg: ByteArray,
            fileName: String,
        ) {
            TODO("Downloading an image is implemented for the web only.")
        }
    }

actual fun createImageSharer(): ImageSharer =
    object : ImageSharer {
        override suspend fun tryShare(
            jpeg: ByteArray,
            fileName: String,
        ): Boolean {
            TODO("Sharing an image is implemented for the web only.")
        }
    }

actual fun createImageCopier(): ImageCopier =
    object : ImageCopier {
        override suspend fun tryCopy(jpeg: ByteArray): Boolean {
            TODO("Copying an image is implemented for the web only.")
        }
    }
