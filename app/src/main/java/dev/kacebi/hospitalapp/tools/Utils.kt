package dev.kacebi.hospitalapp.tools

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object Utils {
    fun bitmapToByteArray(image: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()

//        val size: Int =
//            bitmap.rowBytes * bitmap.height
//        val byteBuffer: ByteBuffer = ByteBuffer.allocate(size)
//        bitmap.copyPixelsToBuffer(byteBuffer)
//
//        return byteBuffer.array()
    }

}