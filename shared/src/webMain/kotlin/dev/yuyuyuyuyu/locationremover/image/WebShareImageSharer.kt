package dev.yuyuyuyuyu.locationremover.image

import js.array.jsArrayOf
import js.objects.unsafeJso
import js.typedarrays.toUint8Array
import web.file.File
import web.file.FilePropertyBag
import web.navigator.navigator
import web.navigator.share
import web.share.ShareData

class WebShareImageSharer : ImageSharer {
    override suspend fun tryShare(jpeg: ByteArray, fileName: String): Boolean {
        val file = File(
            fileBits = jsArrayOf(jpeg.toUint8Array()),
            fileName = fileName,
            options = unsafeJso<FilePropertyBag> { type = JPEG_MIME_TYPE },
        )
        val shareData = unsafeJso<ShareData> { files = jsArrayOf(file) }

        // Browsers without the Web Share API have no canShare either, so reaching it at all is
        // part of the answer.
        val canShare = runCatching { navigator.canShare(shareData) }.getOrDefault(false)
        if (!canShare) return false

        // A share sheet the user dismisses rejects the call, which is a choice rather than the
        // missing support the caller asks about.
        runCatching { navigator.share(shareData) }
        return true
    }
}

actual fun createImageSharer(): ImageSharer = WebShareImageSharer()
