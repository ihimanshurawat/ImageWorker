package com.himanshurawat.imageworker.work

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.himanshurawat.imageworker.Extension
import java.io.File

class From(private val context: Context) {

    private var fileExtension: String? = null
    private var fileName: String? = null
    private var subDirectory: String? = null
    private var directory: String? = (context.applicationContext ?: context).packageName

    //Setting Up Directory
    fun directory(dir: String?): From {
        if (dir != null && dir.trim().isNotEmpty()) {
            directory = dir
        } else {
            throw IllegalArgumentException("Directory cannot be Empty or Null")
        }
        return this
    }

    //Set Sub Directory
    fun subDirectory(subDir: String?): From {
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
    fun withExtension(e: Extension): From {
        fileExtension = e.value
        return this
    }

    fun setFileName(s: String?): From {
        if (s != null && s.trim().isNotEmpty()) {
            val name = s.replace(" ", "")
            fileName = name
        } else {
            throw IllegalArgumentException("File Name cannot be Empty or Null")
        }
        return this
    }

    /*  Function to Fetch File to Directory

        Note - As you may be aware that File Handling is now very strict in Android.
        The data is stored in "Internal shared storage\Android\data\<your-app-package>\<your-specified-directory"

        If you want to continue using previous implementation then stick to v1.0 and \n
        add requestLegacyExternalStorage to true in your app's manifest file.

        https://developer.android.com/about/versions/11/privacy/storage
     */
    fun load(): Bitmap? {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (!readPermissionGranted()) {
            throw SecurityException("Read External Storage Permission Not Granted")
        }

        val dir = if (subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")
        // There is no file to fetch if Directory Doesn't Exist
        if (!dir.exists()) {
            return null
        }

        val file = File(dir, "$fileName$fileExtension")

        return BitmapFactory.decodeFile(file.path)
    }

    /*
     Delete Function, This will delete the Image Files Present in the provided Directory or Sub-Directory, to avoid exploitation only .PNG, .WEBP and .JPG files can be deleted.
     The function output true if the file is deleted successfully. Else false. Make sure you modify your application to handle delete() function properly.
     */
    fun delete(): Boolean {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (!readAndWritePermissionGranted()) {
            throw SecurityException("Read and Write External Storage Permission Not Granted")
        }

        val dir = if (subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")
        // There is no file to fetch if Directory Doesn't Exist
        if (!dir.exists()) {
            return false
        }

        //Fetch the file
        val file = File(dir, "$fileName$fileExtension")

        //If file doesn't exist return false
        if (!file.exists()) {
            return false
        }
        //Return the boolean if System is able to delete the File or not.
        return file.delete()
    }

    fun getFile(): File {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }
        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }
        val dir = if (subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")
        return File(dir, "$fileName$fileExtension")
    }

    fun existsInGallery(): Boolean {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }
        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf("$fileName$fileExtension")
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                val exists = cursor?.use { it.count > 0 } ?: false
                if (exists) return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDir = if (subDirectory == null) {
                File(picturesDir, "$directory")
            } else {
                File(picturesDir, "$directory/$subDirectory")
            }
            val publicFile = File(targetDir, "$fileName$fileExtension")
            if (publicFile.exists()) return true
        }

        return try {
            getFile().exists()
        } catch (e: Exception) {
            false
        }
    }

    fun deleteFromGallery(): Boolean {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }
        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        var deleted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf("$fileName$fileExtension")
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.use {
                    val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (it.moveToNext()) {
                        val mediaId = it.getLong(idCol)
                        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId)
                        context.contentResolver.delete(uri, null, null)
                        deleted = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val targetDir = if (subDirectory == null) {
                    File(picturesDir, "$directory")
                } else {
                    File(picturesDir, "$directory/$subDirectory")
                }
                val publicFile = File(targetDir, "$fileName$fileExtension")
                if (publicFile.exists()) {
                    deleted = publicFile.delete()
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(publicFile.absolutePath),
                        arrayOf(getMimeType()),
                        null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Also delete local copy if exists
        try {
            val localFile = getFile()
            if (localFile.exists()) {
                deleted = localFile.delete() || deleted
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return deleted
    }

    fun getGalleryUri(): Uri? {
        if (fileExtension == null) {
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }
        if (fileName == null) {
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
                val selectionArgs = arrayOf("$fileName$fileExtension")
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val mediaId = it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mediaId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val targetDir = if (subDirectory == null) {
                File(picturesDir, "$directory")
            } else {
                File(picturesDir, "$directory/$subDirectory")
            }
            val publicFile = File(targetDir, "$fileName$fileExtension")
            if (publicFile.exists()) {
                return Uri.fromFile(publicFile)
            }
        }

        // Fallback to local file Uri if available
        return try {
            val localFile = getFile()
            if (localFile.exists()) {
                Uri.fromFile(localFile)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadFromGallery(): Bitmap? {
        val uri = getGalleryUri() ?: return null
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    private fun readPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context.applicationContext,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun readAndWritePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            context.applicationContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context.applicationContext,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    }
}