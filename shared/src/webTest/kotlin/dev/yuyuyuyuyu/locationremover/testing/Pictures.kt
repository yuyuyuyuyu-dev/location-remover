package dev.yuyuyuyuyu.locationremover.testing

import js.closeable.use
import web.blob.Blob
import web.blob.byteArray
import web.dom.document
import web.html.HtmlTagName
import web.html.toBlob
import web.images.createImageBitmap

val PICTURE_SIZE = PictureSize(width = 16, height = 8)

suspend fun pictureWithLocation(): ByteArray = jpeg(PICTURE_SIZE).withExifLocation(orientation = null)

suspend fun anotherPicture(): ByteArray = jpeg(PictureSize(width = 8, height = 8))

suspend fun pictureRotatedClockwiseByExif(): ByteArray {
    val jpeg = jpeg(PICTURE_SIZE)
    return jpeg.withExifLocation(orientation = ORIENTATION_ROTATED_CLOCKWISE)
}

suspend fun Blob.pictureSize(): PictureSize = createImageBitmap(this).use { PictureSize(it.width, it.height) }

fun ByteArray.hasLocation(): Boolean = exifTiffs().any { tiff -> GPS_INFO_TAG in tiff.ifd0Tags() }

private suspend fun jpeg(size: PictureSize): ByteArray {
    val canvas = document.createElement(HtmlTagName.canvas)
    canvas.width = size.width
    canvas.height = size.height

    return canvas.toBlob("image/jpeg").byteArray()
}

private fun ByteArray.withExifLocation(orientation: Int?): ByteArray {
    val tiff = tiff(orientation)
    val app1 = EXIF_HEADER + tiff
    val segment =
        BigEndian()
            .u16(APP1_MARKER)
            .u16(app1.size + SEGMENT_LENGTH_SIZE)
            .bytes(app1)
            .toByteArray()

    return copyOfRange(0, SOI_SIZE) + segment + copyOfRange(SOI_SIZE, size)
}

private fun tiff(orientation: Int?): ByteArray {
    val ifd0EntryCount = if (orientation == null) 1 else 2
    val gpsIfdOffset = IFD0_OFFSET + ifdSize(ifd0EntryCount)
    val latitudeOffset = gpsIfdOffset + ifdSize(GPS_ENTRY_COUNT)
    val longitudeOffset = latitudeOffset + DEGREES_MINUTES_SECONDS_SIZE

    val writer =
        BigEndian()
            .bytes("MM".encodeToByteArray())
            .u16(TIFF_MAGIC_NUMBER)
            .u32(IFD0_OFFSET)
            .u16(ifd0EntryCount)
    orientation?.let { writer.entry(ORIENTATION_TAG, SHORT, 1) { it.u16(orientation).u16(0) } }
    writer
        .entry(GPS_INFO_TAG, LONG, 1) { it.u32(gpsIfdOffset) }
        .u32(0)
        .u16(GPS_ENTRY_COUNT)
        .entry(GPS_LATITUDE_REF_TAG, ASCII, 2) { it.bytes("N".encodeToByteArray()).u16(0).u8(0) }
        .entry(GPS_LATITUDE_TAG, RATIONAL, 3) { it.u32(latitudeOffset) }
        .entry(GPS_LONGITUDE_REF_TAG, ASCII, 2) { it.bytes("E".encodeToByteArray()).u16(0).u8(0) }
        .entry(GPS_LONGITUDE_TAG, RATIONAL, 3) { it.u32(longitudeOffset) }
        .u32(0)
        .rational(35, 1)
        .rational(39, 1)
        .rational(31, 1)
        .rational(139, 1)
        .rational(44, 1)
        .rational(28, 1)

    return writer.toByteArray()
}

private fun ifdSize(entryCount: Int) = ENTRY_COUNT_SIZE + entryCount * ENTRY_SIZE + NEXT_IFD_OFFSET_SIZE

private fun ByteArray.exifTiffs(): List<ByteArray> =
    when {
        startsWith(JPEG_SIGNATURE) -> jpegExifSegments()
        startsWith(PNG_SIGNATURE) -> pngExifChunks()
        else -> error("The picture is neither a JPEG nor a PNG.")
    }

private fun ByteArray.jpegExifSegments(): List<ByteArray> {
    val segments = mutableListOf<ByteArray>()
    var position = SOI_SIZE
    while (position + SEGMENT_HEADER_SIZE <= size && u8(position) == MARKER_PREFIX) {
        val marker = u16(position, littleEndian = false)
        if (marker == SOS_MARKER) break

        val length = u16(position + MARKER_SIZE, littleEndian = false)
        val data = copyOfRange(position + SEGMENT_HEADER_SIZE, position + MARKER_SIZE + length)
        if (marker == APP1_MARKER && data.startsWith(EXIF_HEADER)) {
            segments += data.copyOfRange(EXIF_HEADER.size, data.size)
        }
        position += MARKER_SIZE + length
    }
    return segments
}

