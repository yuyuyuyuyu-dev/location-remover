package dev.yuyuyuyuyu.locationremover.image

import js.closeable.use
import web.blob.Blob
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.dom.document
import web.html.HtmlTagName
import web.html.toBlob
import web.images.createImageBitmap

internal suspend fun Blob.redrawnAs(mimeType: String): Blob {
    val canvas = document.createElement(HtmlTagName.canvas)

    createImageBitmap(this).use { bitmap ->
        canvas.width = bitmap.width
        canvas.height = bitmap.height

        val context =
            checkNotNull(canvas.getContext(CanvasRenderingContext2D.ID)) {
                "The browser does not provide a 2D canvas context."
            }
        context.drawImage(bitmap, 0.0, 0.0)
    }

    return canvas.toBlob(mimeType)
}
