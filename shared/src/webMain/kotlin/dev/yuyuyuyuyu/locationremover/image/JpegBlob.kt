package dev.yuyuyuyuyu.locationremover.image

import js.array.jsArrayOf
import js.objects.unsafeJso
import js.typedarrays.toUint8Array
import web.blob.Blob
import web.blob.BlobPropertyBag

internal const val JPEG_MIME_TYPE = "image/jpeg"

internal fun ByteArray.toJpegBlob(): Blob =
    Blob(
        blobParts = jsArrayOf(toUint8Array()),
        options = unsafeJso<BlobPropertyBag> { type = JPEG_MIME_TYPE },
    )
