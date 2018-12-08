package com.himanshurawat.imageworker.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Base64
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.NullPointerException

class To(private val context: Context) {

    private var fileExtension: String? = null
    private var fileName: String? = null
    private var subDirectory: String? = null
    private var directory: String? = context.applicationContext.packageName

    //Setting Up Directory
    fun directory(dir: String?): To {

        if(dir!= null && dir.trim().isNotEmpty()){
            val directoryString = dir.replace(" ", "")
            directory = directoryString
        }else{
            throw IllegalArgumentException("Directory cannot be Empty or Null")
        }

        return this
    }

    //Set Sub Directory
    fun subDirectory(subDir: String?): To {

        if(subDir!=null && subDir.trim().isNotEmpty()){
            val subDirectoryName = subDir.replace(" ","")
            if(subDirectory != null) {
                subDirectory = "$subDirectory/$subDirectoryName"
            }else{
                subDirectory = subDirectoryName
            }
        }else{
            throw java.lang.IllegalArgumentException("Sub Directory Name cannot be Empty or Null")
        }
        return this
    }


    //Set File Extension
    fun withExtension(e: Extension): To {

        fileExtension = e.value

        return this
    }

    fun setBitmapQuality(q: Int?){


    }

    fun setFileName(s :String?): To {

        if(s!=null && s.trim().isNotEmpty()){
            val name = s.replace(" ","")
            fileName = name
        }else{
            throw java.lang.IllegalArgumentException("File Name cannot be Empty or Null")
        }
        return this
    }

    //Synchronous Function to Save File to Directory
    fun save(bitmap: Bitmap?, quality: Int? = 100){

        if(quality == null || quality < 1 && quality <= 100){
            throw java.lang.IllegalArgumentException("Bitmap Quality must be between 1 to 100")
        }

        if(bitmap == null){
            throw java.lang.IllegalArgumentException("Bitmap cannot be Empty or Null")
        }

        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use setFileExtension() Method")
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

        val file = File(dir,"$fileName$fileExtension")

        val outStream: FileOutputStream

        try{

            outStream = FileOutputStream(file)

            if(fileExtension!!.equals(".png",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".jpeg",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".webp",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
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


    fun save(base64: String?,quality: Int? = 100){

        if(quality == null || quality < 1 && quality <= 100){
            throw java.lang.IllegalArgumentException("Quality must be between 1 to 100")
        }

        if(base64 == null || base64.isEmpty()){
            throw IllegalArgumentException("Base64 Encoded String should't be Null or Empty")
        }
        val decodedString: ByteArray = Base64.decode(base64,Base64.DEFAULT)
        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedString,0,decodedString.size)

        if(bitmap == null){
            throw NullPointerException("Decoding Base64 String results in Null Bitmap")
        }


        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use setFileExtension() Method")
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

        val file = File(dir,"$fileName$fileExtension")

        val outStream: FileOutputStream

        try{

            outStream = FileOutputStream(file)

            if(fileExtension!!.equals(".png",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".jpeg",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".webp",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
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

    fun save(drawable: Drawable?, quality: Int? = 100){
        if(quality == null || quality < 1 && quality <= 100){
            throw java.lang.IllegalArgumentException("Quality must be between 1 to 100")
        }
        if(drawable == null){
            throw java.lang.IllegalArgumentException("Drawable cannot be Null")
        }

        val bitmap = ImageWorker.convert().drawableToBitmap(drawable)

        if(bitmap == null){
            throw NullPointerException("Decoding Base64 String results in Null Bitmap")
        }


        if(fileExtension == null){
            throw NullPointerException("File Extension Null. Use setFileExtension() Method")
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

        val file = File(dir,"$fileName$fileExtension")

        val outStream: FileOutputStream

        try{

            outStream = FileOutputStream(file)

            if(fileExtension!!.equals(".png",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".jpeg",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
            }
            if(fileExtension!!.equals(".webp",true)){
                bitmap.compress(Bitmap.CompressFormat.PNG,quality,outStream)
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