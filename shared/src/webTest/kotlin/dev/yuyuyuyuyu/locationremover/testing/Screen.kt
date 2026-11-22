package dev.yuyuyuyuyu.locationremover.testing

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
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
suspend fun ComposeUiTest.isShown(text: StringResource): Boolean {
    val label = getString(text)
    return onAllNodesWithText(label).fetchSemanticsNodes().isNotEmpty()
}

suspend fun awaitSharedFile(): File {
    awaitUntil("a file is shared") { ShareSheet.sharedFileOrNull() != null }
    return checkNotNull(ShareSheet.sharedFileOrNull())
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
