package com.himanshurawat.imageworker.work

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/*Note - As you may be aware that File Handling is now very strict in Android.
The data is stored in "Internal shared storage\Android\data\<your-app-package>\<your-specified-directory"

If you want to continue using previous implementation then stick to v1.0 and \n
add requestLegacyExternalStorage to true in your app's manifest file.
https://developer.android.com/about/versions/11/privacy/storage
*/

class To(private val context: Context) {

    private var fileExtension: String? = null
    private var fileName: String? = null
    private var subDirectory: String? = null
    private var directory: String? = (context.applicationContext ?: context).packageName

    //Setting Up Directory
    fun directory(dir: String?): To {
        if (dir != null && dir.trim().isNotEmpty()) {
            directory = dir
        } else {
            throw IllegalArgumentException("Directory cannot be Empty or Null")
        }
        return this
    }

    //Set Sub Directory
    fun subDirectory(subDir: String?): To {
        subDirectory = if (subDir != null && subDir.trim().isNotEmpty()) {
            if (subDirectory != null) {
                "$subDirectory/$subDir"
            } else {
                subDir
            }
        } else {
            throw IllegalArgumentException("Sub Directory Name cannot be Empty or Null")
        }
        return this
    }

    //Set File Extension
    fun withExtension(e: Extension): To {
        fileExtension = e.value
        return this
    }

    //Setting File Name
    fun setFileName(s: String?): To {
        if (s != null && s.trim().isNotEmpty()) {
            val name = s.replace(" ", "")
            fileName = name
        } else {
            throw IllegalArgumentException("File Name cannot be Empty or Null")
        }
        return this
    }

    private fun writeBitmapToFile(bitmap: Bitmap, quality: Int) {
        val dir = if (subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")

        //Create Directory If Doesn't Exist
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, "$fileName$fileExtension")

        try {
            FileOutputStream(file).use { outStream ->
                val format = when {
                    fileExtension.equals(".png", true) -> Bitmap.CompressFormat.PNG
                    fileExtension.equals(".jpeg", true) || fileExtension.equals(".jpg", true) -> Bitmap.CompressFormat.JPEG
                    fileExtension.equals(".webp", true) -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Bitmap.CompressFormat.WEBP_LOSSY
                        } else {
                            @Suppress("DEPRECATION")
                            Bitmap.CompressFormat.WEBP
                        }
                    }
                    else -> Bitmap.CompressFormat.PNG
                }
                bitmap.compress(format, quality, outStream)
                outStream.flush()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Synchronous Function to Save File to Directory
    fun save(bitmap: Bitmap?, quality: Int? = 100) {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Bitmap Quality must be between 1 to 100")
        }

        if (bitmap == null) {
            throw IllegalArgumentException("Bitmap cannot be Empty or Null")
        }

        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (!writePermissionGranted()) {
            throw SecurityException("Write External Storage Permission Not Granted")
        }

        writeBitmapToFile(bitmap, quality)
    }

    fun save(base64: String?, quality: Int? = 100) {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Quality must be between 1 to 100")
        }

        if (base64 == null || base64.isEmpty()) {
            throw IllegalArgumentException("Base64 Encoded String should't be Null or Empty")
        }
        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            ?: throw NullPointerException("Decoding Base64 String results in Null Bitmap")

        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (!writePermissionGranted()) {
            throw SecurityException("Write External Storage Permission Not Granted")
        }

        writeBitmapToFile(bitmap, quality)
    }

    fun save(drawable: Drawable?, quality: Int? = 100) {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Quality must be between 1 to 100")
        }
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be Null")
        }

        val bitmap = ImageWorker.convert().drawableToBitmap(drawable)
            ?: throw NullPointerException("Converting Drawable results in Null Bitmap")

        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (!writePermissionGranted()) {
            throw SecurityException("Write External Storage Permission Not Granted")
        }

        writeBitmapToFile(bitmap, quality)
    }

    fun saveToGallery(bitmap: Bitmap?, quality: Int? = 100, syncLocalCopy: Boolean = true): Uri? {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Bitmap Quality must be between 1 to 100")
        }

        if (bitmap == null) {
            throw IllegalArgumentException("Bitmap cannot be Empty or Null")
        }

        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        var mediaStoreUri: Uri? = null
        val format = getCompressFormat()
        val mimeType = getMimeType()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val relativePath = if (subDirectory == null) {
                "Pictures/$directory"
            } else {
                "Pictures/$directory/$subDirectory"
            }
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName$fileExtension")
                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outStream ->
                    bitmap.compress(format, quality, outStream)
                    outStream.flush()
                }
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                mediaStoreUri = uri
            }
        } else {
            if (!galleryWritePermissionGranted()) {
                throw SecurityException("Write External Storage Permission Not Granted")
            }
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDir = if (subDirectory == null) {
                File(picturesDir, "$directory")
            } else {
                File(picturesDir, "$directory/$subDirectory")
            }
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }
            val file = File(targetDir, "$fileName$fileExtension")
            FileOutputStream(file).use { outStream ->
                bitmap.compress(format, quality, outStream)
                outStream.flush()
            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf(mimeType),
                null
            )
            mediaStoreUri = Uri.fromFile(file)
        }

        if (syncLocalCopy) {
            try {
                writeBitmapToFile(bitmap, quality)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return mediaStoreUri
    }

    fun saveToGallery(drawable: Drawable?, quality: Int? = 100, syncLocalCopy: Boolean = true): Uri? {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Quality must be between 1 to 100")
        }
        if (drawable == null) {
            throw IllegalArgumentException("Drawable cannot be Null")
        }

        val bitmap = ImageWorker.convert().drawableToBitmap(drawable)
            ?: throw NullPointerException("Converting Drawable results in Null Bitmap")

        return saveToGallery(bitmap, quality, syncLocalCopy)
    }

    fun saveToGallery(base64: String?, quality: Int? = 100, syncLocalCopy: Boolean = true): Uri? {
        if (quality == null || quality < 1 || quality > 100) {
            throw IllegalArgumentException("Quality must be between 1 to 100")
        }

        if (base64 == null || base64.isEmpty()) {
            throw IllegalArgumentException("Base64 Encoded String should't be Null or Empty")
        }
        val decodedString: ByteArray = Base64.decode(base64, Base64.DEFAULT)
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            ?: throw NullPointerException("Decoding Base64 String results in Null Bitmap")

        return saveToGallery(bitmap, quality, syncLocalCopy)
    }

    private fun getCompressFormat(): Bitmap.CompressFormat {
        return when {
            fileExtension.equals(".png", true) -> Bitmap.CompressFormat.PNG
            fileExtension.equals(".jpeg", true) || fileExtension.equals(".jpg", true) -> Bitmap.CompressFormat.JPEG
            fileExtension.equals(".webp", true) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
            }
            else -> Bitmap.CompressFormat.PNG
        }
    }

    private fun getMimeType(): String {
        return when {
            fileExtension.equals(".png", true) -> "image/png"
            fileExtension.equals(".jpeg", true) || fileExtension.equals(".jpg", true) -> "image/jpeg"
            fileExtension.equals(".webp", true) -> "image/webp"
            else -> "image/png"
        }
    }

    private fun writePermissionGranted(): Boolean {
        // App-specific external storage doesn't require runtime permission on API 19+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context.applicationContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun galleryWritePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context.applicationContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    }
}