package dev.yuyuyuyuyu.locationremover.image

interface ImageCopier {
    suspend fun tryCopy(jpeg: ByteArray): Boolean
}

expect fun createImageCopier(): ImageCopier
