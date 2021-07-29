package com.himanshurawat.imageworkersample

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.content.ActivityNotFoundException
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
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)
        val bitmapDrawable = ContextCompat.getDrawable(this,R.drawable.rose) as BitmapDrawable

        button.setOnClickListener {

            //dispatchTakePictureIntent()
            //val bool = ImageWorker.from(this).directory("Image Worker").subDirectory("Sub").setFileName("Image").withExtension(Extension.PNG).delete()
            //Toast.makeText(this, "Hello $bool",Toast.LENGTH_SHORT).show()
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
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }



}
