package dev.yuyuyuyuyu.locationremover.ui.locationRemover

data class LocationRemoverUiState(
    val selectedJpeg: ByteArray? = null,
    val exifRemovedJpeg: ByteArray? = null,
    val sharingIsNotSupportedDialogIsShown: Boolean = false,
    val imageCopiedDialogIsShown: Boolean = false,
    val copyingIsNotSupportedDialogIsShown: Boolean = false,
)
