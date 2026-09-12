package com.himanshurawat.imageworker

import android.content.Context
import com.himanshurawat.imageworker.work.Convert
import com.himanshurawat.imageworker.work.From
import com.himanshurawat.imageworker.work.To
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ImageWorkerUnitTest {

    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        `when`(mockContext.packageName).thenReturn("com.himanshurawat.imageworker.test")
        `when`(mockContext.applicationContext).thenReturn(mockContext)
    }

    @Test
    fun testExtensions() {
        assertEquals(".png", Extension.PNG.value)
        assertEquals(".jpeg", Extension.JPEG.value)
        assertEquals(".jpg", Extension.JPG.value)
        assertEquals(".webp", Extension.WEBP.value)
    }

    @Test
    fun testConvertBase64Validation() {
        val convert = Convert()
        try {
            convert.base64ToBitmap("")
            fail("Expected IllegalArgumentException for empty base64")
        } catch (e: IllegalArgumentException) {
            assertEquals("Base64 Encoded String cannot be Empty", e.message)
        }

        try {
            convert.base64ToDrawable("")
            fail("Expected IllegalArgumentException for empty base64")
        } catch (e: IllegalArgumentException) {
            assertEquals("Base64 Encoded String cannot be Empty", e.message)
        }

        try {
            convert.drawableToBase64(null)
            fail("Expected IllegalArgumentException for null drawable")
        } catch (e: IllegalArgumentException) {
            assertEquals("Drawable cannot be Null", e.message)
        }
    }

    @Test
    fun testToDirectoryValidation() {
        val to = To(mockContext)

        try {
            to.directory("")
            fail("Expected IllegalArgumentException for empty directory")
        } catch (e: IllegalArgumentException) {
            assertEquals("Directory cannot be Empty or Null", e.message)
        }

        try {
            to.directory("   ")
            fail("Expected IllegalArgumentException for whitespace directory")
        } catch (e: IllegalArgumentException) {
            assertEquals("Directory cannot be Empty or Null", e.message)
        }
    }

    @Test
    fun testToSubDirectoryValidation() {
        val to = To(mockContext)

        try {
            to.subDirectory("")
            fail("Expected IllegalArgumentException for empty subDirectory")
        } catch (e: IllegalArgumentException) {
            assertEquals("Sub Directory Name cannot be Empty or Null", e.message)
        }
    }

    @Test
    fun testToFileNameValidation() {
        val to = To(mockContext)

        try {
            to.setFileName("")
            fail("Expected IllegalArgumentException for empty fileName")
        } catch (e: IllegalArgumentException) {
            assertEquals("File Name cannot be Empty or Null", e.message)
        }

        try {
            to.setFileName("   ")
            fail("Expected IllegalArgumentException for whitespace fileName")
        } catch (e: IllegalArgumentException) {
            assertEquals("File Name cannot be Empty or Null", e.message)
        }
    }

    @Test
    fun testToSaveQualityValidation() {
        val to = To(mockContext)

        try {
            to.save(bitmap = null, quality = 0)
            fail("Expected IllegalArgumentException for quality 0")
        } catch (e: IllegalArgumentException) {
            assertEquals("Bitmap Quality must be between 1 to 100", e.message)
        }

        try {
            to.save(bitmap = null, quality = 101)
            fail("Expected IllegalArgumentException for quality 101")
        } catch (e: IllegalArgumentException) {
            assertEquals("Bitmap Quality must be between 1 to 100", e.message)
        }

        try {
            to.save(bitmap = null, quality = -5)
            fail("Expected IllegalArgumentException for quality -5")
        } catch (e: IllegalArgumentException) {
            assertEquals("Bitmap Quality must be between 1 to 100", e.message)
        }
    }

    @Test
    fun testFromDirectoryValidation() {
        val from = From(mockContext)

        try {
            from.directory("")
            fail("Expected IllegalArgumentException for empty directory")
        } catch (e: IllegalArgumentException) {
            assertEquals("Directory cannot be Empty or Null", e.message)
        }
    }

    @Test
    fun testFromSubDirectoryValidation() {
        val from = From(mockContext)

        try {
            from.subDirectory("")
            fail("Expected IllegalArgumentException for empty subDirectory")
        } catch (e: IllegalArgumentException) {
            assertEquals("Sub Directory Name cannot be Empty or Null", e.message)
        }
    }

    @Test
    fun testFromFileNameValidation() {
        val from = From(mockContext)

        try {
            from.setFileName("")
            fail("Expected IllegalArgumentException for empty fileName")
        } catch (e: IllegalArgumentException) {
            assertEquals("File Name cannot be Empty or Null", e.message)
        }
    }
}
