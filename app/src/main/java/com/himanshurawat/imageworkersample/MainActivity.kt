package com.himanshurawat.imageworkersample

import android.app.usage.UsageStats
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.himanshurawat.imageworker.Extension
import com.himanshurawat.imageworker.ImageWorker
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bitmapDrawable = ContextCompat.getDrawable(this,R.drawable.rose) as BitmapDrawable

        button.setOnClickListener({
            dispatchTakePictureIntent()
        })


        imageView.setImageBitmap(ImageWorker.from(this).directory("ImageWorker").
            subDirectory("SubDirectory").
            setFileName("Image").
            withExtension(Extension.PNG).load())





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            ImageWorker.to(this).
                directory("ImageWorker").
                subDirectory("SubDirectory").
                setFileName("Image").
                withExtension(Extension.PNG).
                save(imageBitmap)


        }
    }


    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


}
