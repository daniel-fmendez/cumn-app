package com.example.armoryboxkotline

import android.graphics.*
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalGetImage::class)
fun ImageProxy.toBitmapFromImageProxy(): Bitmap? {
    val image = this.image ?: return null
    if (image.format != ImageFormat.YUV_420_888) return null

    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, this.width, this.height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
