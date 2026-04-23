package com.kelly3d.arcwave2026.player

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun loadArtworkFromAudio(path: String): ImageBitmap? {
    val retriever = MediaMetadataRetriever()

    return try {
        val uri = Uri.parse(path)
        val context = AndroidContextProvider.appContext
        
        if (uri.scheme == "content") {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                retriever.setDataSource(pfd.fileDescriptor)
            }
        } else {
            retriever.setDataSource(path)
        }

        val bytes = retriever.embeddedPicture ?: return null
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    } catch (e: Exception) {
        Log.w("ArtworkLoader", "Failed to load artwork for $path", e)
        null
    } finally {
        try {
            retriever.release()
        } catch (_: Exception) {
        }
    }
}
