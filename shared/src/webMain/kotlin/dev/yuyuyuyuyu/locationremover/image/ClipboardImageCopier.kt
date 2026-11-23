package dev.yuyuyuyuyu.locationremover.image

import js.array.jsArrayOf
import js.coroutines.promise
import js.objects.recordOf
import kotlinx.coroutines.coroutineScope
import web.clipboard.ClipboardItem
import web.clipboard.write
import web.navigator.navigator

class ClipboardImageCopier : ImageCopier {
    override suspend fun tryCopy(jpeg: ByteArray): Boolean =
        runCatching {
            coroutineScope {
                val png = promise { jpeg.toJpegBlob().redrawnAs(PNG_MIME_TYPE) }
                val item = ClipboardItem(recordOf(PNG_MIME_TYPE.toJsString() to png))

                navigator.clipboard.write(jsArrayOf(item))
            }
        }.isSuccess

    private companion object {
        private const val PNG_MIME_TYPE = "image/png"
    }
}

actual fun createImageCopier(): ImageCopier = ClipboardImageCopier()
