package dev.yuyuyuyuyu.locationremover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import dev.yuyuyuyuyu.locationremover.image.createExifRemover
import dev.yuyuyuyuyu.locationremover.image.createImageDownloader
import dev.yuyuyuyuyu.locationremover.image.createImagePicker
import dev.yuyuyuyuyu.locationremover.image.createImageSharer
import dev.yuyuyuyuyu.locationremover.ui.locationRemover.LocationRemoverScreen
import dev.yuyuyuyuyu.locationremover.ui.locationRemover.LocationRemoverViewModel
import dev.yuyuyuyuyu.mymaterialtheme.MyMaterialTheme
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.app_name
import locationremover.shared.generated.resources.source_code
import org.jetbrains.compose.resources.stringResource

private const val SOURCE_CODE_URL = "https://github.com/yuyuyuyuyu-dev/location-remover"

@Composable
@Preview
@OptIn(ExperimentalMaterial3Api::class)
fun App() {
    val viewModel =
        remember {
            LocationRemoverViewModel(
                imagePicker = createImagePicker(),
                exifRemover = createExifRemover(),
                imageDownloader = createImageDownloader(),
                imageSharer = createImageSharer()
            )
        }
    val uriHandler = LocalUriHandler.current

    MyMaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(Res.string.app_name)) })
            },
            bottomBar = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { uriHandler.openUri(SOURCE_CODE_URL) }) {
                        Text(stringResource(Res.string.source_code))
                    }
                }
            }
        ) { innerPadding ->
            LocationRemoverScreen(
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
