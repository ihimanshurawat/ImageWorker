package com.himanshurawat.imageworker.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
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

        if(subDir!=null && subDir.trim().isNotEmpty()){
            if(subDirectory != null) {
                subDirectory = "$subDirectory/$subDir"
            }else{
                subDirectory = subDir
            }
        }else{
            throw java.lang.IllegalArgumentException("Sub Directory Name cannot be Empty or Null")
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
            throw java.lang.IllegalArgumentException("File Name cannot be Empty or Null")
        }
        return this
    }

    //Synchronous Function to Save File to Directory
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
            File("${Environment.getExternalStorageDirectory().absolutePath}/$directory")
        else
            File("${Environment.getExternalStorageDirectory().absolutePath}/$directory/$subDirectory")

        //Create Directory If Doesn't Exist
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