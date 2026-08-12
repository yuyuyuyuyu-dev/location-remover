package dev.yuyuyuyuyu.locationremover.image

import kotlinx.coroutines.suspendCancellableCoroutine
import web.blob.byteArray
import web.dom.document
import web.events.EventHandler
import web.events.addHandler
import web.html.HTMLInputElement
import web.html.HtmlTagName
import web.html.InputType
import web.html.cancelEvent
import web.html.changeEvent
import web.html.file
import kotlin.coroutines.resume

class BrowserImagePicker : ImagePicker {
    override suspend fun pickJpegOrNull(): ByteArray? {
        val input = document.createElement(HtmlTagName.input)
        input.type = InputType.file
        input.accept = JPEG_MIME_TYPE

        input.showDialogAndAwaitClose()

        return input.files?.item(0)?.byteArray()
    }
}

/**
 * The dialog is opened from inside, once both handlers are attached: an event that arrives in the
 * same turn as `click()` would otherwise reach nobody and suspend this call forever.
 *
 * A dismissed dialog fires `cancel` and never `change`, so `change` alone is not enough to wait on
 * either.
 */
private suspend fun HTMLInputElement.showDialogAndAwaitClose() =
    suspendCancellableCoroutine { continuation ->
        lateinit var unsubscribeFromChange: () -> Unit
        lateinit var unsubscribeFromCancel: () -> Unit

        val resume = {
            unsubscribeFromChange()
            unsubscribeFromCancel()
            continuation.resume(Unit)
        }

        unsubscribeFromChange = changeEvent.addHandler(EventHandler(resume))
        unsubscribeFromCancel = cancelEvent.addHandler(EventHandler(resume))

        continuation.invokeOnCancellation {
            unsubscribeFromChange()
            unsubscribeFromCancel()
        }

        click()
    }

actual fun createImagePicker(): ImagePicker = BrowserImagePicker()
