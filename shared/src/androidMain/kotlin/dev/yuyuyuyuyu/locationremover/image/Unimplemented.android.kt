package dev.yuyuyuyuyu.locationremover.image

// Location Remover ships to the web only. Android is still a target of the shared module, so it
// needs these actuals to compile — but nothing behind them is written, and an Android build that
// reaches one fails on the spot rather than pretending to work.

actual fun createImagePicker(): ImagePicker = TODO("Picking an image is implemented for the web only.")

actual fun createExifRemover(): ExifRemover = TODO("Removing Exif is implemented for the web only.")

actual fun createImageDownloader(): ImageDownloader = TODO("Downloading an image is implemented for the web only.")

actual fun createImageSharer(): ImageSharer = TODO("Sharing an image is implemented for the web only.")
