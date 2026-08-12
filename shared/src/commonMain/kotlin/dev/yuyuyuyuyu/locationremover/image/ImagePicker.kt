package dev.yuyuyuyuyu.locationremover.image

interface ImagePicker {
    suspend fun pickJpegOrNull(): ByteArray?
}

expect fun createImagePicker(): ImagePicker
