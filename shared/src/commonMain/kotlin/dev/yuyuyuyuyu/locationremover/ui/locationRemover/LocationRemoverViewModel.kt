package dev.yuyuyuyuyu.locationremover.ui.locationRemover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.yuyuyuyuyu.locationremover.image.ExifRemover
import dev.yuyuyuyuyu.locationremover.image.ImageDownloader
import dev.yuyuyuyuyu.locationremover.image.ImagePicker
import dev.yuyuyuyuyu.locationremover.image.ImageSharer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocationRemoverViewModel(
    private val imagePicker: ImagePicker,
    private val exifRemover: ExifRemover,
    private val imageDownloader: ImageDownloader,
    private val imageSharer: ImageSharer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LocationRemoverUiState())
    val uiState: StateFlow<LocationRemoverUiState> = _uiState.asStateFlow()

    fun onSelectImageButtonClicked() {
        viewModelScope.launch {
            val selectedJpeg = imagePicker.pickJpegOrNull() ?: return@launch

            // The result of the previous image would otherwise stay on screen next to the newly
            // selected one, offering a download of something the user has moved on from.
            _uiState.update { it.copy(selectedJpeg = selectedJpeg, exifRemovedJpeg = null) }
        }
    }

    fun onRemoveExifButtonClicked() {
        val selectedJpeg = _uiState.value.selectedJpeg ?: return

        viewModelScope.launch {
            val exifRemovedJpeg = exifRemover.removeExif(selectedJpeg)
            _uiState.update { it.copy(exifRemovedJpeg = exifRemovedJpeg) }
        }
    }

    fun onDownloadButtonClicked() {
        val exifRemovedJpeg = _uiState.value.exifRemovedJpeg ?: return

        imageDownloader.download(exifRemovedJpeg, FILE_NAME)
    }

    fun onShareButtonClicked() {
        val exifRemovedJpeg = _uiState.value.exifRemovedJpeg ?: return

        viewModelScope.launch {
            if (!imageSharer.tryShare(exifRemovedJpeg, FILE_NAME)) {
                _uiState.update { it.copy(sharingIsNotSupportedDialogIsShown = true) }
            }
        }
    }

    fun onSharingIsNotSupportedDialogDismissed() {
        _uiState.update { it.copy(sharingIsNotSupportedDialogIsShown = false) }
    }

    private companion object {
        private const val FILE_NAME = "image.jpg"
    }
}
