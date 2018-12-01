package com.himanshurawat.imageworker

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.NullPointerException

class ImageWorker(private var context: Context) {

    private var bitmap: Bitmap? = null
    private var fileExtension: String? = null
    private var fileName: String? = null
    private var subDirectory: String? = null
    private var bitmapQuality: Int = 100
    private var directory: String? = context.applicationContext.packageName


    //Setting Up Directory
    fun setDirectory(s: String?): ImageWorker{

        if(s!= null && s.trim().isNotEmpty()){
            val directoryString = s.replace(" ", "")
            directory = directoryString
        }else{
            throw IllegalArgumentException("Directory cannot be Empty")
        }

        return this@ImageWorker
    }

    //Set Sub Directory
    fun setSubDirectory(s: String?): ImageWorker{

        if(s!=null && s.trim().isNotEmpty()){
            val subDirectoryName = s.replace(" ","")
            if(subDirectory != null) {
                subDirectory = "$subDirectory/$subDirectoryName"
            }else{
                subDirectory = subDirectoryName
            }
        }else{
            throw java.lang.IllegalArgumentException("Sub Directory Name cannot be Empty")
        }
        return this@ImageWorker
    }


    //Set File Extension
    fun setExtension(e: Extension): ImageWorker{

        fileExtension = e.value

        return this@ImageWorker
    }

    fun setBitmapQuality(q: Int?){

        if(q != null && q > 0){
            bitmapQuality = q
        }else{
            throw java.lang.IllegalArgumentException("Bitmap Quality cannot be Empty or 0")
        }
    }

    fun setFileName(s :String?): ImageWorker{

        if(s!=null && s.trim().isNotEmpty()){
            val name = s.replace(" ","")
            fileName = name
        }else{
            throw java.lang.IllegalArgumentException("File Name cannot be Empty")
        }
        return this@ImageWorker
    }

    fun setBitmap(b: Bitmap?):ImageWorker{

        if(b != null){
            bitmap = b
        }else{
            throw java.lang.IllegalArgumentException("Bitmap cannot be Empty")
        }
        return this@ImageWorker
    }

    fun saveBitmap(){

        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use setFileExtension() Method")
        }
        if(bitmap == null){
            throw NullPointerException("Bitmap Null. Use setBitmap() Method")
        }
        if(fileName == null){
            throw NullPointerException("File Name Null. Use setFileName() Method")
        }

        if(!writePermissionGranted()){
            throw SecurityException("Write External Storage Permission Not Granted")
        }


        val dir = if(subDirectory == null)
                File("${Environment.getExternalStorageDirectory().absolutePath}/$directory")
            else
                File("${Environment.getExternalStorageDirectory().absolutePath}/$directory/$subDirectory")

        //Create Directory If Doesn't Exist
        if(!dir.exists()){
            dir.mkdirs()
        }

        val file = File(dir,fileName)

        try{
            val outStream = FileOutputStream(file)

            when {
                fileExtension?.equals(Extension.PNG) as Boolean -> {
                    bitmap?.compress(Bitmap.CompressFormat.PNG,bitmapQuality,outStream)
                }
                fileExtension?.equals(Extension.JPEG) as Boolean -> {
                    bitmap?.compress(Bitmap.CompressFormat.JPEG,bitmapQuality,outStream)
                }
                fileExtension?.equals(Extension.WEBP) as Boolean -> {
                    bitmap?.compress(Bitmap.CompressFormat.WEBP,bitmapQuality,outStream)
                }
            }
            outStream.flush()
            outStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
        catch (e : IOException){
            e.printStackTrace()
        }
    }


    private fun writePermissionGranted(): Boolean{
        if(ContextCompat.checkSelfPermission(context.applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED){
            return true
        }
        return false
    }


}

enum class Extension(s: String){

    PNG(".png"), JPEG(".jpeg"), WEBP(".webp");

    val value: String = s
}
