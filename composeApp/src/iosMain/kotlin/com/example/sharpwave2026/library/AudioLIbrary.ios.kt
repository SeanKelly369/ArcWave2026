package com.example.sharpwave2026.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.sharpwave2026.player.Track
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSDirectoryEnumerationSkipsHiddenFiles
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSLog
import platform.Foundation.NSUserDomainMask

@Composable
actual fun provideAudioLibrary(): AudioLibrary = remember { IosAudioLibrary() }
private class IosAudioLibrary: AudioLibrary {

    override suspend fun scanTracks(): List<Track> = withContext(Dispatchers.Default) {


        val docs = documentDirUrl()

        // 1 scan documents
        var tracks = scanDirectoryForAudio(docs)

        // 2 if empty, seed from bundle (dev convenience, then re-scan
        if (tracks.isEmpty()) {
            seedBundleAudioIntoDocuments(docs)
            tracks = scanDirectoryForAudio(docs)
        }
        tracks.sortedBy { it.title.lowercase() }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirUrl(): NSURL {
        val fm = NSFileManager.defaultManager
        val url = fm.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(url) { "Could not resolve Documents directory URL" }
    }

    private fun scanDirectoryForAudio(dir: NSURL): List<Track> {
        val fm = NSFileManager.defaultManager
        val enumerator = fm.enumeratorAtURL(
            url = dir,
            includingPropertiesForKeys = null,
            options = NSDirectoryEnumerationSkipsHiddenFiles,
            errorHandler = null
        ) ?: return emptyList()

        val out = mutableListOf<Track>()

        while (true) {
            val next = enumerator.nextObject() ?: break

            val url: NSURL = when (next) {
                is NSURL -> next
                is String -> dir.URLByAppendingPathComponent(next) ?: continue
                else -> continue
            }

            val last = url.lastPathComponent ?: continue
            val lower = last.lowercase()

            val ext =
                when {
                    lower.endsWith(".mp3") -> "mp3"
                    lower.endsWith(".m4a") -> "m4a"
                    lower.endsWith(".wav") -> "wav"
                    else -> null
                } ?: continue

            // Use file://... for playback
            val fileUrlString = url.absoluteString ?: continue

            val title = last
                .removeSuffix(".$ext")
                .replace('_', ' ')

            out += Track(
                id = fileUrlString,
                title = title,
                artist = "",
                uri = fileUrlString
            )
        }
        return out
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun seedBundleAudioIntoDocuments(docs: NSURL) {
        val fm = NSFileManager.defaultManager

        fun bundleUrls(ext: String): List<NSURL> =
            NSBundle.mainBundle.URLsForResourcesWithExtension(ext, subdirectory = null).orEmpty() as List<NSURL>

        val all = buildList {
            addAll(bundleUrls("mp3"))
            addAll(bundleUrls("m4a"))
            addAll(bundleUrls("wav"))
        }

        for (src in all) {
            val name = src.lastPathComponent ?: continue
            val dest = docs.URLByAppendingPathComponent(name) ?: continue
            val destPath = dest.path ?: continue

            if (fm.fileExistsAtPath(destPath)) continue

            val ok = fm.copyItemAtURL(src, dest, error = null)
            if (!ok) {
                NSLog("AudioLibrary: failed to copy bundle audio %@ -> %@", src.absoluteString ?: "", dest.absoluteString ?: "")
            }
        }
    }
}