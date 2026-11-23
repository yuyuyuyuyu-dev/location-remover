package dev.yuyuyuyuyu.locationremover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import dev.yuyuyuyuyu.locationremover.image.createExifRemover
import dev.yuyuyuyuyu.locationremover.image.createImageCopier
import dev.yuyuyuyuyu.locationremover.image.createImageDownloader
import dev.yuyuyuyuyu.locationremover.image.createImagePicker
import dev.yuyuyuyuyu.locationremover.image.createImageSharer
import dev.yuyuyuyuyu.locationremover.ui.locationRemover.LocationRemoverScreen
import dev.yuyuyuyuyu.locationremover.ui.locationRemover.LocationRemoverViewModel
import dev.yuyuyuyuyu.mycomposables.MyMaterialTheme
import dev.yuyuyuyuyu.mycomposables.MyScaffold
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

private const val SOURCE_CODE_URL = "https://github.com/yuyuyuyuyu-dev/location-remover"

@Composable
@Preview
fun App() {
    val uriHandler = LocalUriHandler.current
    val viewModel =
        remember {
            LocationRemoverViewModel(
                imagePicker = createImagePicker(),
                exifRemover = createExifRemover(),
                imageDownloader = createImageDownloader(),
                imageCopier = createImageCopier(),
                imageSharer = createImageSharer(),
                uriHandler = uriHandler,
            )
        }
    val snackbarHostState = remember { SnackbarHostState() }
    val libraries by produceLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    MyMaterialTheme {
        MyScaffold(
            title = stringResource(Res.string.app_name),
            libraries = libraries,
            onSourceCodeButtonClick = { uriHandler.openUri(SOURCE_CODE_URL) },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                LocationRemoverScreen(
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                )
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}
