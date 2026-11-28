package dev.yuyuyuyuyu.locationremover

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.IntentCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sharedJpegUri = intent.sharedJpegUriOrNull()

        setContent {
            val sharedJpeg by produceState<ByteArray?>(initialValue = null, sharedJpegUri) {
                value = sharedJpegUri?.let { withContext(Dispatchers.IO) { readOrNull(it) } }
            }

            App(sharedJpeg = sharedJpeg)
        }
    }

    private fun readOrNull(uri: Uri): ByteArray? =
        try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (_: IOException) {
            null
        } catch (_: SecurityException) {
            null
        }
}

private fun Intent.sharedJpegUriOrNull(): Uri? {
    if (action != Intent.ACTION_SEND || type != JPEG_MIME_TYPE) return null

    return IntentCompat
        .getParcelableExtra(this, Intent.EXTRA_STREAM, Uri::class.java)
        ?.takeIf { it.scheme == ContentResolver.SCHEME_CONTENT }
}

private const val JPEG_MIME_TYPE = "image/jpeg"

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
