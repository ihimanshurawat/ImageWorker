package com.himanshurawat.imageworker.work

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException



//Testing is Still Pending for this Class
class Convert {

    fun bitmapToDrawable(bitmap: Bitmap): Drawable?{

        return BitmapDrawable(Resources.getSystem(),bitmap)
    }

    fun bitmapToBase64(bitmap: Bitmap): String?{
//        if(bitmap == null){
//            throw IllegalArgumentException("Bitmap cannot be Null")
//        }
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        val byteArray: ByteArray = outputStream.toByteArray()


        return Base64.encodeToString(byteArray,Base64.DEFAULT)
    }

    fun base64ToBitmap(base64: String): Bitmap?{
        if(base64.isEmpty()){
            throw IllegalArgumentException("Base64 Encoded String cannot be Empty")
        }

        val byteArray = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }


    fun base64ToDrawable(base64: String): Drawable?{
        if(base64.isEmpty()){
            throw IllegalArgumentException("Base64 Encoded String cannot be Empty")
        }

        return BitmapDrawable(Resources.getSystem(),base64ToBitmap(base64))
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap?{

//        if(drawable == null){
//            throw IllegalArgumentException("Drawable cannot be Null")
//        }

        if(drawable is BitmapDrawable){
            return drawable.bitmap
        }

        var width = drawable.intrinsicWidth
        width = if (width > 0) width else 1
        var height = drawable.intrinsicHeight
        height = if (height > 0) height else 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun drawableToBase64(drawable: Drawable?): String?{
        if(drawable == null){
            throw IllegalArgumentException("Drawable cannot be Null or Empty")
        }

        return bitmapToBase64(drawableToBitmap(drawable) as Bitmap)
    }


}