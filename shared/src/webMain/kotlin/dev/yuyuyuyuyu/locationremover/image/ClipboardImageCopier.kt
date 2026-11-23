package dev.yuyuyuyuyu.locationremover.image

import js.array.jsArrayOf
import js.closeable.use
import js.coroutines.promise
import js.objects.recordOf
import kotlinx.coroutines.coroutineScope
import web.blob.Blob
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.clipboard.ClipboardItem
import web.clipboard.write
import web.dom.document
import web.html.HtmlTagName
import web.html.toBlob
import web.images.createImageBitmap
import web.navigator.navigator

private const val PNG_MIME_TYPE = "image/png"

class ClipboardImageCopier : ImageCopier {
    override suspend fun tryCopy(jpeg: ByteArray): Boolean =
        runCatching {
            coroutineScope {
                val png = promise { jpeg.toPngBlob() }
                val item = ClipboardItem(recordOf<JsString, JsAny>(PNG_MIME_TYPE.toJsString() to png))
                navigator.clipboard.write(jsArrayOf(item))
            }
        }.isSuccess
}

private suspend fun ByteArray.toPngBlob(): Blob {
    val canvas = document.createElement(HtmlTagName.canvas)

    createImageBitmap(toJpegBlob()).use { bitmap ->
        canvas.width = bitmap.width
        canvas.height = bitmap.height

        val context =
            checkNotNull(canvas.getContext(CanvasRenderingContext2D.ID)) {
                "The browser does not provide a 2D canvas context."
            }
        context.drawImage(bitmap, 0.0, 0.0)
    }

    return canvas.toBlob(PNG_MIME_TYPE)
}

actual fun createImageCopier(): ImageCopier = ClipboardImageCopier()
