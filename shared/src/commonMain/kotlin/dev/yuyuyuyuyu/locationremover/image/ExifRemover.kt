package dev.yuyuyuyuyu.locationremover.image

interface ExifRemover {
    suspend fun removeExif(jpeg: ByteArray): ByteArray
}

expect fun createExifRemover(): ExifRemover
