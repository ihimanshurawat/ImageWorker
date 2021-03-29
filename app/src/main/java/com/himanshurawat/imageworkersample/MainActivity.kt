package com.himanshurawat.imageworkersample

import android.app.usage.UsageStats
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bitmapDrawable = ContextCompat.getDrawable(this,R.drawable.rose) as BitmapDrawable

        button.setOnClickListener {
            dispatchTakePictureIntent()
        }

        val bitmap = ImageWorker.from(this).directory("Image Worker").
        subDirectory("Sub").
        setFileName("Image").
        withExtension(Extension.PNG).load()

        imageView.setImageBitmap(bitmap)

        Log.i("Testing", " IS IT WORKING?")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            ImageWorker.to(this).
                directory("Image Worker").
                subDirectory("Sub").
                setFileName("Image").
                withExtension(Extension.PNG).
                save(imageBitmap)
        }
    }


    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


}
