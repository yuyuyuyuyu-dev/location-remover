package dev.yuyuyuyuyu.locationremover.ui.locationRemover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import locationremover.shared.generated.resources.Res
import locationremover.shared.generated.resources.download
import locationremover.shared.generated.resources.exif_removed_image
import locationremover.shared.generated.resources.ok
import locationremover.shared.generated.resources.please_select_an_image
import locationremover.shared.generated.resources.remove_exif
import locationremover.shared.generated.resources.select_an_image
import locationremover.shared.generated.resources.selected_image
import locationremover.shared.generated.resources.share
import locationremover.shared.generated.resources.sharing_is_not_supported
import locationremover.shared.generated.resources.supported_environments
import org.jetbrains.compose.resources.stringResource

@Composable
fun LocationRemoverScreen(viewModel: LocationRemoverViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.please_select_an_image),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
        )

        Button(onClick = viewModel::onSelectImageButtonClicked) {
            Text(stringResource(Res.string.select_an_image))
        }

        uiState.selectedJpeg?.let { selectedJpeg ->
            JpegImage(
                jpeg = selectedJpeg,
                contentDescription = stringResource(Res.string.selected_image),
                modifier = Modifier.widthIn(max = 320.dp)
            )

            Button(onClick = viewModel::onRemoveExifButtonClicked) {
                Text(stringResource(Res.string.remove_exif))
            }
        }

        uiState.exifRemovedJpeg?.let { exifRemovedJpeg ->
            HorizontalDivider()

            JpegImage(
                jpeg = exifRemovedJpeg,
                contentDescription = stringResource(Res.string.exif_removed_image),
                modifier = Modifier.widthIn(max = 640.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = viewModel::onDownloadButtonClicked) {
                    Text(stringResource(Res.string.download))
                }

                Button(onClick = viewModel::onShareButtonClicked) {
                    Text(stringResource(Res.string.share))
                }
            }
        }
    }

    if (uiState.sharingIsNotSupportedDialogIsShown) {
        SharingIsNotSupportedDialog(
            onDismissRequest = viewModel::onSharingIsNotSupportedDialogDismissed
        )
    }
}

@Composable
private fun JpegImage(jpeg: ByteArray, contentDescription: String, modifier: Modifier = Modifier) {
    val imageBitmap = remember(jpeg) { jpeg.decodeToImageBitmap() }

    Image(
        bitmap = imageBitmap,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth
    )
}

@Composable
private fun SharingIsNotSupportedDialog(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.ok))
            }
        },
        title = { Text(stringResource(Res.string.sharing_is_not_supported)) },
        text = { Text(stringResource(Res.string.supported_environments)) }
    )
}
