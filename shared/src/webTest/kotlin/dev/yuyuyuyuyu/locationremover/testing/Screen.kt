package dev.yuyuyuyuyu.locationremover.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import dev.yuyuyuyuyu.mycomposables.SimpleTopAppBarTestTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import web.blob.Blob
import web.file.File
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeUiTest.click(text: StringResource) {
    val label = getString(text)
    awaitUntil("\"$label\" is shown") { isShown(text) }
    onNodeWithText(label).performScrollTo().performClick()
}

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeUiTest.clickWithoutScrolling(text: StringResource) {
    val label = getString(text)
    awaitUntil("\"$label\" is shown") { isShown(text) }
    onNodeWithText(label).performClick()
}

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeUiTest.clickMenuItem(itemTag: String) {
    awaitUntil("the menu button is shown") { isTagShown(SimpleTopAppBarTestTags.MENU_BUTTON) }
    onNodeWithTag(SimpleTopAppBarTestTags.MENU_BUTTON).performClick()
    awaitUntil("the menu item $itemTag is shown") { isTagShown(itemTag) }
    onNodeWithTag(itemTag).performClick()
}

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeUiTest.scrollTo(text: String) =
    awaitUntil("\"$text\" is scrolled to") {
        runCatching { onNode(hasScrollToNodeAction()).performScrollToNode(hasText(text)) }.isSuccess
    }

@OptIn(ExperimentalTestApi::class)
suspend fun ComposeUiTest.isShown(text: StringResource): Boolean = isShown(getString(text))

@OptIn(ExperimentalTestApi::class)
fun ComposeUiTest.isShown(text: String): Boolean = onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

@OptIn(ExperimentalTestApi::class)
private fun ComposeUiTest.isTagShown(tag: String): Boolean = onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()

suspend fun awaitSharedFile(): File {
    awaitUntil("a file is shared") { ShareSheet.sharedFileOrNull() != null }
    return checkNotNull(ShareSheet.sharedFileOrNull())
}

suspend fun awaitCopiedImage(): Blob {
    awaitUntil("an image is copied") { Clipboard.copiedImageOrNull() != null }
    return checkNotNull(Clipboard.copiedImageOrNull())
}

suspend fun awaitDownloadedFile(): File {
    awaitUntil("a file is downloaded") { Downloads.downloadedFileOrNull() != null }
    return checkNotNull(Downloads.downloadedFileOrNull())
}

suspend fun awaitUntil(
    description: String,
    condition: suspend () -> Boolean,
) {
    repeat((TIMEOUT / INTERVAL).toInt()) {
        if (condition()) return
        waitInRealTime(INTERVAL)
    }
    fail("Timed out waiting until $description.")
}

suspend fun awaitSettled() = waitInRealTime(SETTLING_TIME)

private suspend fun waitInRealTime(duration: Duration) =
    withContext(Dispatchers.Default) {
        delay(duration)
    }

private val TIMEOUT = 10.seconds
private val INTERVAL = 10.milliseconds
private val SETTLING_TIME = 100.milliseconds
