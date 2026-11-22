package dev.yuyuyuyuyu.locationremover.testing

import js.typedarrays.Uint8Array
import js.typedarrays.toUint8Array
import web.file.File

object FileDialog {
    fun choose(jpeg: ByteArray) = replaceFileDialog(jpeg.toUint8Array())

    fun cancel() = replaceFileDialog(null)

    fun closeCount(): Int = fileDialogCloseCount()
}

object ShareSheet {
    fun complete() = replaceShareSheet(supported = true, dismissed = false)

    fun dismiss() = replaceShareSheet(supported = true, dismissed = true)

    fun beUnsupported() = replaceShareSheet(supported = false, dismissed = false)

    fun openCount(): Int = shareSheetOpenCount()

    fun sharedFileOrNull(): File? = sharedFile()
}

object NewTab {
    fun replace() = replaceNewTab()

    fun openedUrlOrNull(): String? = openedUrl()
}

fun restoreBrowser(): Unit =
    js(
        """{
        const test = globalThis.locationRemoverTest;
        if (test?.click) HTMLInputElement.prototype.click = test.click;
        if (test?.open) window.open = test.open;
        delete navigator.canShare;
        delete navigator.share;
        globalThis.locationRemoverTest = undefined;
    }""",
    )

@Suppress("UnusedParameter")
private fun replaceFileDialog(jpeg: Uint8Array<*>?): Unit =
    js(
        """{
        const test = (globalThis.locationRemoverTest ??= {});
        test.click ??= HTMLInputElement.prototype.click;
        test.fileDialogCloseCount = 0;
        HTMLInputElement.prototype.click = function () {
            if (this.type !== "file") {
                test.click.call(this);
                return;
            }
            test.fileDialogCloseCount++;
            if (jpeg === null) {
                this.dispatchEvent(new Event("cancel"));
                return;
            }
            const dataTransfer = new DataTransfer();
            dataTransfer.items.add(new File([jpeg], "picture.jpg", { type: "image/jpeg" }));
            this.files = dataTransfer.files;
            this.dispatchEvent(new Event("change"));
        };
    }""",
    )

private fun fileDialogCloseCount(): Int = js("globalThis.locationRemoverTest?.fileDialogCloseCount ?? 0")

@Suppress("UnusedParameter")
private fun replaceShareSheet(
    supported: Boolean,
    dismissed: Boolean,
): Unit =
    js(
        """{
        const test = (globalThis.locationRemoverTest ??= {});
        test.shareSheetOpenCount = 0;
        test.sharedFiles = null;
        const canShare = supported ? () => true : undefined;
        const share = supported
            ? async (data) => {
                  test.shareSheetOpenCount++;
                  if (dismissed) throw new DOMException("Share canceled", "AbortError");
                  test.sharedFiles = data.files;
              }
            : undefined;
        Object.defineProperty(navigator, "canShare", { configurable: true, value: canShare });
        Object.defineProperty(navigator, "share", { configurable: true, value: share });
    }""",
    )

private fun shareSheetOpenCount(): Int = js("globalThis.locationRemoverTest?.shareSheetOpenCount ?? 0")

private fun sharedFile(): File? = js("globalThis.locationRemoverTest?.sharedFiles?.[0] ?? null")

private fun replaceNewTab(): Unit =
    js(
        """{
        const test = (globalThis.locationRemoverTest ??= {});
        test.open ??= window.open;
        test.openedUrl = null;
        window.open = (url) => {
            test.openedUrl = String(url);
            return null;
        };
    }""",
    )

private fun openedUrl(): String? = js("globalThis.locationRemoverTest?.openedUrl ?? null")
