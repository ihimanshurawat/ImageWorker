package com.himanshurawat.imageworker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ImageWorkerGalleryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    private fun createTestBitmap(width: Int = 100, height: Int = 100): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply { color = Color.BLUE }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        return bitmap
    }

    @Test
    fun testSaveToGalleryAndLifecycle() {
        val bitmap = createTestBitmap()
        val testId = "test_img_01"
        val dir = "Anime Wallpapers"
        val subDir = "TestAnime"

        // 1. Save to gallery
        val uri = ImageWorker.to(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .saveToGallery(bitmap, 90, syncLocalCopy = true)

        assertNotNull("MediaStore URI should not be null", uri)

        // 2. Check existence in gallery
        val exists = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .existsInGallery()

        assertTrue("Image should exist in gallery", exists)

        // 3. Get gallery URI
        val retrievedUri = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .getGalleryUri()

        assertNotNull("Retrieved gallery URI should not be null", retrievedUri)

        // 4. Load from gallery
        val loadedBitmap = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .loadFromGallery()

        assertNotNull("Loaded bitmap should not be null", loadedBitmap)

        // 5. Check local file
        val localFile = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .getFile()

        assertTrue("Local file should exist when syncLocalCopy = true", localFile.exists())

        // 6. Delete from gallery
        val deleted = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .deleteFromGallery()

        assertTrue("Delete should succeed", deleted)

        // 7. Verify no longer exists
        val existsAfterDelete = ImageWorker.from(context)
            .directory(dir)
            .subDirectory(subDir)
            .setFileName(testId)
            .withExtension(Extension.JPEG)
            .existsInGallery()

        assertFalse("Image should not exist after deletion", existsAfterDelete)
        assertFalse("Local file should be deleted", localFile.exists())
    }

    @Test
    fun testSaveDrawableToGallery() {
        val bitmap = createTestBitmap()
        val drawable = BitmapDrawable(context.resources, bitmap)
        val testId = "test_drawable_01"

        val uri = ImageWorker.to(context)
            .directory("Wallpapers")
            .setFileName(testId)
            .withExtension(Extension.PNG)
            .saveToGallery(drawable, 100)

        assertNotNull("URI should not be null", uri)

        val exists = ImageWorker.from(context)
            .directory("Wallpapers")
            .setFileName(testId)
            .withExtension(Extension.PNG)
            .existsInGallery()

        assertTrue(exists)

        ImageWorker.from(context)
            .directory("Wallpapers")
            .setFileName(testId)
            .withExtension(Extension.PNG)
            .deleteFromGallery()
    }

    @Test
    fun testValidationErrors() {
        val to = ImageWorker.to(context)
            .directory("Test")
            .setFileName("file")
            .withExtension(Extension.JPEG)

        try {
            to.saveToGallery(bitmap = null, quality = 100)
            fail("Expected IllegalArgumentException for null bitmap")
        } catch (e: IllegalArgumentException) {
            assertEquals("Bitmap cannot be Empty or Null", e.message)
        }

        try {
            to.saveToGallery(bitmap = createTestBitmap(), quality = 105)
            fail("Expected IllegalArgumentException for invalid quality")
        } catch (e: IllegalArgumentException) {
            assertEquals("Bitmap Quality must be between 1 to 100", e.message)
        }
    }
}
