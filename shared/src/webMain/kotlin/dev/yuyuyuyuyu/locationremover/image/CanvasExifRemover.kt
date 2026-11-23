package dev.yuyuyuyuyu.locationremover.image

import web.blob.byteArray

class CanvasExifRemover : ExifRemover {
    /**
     * Drawing the decoded pixels onto a canvas and encoding them again yields a brand new JPEG.
     * The metadata of the original — the Exif block, and the location inside it — is never part of
     * those pixels, so it has nowhere to survive.
     */
    override suspend fun removeExif(jpeg: ByteArray): ByteArray {
        val exifRemovedJpeg = jpeg.toJpegBlob().redrawnAs(JPEG_MIME_TYPE)
        return exifRemovedJpeg.byteArray()
    }
}

actual fun createExifRemover(): ExifRemover = CanvasExifRemover()
