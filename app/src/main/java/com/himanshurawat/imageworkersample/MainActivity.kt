package com.himanshurawat.imageworkersample

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageWorker = ImageWorker(this).
            setDirectory("anime     wallpapers").
            setExtension(Extension.JPEG).
            setFileName("    1").
            setSubDirectory("yahooo").
            setSubDirectory("chutiya").
            setSubDirectory("probro").saveBitmap()




    }



}
