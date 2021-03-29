package com.himanshurawat.imageworker.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.himanshurawat.imageworker.Extension
import java.io.File
import java.lang.IllegalArgumentException

class From(private val context: Context) {

    private var fileExtension: String? = null
    private var fileName: String? = null
    private var subDirectory: String? = null
    private var directory: String? = context.applicationContext.packageName

    //Setting Up Directory
    fun directory(dir: String?): From {
        if(dir!= null && dir.trim().isNotEmpty()){
            directory = dir
        }else{
            throw IllegalArgumentException("Directory cannot be Empty or Null")
        }
        return this
    }

    //Set Sub Directory
    fun subDirectory(subDir: String?): From {
        subDirectory = if(subDir!=null && subDir.trim().isNotEmpty()){
            if(subDirectory != null) {
                "$subDirectory/$subDir"
            }else{
                subDir
            }
        }else{
            throw IllegalArgumentException("Sub Directory Name cannot be Empty or Null")
        }
        return this
    }

    //Set File Extension
    fun withExtension(e: Extension): From {
        fileExtension = e.value
        return this
    }

    fun setFileName(s :String?): From {
        if(s!=null && s.trim().isNotEmpty()){
            val name = s.replace(" ","")
            fileName = name
        }else{
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
    fun load(): Bitmap?{
        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if(fileName == null){
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if(!readPermissionGranted()){
            throw SecurityException("Write External Storage Permission Not Granted")
        }

        val dir = if(subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")
        // There is no file to fetch if Directory Doesn't Exist
        if(!dir.exists()){
            return null
        }

        val file = File(dir,"$fileName$fileExtension")

        return BitmapFactory.decodeFile(file.path)
    }

    private fun readPermissionGranted(): Boolean{
        if(ContextCompat.checkSelfPermission(context.applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        return false
    }
}