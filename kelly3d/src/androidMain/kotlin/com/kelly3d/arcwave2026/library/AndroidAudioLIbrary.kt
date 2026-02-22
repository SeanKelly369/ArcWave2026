package com.kelly3d.arcwave2026.library

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.provider.MediaStore
import com.kelly3d.arcwave2026.player.Track
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

class AndroidAudioLibrary(
    private val context: Context
) : AudioLibrary {

    private suspend fun forceScanDownloadsAudio(): Unit =
        suspendCancellableCoroutine { cont ->
            val dir = File("/storage/emulated/0/Download")
            val files = dir.listFiles { f ->
                f.isFile && (
                        f.name.endsWith(".mp3", true) ||
                                f.name.endsWith(".m4a", true) ||
                                f.name.endsWith(".wav", true)
                        )
            }.orEmpty()

            if (files.isEmpty()) {
                cont.resume(Unit)
                return@suspendCancellableCoroutine
            }

            val paths = files.map { it.absolutePath }.toTypedArray()

            var remaining = paths.size
            MediaScannerConnection.scanFile(context, paths, null) { path, uri ->
                Log.i("AudioLibrary", "Scanned $path -> $uri")
                remaining--
                if (remaining <= 0 && cont.isActive) cont.resume(Unit)
            }

            cont.invokeOnCancellation { /* nothing to cancel */ }
        }

    override suspend fun scanTracks(): List<Track> {
        val resolver = context.contentResolver

        forceScanDownloadsAudio()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        Log.i("AudioLibrary", "collection=$collection")

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
        )
        Log.i("AudioLibrary", "projection=${projection.joinToString()}")

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs: Array<String>? = null

        val sortOrder = "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"

        val result = mutableListOf<Track>()

        resolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { c ->
            Log.i("AudioLibrary", "cursor.count=${c.count}")

            val idCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val displayName = c.getString(nameCol) ?: "Unknown"
                val title = c.getString(titleCol)?.takeIf { it.isNotBlank() } ?: displayName
                val artist = c.getString(artistCol) ?: ""
                val durationMs = c.getLong(durCol)
                val album = c.getString(albumCol)

                val uri = ContentUris.withAppendedId(collection, id).toString()
                Log.i("AudioLibrary", "found title=$title artist=$artist dur=$durationMs uri=$uri")

                result += Track(
                    id = uri,
                    title = title,
                    artist = artist,
                    uri = uri,
                    durationMs = durationMs,
                    album = album
                )
            }
        } ?: run {
            Log.e("AudioLibrary", "query returned null cursor (permission?)")
        }

        return result
    }
}