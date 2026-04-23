package com.kelly3d.arcwave2026.player

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.AVFoundation.*
import platform.Foundation.*
import platform.posix.memcpy

actual fun loadArtworkFromAudio(path: String): ImageBitmap? {
    return try {
        val url = if (path.startsWith("file://")) {
            NSURL.URLWithString(path)
        } else {
            NSURL.fileURLWithPath(path)
        } ?: return null

        val asset = AVAsset.assetWithURL(url)

        val artworkItem = asset.commonMetadata
            .mapNotNull { it as? AVMetadataItem }
            .firstOrNull { it.commonKey == AVMetadataCommonKeyArtwork }
            ?: return null

        val data = artworkItem.dataValue ?: (artworkItem.value as? NSData) ?: return null
        val bytes = data.toByteArray()

        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (_: Throwable) {
        null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (length > 0uL) {
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length)
        }
    }
    return result
}

