package dev.yuyuyuyuyu.locationremover.image

interface ImageSharer {
    suspend fun tryShare(jpeg: ByteArray, fileName: String): Boolean
}

expect fun createImageSharer(): ImageSharer
