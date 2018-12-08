package com.himanshurawat.imageworker

import android.app.job.JobInfo
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
import android.provider.MediaStore
import android.content.ComponentName
import com.himanshurawat.imageworker.work.Convert
import com.himanshurawat.imageworker.work.From
import com.himanshurawat.imageworker.work.To


class ImageWorker {

    companion object {
        fun to(context: Context): To {
            return To(context)
        }

        fun from(context: Context): From{
            return From(context)
        }

        fun convert(): Convert{
            return Convert()
        }

    }



}

enum class Extension(s: String){

    PNG(".png"), JPEG(".jpeg"), WEBP(".webp");

    val value: String = s
}
