package dev.yuyuyuyuyu.locationremover.image

import js.closeable.use
import web.blob.byteArray
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.dom.document
import web.html.HtmlTagName
import web.html.toBlob
import web.images.createImageBitmap

class CanvasExifRemover : ExifRemover {
    /**
     * Drawing the decoded pixels onto a canvas and encoding them again yields a brand new JPEG.
     * The metadata of the original — the Exif block, and the location inside it — is never part of
     * those pixels, so it has nowhere to survive.
     */
    override suspend fun removeExif(jpeg: ByteArray): ByteArray {
        val canvas = document.createElement(HtmlTagName.canvas)

        createImageBitmap(jpeg.toJpegBlob()).use { bitmap ->
            canvas.width = bitmap.width
            canvas.height = bitmap.height

            val context = checkNotNull(canvas.getContext(CanvasRenderingContext2D.ID)) {
                "The browser does not provide a 2D canvas context."
            }
            context.drawImage(bitmap, 0.0, 0.0)
        }

        return canvas.toBlob(JPEG_MIME_TYPE).byteArray()
    }
}

actual fun createExifRemover(): ExifRemover = CanvasExifRemover()
