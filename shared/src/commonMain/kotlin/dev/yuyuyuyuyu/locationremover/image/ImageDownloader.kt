package dev.yuyuyuyuyu.locationremover.image

interface ImageDownloader {
    fun download(jpeg: ByteArray, fileName: String)
}

expect fun createImageDownloader(): ImageDownloader
