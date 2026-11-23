package dev.yuyuyuyuyu.locationremover.ui.locationRemover

data class LocationRemoverUiState(
    val selectedJpeg: ByteArray? = null,
    val exifRemovedJpeg: ByteArray? = null,
    val copiedMessageIsShown: Boolean = false,
    val copyingFailedDialogIsShown: Boolean = false,
    val sharingIsNotSupportedDialogIsShown: Boolean = false,
    val imageCopiedDialogIsShown: Boolean = false,
)
