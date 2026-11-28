package dev.yuyuyuyuyu.locationremover.testing

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun picture(): ByteArray {
    val bitmap = Bitmap.createBitmap(16, 8, Bitmap.Config.ARGB_8888)

    return ByteArrayOutputStream()
        .also { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        .toByteArray()
}
