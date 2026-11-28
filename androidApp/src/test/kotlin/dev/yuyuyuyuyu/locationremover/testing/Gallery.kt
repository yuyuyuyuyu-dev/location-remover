package dev.yuyuyuyuyu.locationremover.testing

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import org.robolectric.Robolectric
import java.io.File

object Gallery {
    private const val AUTHORITY = "com.example.gallery"

    fun publish(jpeg: ByteArray): Uri {
        val file = File.createTempFile("picture", ".jpg").apply { deleteOnExit() }
        file.writeBytes(jpeg)
        val provider = Robolectric.setupContentProvider(GalleryProvider::class.java, AUTHORITY)
        provider.file = file

        return Uri.parse("content://$AUTHORITY/picture.jpg")
    }
}

class GalleryProvider : ContentProvider() {
    lateinit var file: File

    override fun onCreate(): Boolean = true

    override fun openFile(
        uri: Uri,
        mode: String,
    ): ParcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

    override fun getType(uri: Uri): String = "image/jpeg"

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun insert(
        uri: Uri,
        values: ContentValues?,
    ): Uri? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}
