package com.himanshurawat.imageworker.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.himanshurawat.imageworker.Extension
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files

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

    /*
     Delete Function, This will delete the Image Files Present in the provided Directory or Sub-Directory, to avoid exploitation only .PNG, .WEBP and .JPG files can be deleted.
     The function output true if the file is deleted successfully. Else false. Make sure you modify your application to handle delete() function properly.
     */
     fun delete():Boolean{
        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use withExtension() Method")
        }

        if(fileName == null){
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if(!readAndWritePermissionGranted()){
            throw SecurityException("Make sure user has granted Read and Write External Storage Permission Not Granted")
        }

        val dir = if(subDirectory == null)
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory")
        else
            File("${context.getExternalFilesDir(null)?.absolutePath}/$directory/$subDirectory")
        // There is no file to fetch if Directory Doesn't Exist
        if(!dir.exists()){
            return false
        }

        //Fetch the file from the
        val file = File(dir,"$fileName$fileExtension")

        //If file doesn't exist return false
        if(!file.exists()){
            return false
        }
        //Return the boolean if System is able to delete the File or not.
        return file.delete()

    }
    private fun readPermissionGranted(): Boolean{
        if(ContextCompat.checkSelfPermission(context.applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun readAndWritePermissionGranted(): Boolean{
        if(ContextCompat.checkSelfPermission(context.applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context.applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        return false
    }

}