private fun ByteArray.pngExifChunks(): List<ByteArray> {
    val chunks = mutableListOf<ByteArray>()
    var position = PNG_SIGNATURE.size
    while (position + CHUNK_HEADER_SIZE <= size) {
        val length = u32(position, littleEndian = false)
        val type = copyOfRange(position + CHUNK_LENGTH_SIZE, position + CHUNK_HEADER_SIZE).decodeToString()
        if (type == IEND_CHUNK) break

        val dataStart = position + CHUNK_HEADER_SIZE
        if (type == EXIF_CHUNK) chunks += copyOfRange(dataStart, dataStart + length)
        position = dataStart + length + CHUNK_CRC_SIZE
    }
    return chunks
}

private fun ByteArray.ifd0Tags(): List<Int> {
    val littleEndian = copyOfRange(0, 2).decodeToString() == "II"
    val ifd0Offset = u32(IFD0_OFFSET_POSITION, littleEndian)
    val entryCount = u16(ifd0Offset, littleEndian)
    return (0 until entryCount).map { index ->
        u16(ifd0Offset + ENTRY_COUNT_SIZE + index * ENTRY_SIZE, littleEndian)
    }
}

private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
    if (size < prefix.size) return false
    return copyOfRange(0, prefix.size).contentEquals(prefix)
}

private fun ByteArray.u8(position: Int) = this[position].toInt() and BYTE_MASK

private fun ByteArray.u16(
    position: Int,
    littleEndian: Boolean,
): Int {
    val (high, low) = if (littleEndian) position + 1 to position else position to position + 1
    return (u8(high) shl BITS_PER_BYTE) or u8(low)
}

private fun ByteArray.u32(
    position: Int,
    littleEndian: Boolean,
): Int {
    val (high, low) = if (littleEndian) position + 2 to position else position to position + 2
    return (u16(high, littleEndian) shl (BITS_PER_BYTE * 2)) or u16(low, littleEndian)
}

private class BigEndian {
    private val bytes = mutableListOf<Byte>()

    fun u8(value: Int) = apply { bytes += value.toByte() }

    fun u16(value: Int) = u8(value shr BITS_PER_BYTE).u8(value)

    fun u32(value: Int) = u16(value shr (BITS_PER_BYTE * 2)).u16(value)

    fun bytes(value: ByteArray) = apply { bytes += value.toList() }

    fun rational(
        numerator: Int,
        denominator: Int,
    ) = u32(numerator).u32(denominator)

    fun entry(
        tag: Int,
        type: Int,
        count: Int,
        value: (BigEndian) -> BigEndian,
    ) = u16(tag).u16(type).u32(count).let(value)

    fun toByteArray() = bytes.toByteArray()
}

private val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
private val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
private val EXIF_HEADER = byteArrayOf(0x45, 0x78, 0x69, 0x66, 0x00, 0x00)

private const val BYTE_MASK = 0xFF
private const val BITS_PER_BYTE = 8

private const val MARKER_PREFIX = 0xFF
private const val APP1_MARKER = 0xFFE1
private const val SOS_MARKER = 0xFFDA
private const val SOI_SIZE = 2
private const val MARKER_SIZE = 2
private const val SEGMENT_LENGTH_SIZE = 2
private const val SEGMENT_HEADER_SIZE = MARKER_SIZE + SEGMENT_LENGTH_SIZE

private const val CHUNK_LENGTH_SIZE = 4
private const val CHUNK_TYPE_SIZE = 4
private const val CHUNK_HEADER_SIZE = CHUNK_LENGTH_SIZE + CHUNK_TYPE_SIZE
private const val CHUNK_CRC_SIZE = 4
private const val EXIF_CHUNK = "eXIf"
private const val IEND_CHUNK = "IEND"

private const val TIFF_MAGIC_NUMBER = 42
private const val IFD0_OFFSET_POSITION = 4
private const val IFD0_OFFSET = 8
private const val ENTRY_COUNT_SIZE = 2
private const val ENTRY_SIZE = 12
private const val NEXT_IFD_OFFSET_SIZE = 4
private const val DEGREES_MINUTES_SECONDS_SIZE = 3 * 8

private const val ASCII = 2
private const val SHORT = 3
private const val LONG = 4
private const val RATIONAL = 5

private const val ORIENTATION_TAG = 0x0112
private const val ORIENTATION_ROTATED_CLOCKWISE = 6
private const val GPS_INFO_TAG = 0x8825
private const val GPS_ENTRY_COUNT = 4
private const val GPS_LATITUDE_REF_TAG = 1
private const val GPS_LATITUDE_TAG = 2
private const val GPS_LONGITUDE_REF_TAG = 3
private const val GPS_LONGITUDE_TAG = 4
