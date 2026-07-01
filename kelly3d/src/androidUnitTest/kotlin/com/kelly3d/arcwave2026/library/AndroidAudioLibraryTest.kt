package com.kelly3d.arcwave2026.library

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AndroidAudioLibraryTest {

    private val context = mockk<Context>(relaxed = true)
    private val contentResolver = mockk<ContentResolver>()
    private val cursor = mockk<Cursor>(relaxed = true)
    private lateinit var library: AndroidAudioLibrary

    @Before
    fun setUp() {
        library = AndroidAudioLibrary(context)
        every { context.contentResolver } returns contentResolver
        
        // Mocking MediaScannerConnection since it hits disk/system services
        mockkStatic(android.media.MediaScannerConnection::class)
        every { 
            android.media.MediaScannerConnection.scanFile(any(), any(), any(), any()) 
        } answers {
            val callback = arg<android.media.MediaScannerConnection.OnScanCompletedListener>(3)
            callback.onScanCompleted(null, null)
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `scanTracks returns empty list when cursor is null`() = runBlocking {
        every {
            contentResolver.query(any(), any(), any(), null, any())
        } returns null

        val result = library.scanTracks()
        
        assertTrue(result.isEmpty())
    }

    @Test
    fun `scanTracks returns tracks when cursor has data`() = runBlocking {
        every {
            contentResolver.query(any(), any(), any(), null, any())
        } returns cursor
        
        every { cursor.moveToNext() } returnsMany listOf(true, false)
        every { cursor.count } returns 1
        
        // Mock column indices
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID) } returns 0
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME) } returns 1
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE) } returns 2
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST) } returns 3
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION) } returns 4
        every { cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM) } returns 5

        // Mock row data
        every { cursor.getLong(0) } returns 123L
        every { cursor.getString(1) } returns "test_audio.mp3"
        every { cursor.getString(2) } returns "Test Title"
        every { cursor.getString(3) } returns "Test Artist"
        every { cursor.getLong(4) } returns 180000L
        every { cursor.getString(5) } returns "Test Album"

        val result = library.scanTracks()

        assertEquals(1, result.size)
        assertEquals("Test Title", result[0].title)
        assertTrue(result[0].uri.contains("123"))
    }
}
