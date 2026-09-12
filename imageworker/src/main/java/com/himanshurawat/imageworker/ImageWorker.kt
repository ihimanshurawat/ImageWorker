package com.himanshurawat.imageworker

import android.content.Context
import com.himanshurawat.imageworker.work.Convert
import com.himanshurawat.imageworker.work.From
import com.himanshurawat.imageworker.work.To

class ImageWorker {

    companion object {
        fun to(context: Context): To {
            return To(context)
        }

        fun from(context: Context): From {
            return From(context)
        }

        fun convert(): Convert {
            return Convert()
        }
    }
}

enum class Extension(val value: String) {
    PNG(".png"),
    JPEG(".jpeg"),
    JPG(".jpg"),
    WEBP(".webp");
}
