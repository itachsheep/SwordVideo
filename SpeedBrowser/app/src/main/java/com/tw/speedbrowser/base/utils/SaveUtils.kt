package com.tw.speedbrowser.base.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.OutputStream

class SaveUtils {

    object Fun {

        fun saveBitmap2Galley(context: Context, bitmap: Bitmap, name: String,needToast:Boolean = true): Uri? {

            if (bitmap == null) {
                return null
            }

            var isSuccessed: Boolean = false
            val fos: OutputStream?
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/bingx")
            val imageUri =
                    context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            fos = imageUri?.let { context.contentResolver.openOutputStream(it) }
            fos?.let {
                isSuccessed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }

            if (needToast) {
//                if (isSuccessed) {
//                    SafeToast.makeImageText(context.getString(R.string.save_success))
//                } else {
//                    SafeToast.makeText(context.getString(R.string.save_error))
//                }
            }

            fos?.flush()
            fos?.close()

            if (imageUri != null) {
                return imageUri
            }
            return null
        }
    }

}