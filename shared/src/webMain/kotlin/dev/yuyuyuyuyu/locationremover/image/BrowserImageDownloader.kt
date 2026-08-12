package dev.yuyuyuyuyu.locationremover.image

import web.dom.document
import web.html.HtmlTagName
import web.timers.setTimeout
import web.url.URL

class BrowserImageDownloader : ImageDownloader {
    override fun download(jpeg: ByteArray, fileName: String) {
        val objectUrl = URL.createObjectURL(jpeg.toJpegBlob())

        val link = document.createElement(HtmlTagName.a)
        link.download = fileName
        link.href = objectUrl
        link.click()

        // Revoking right away would pull the blob out from under a download the browser has not
        // started yet, so the URL is released only once it has had its turn.
        setTimeout({ URL.revokeObjectURL(objectUrl) })
    }
}

actual fun createImageDownloader(): ImageDownloader = BrowserImageDownloader()
