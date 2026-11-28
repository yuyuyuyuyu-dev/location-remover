package dev.yuyuyuyuyu.locationremover.nonspec

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import dev.yuyuyuyuyu.locationremover.MainActivity
import dev.yuyuyuyuyu.locationremover.testing.Gallery
import dev.yuyuyuyuyu.locationremover.testing.picture
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReceivingTest {
    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private var scenario: ActivityScenario<MainActivity>? = null

    @After
    fun closeTheApp() {
        scenario?.close()
    }

    @Test
    fun `should show the picture shared from another app`() {
        // Arrange
        val pictureUri = Gallery.publish(picture())
        val intent =
            Intent(Intent.ACTION_SEND)
                .setPackage(ApplicationProvider.getApplicationContext<Context>().packageName)
                .setType("image/jpeg")
                .putExtra(Intent.EXTRA_STREAM, pictureUri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Act
        scenario = ActivityScenario.launch(intent)

        // Assert
        composeTestRule.waitUntilAtLeastOneExists(
            matcher = hasContentDescription(SELECTED_IMAGE),
            timeoutMillis = TIMEOUT_MILLIS,
        )
    }

    private companion object {
        private const val SELECTED_IMAGE = "選択した画像"
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
