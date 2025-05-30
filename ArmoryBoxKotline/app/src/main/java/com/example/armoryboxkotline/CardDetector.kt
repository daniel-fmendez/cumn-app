package com.example.armoryboxkotline

import android.graphics.Bitmap
import android.content.Context
import android.util.Log
import boofcv.alg.filter.binary.BinaryImageOps
import boofcv.alg.filter.binary.GThresholdImageOps
import boofcv.android.ConvertBitmap
import boofcv.struct.ConnectRule
import boofcv.struct.image.GrayU8
import georegression.struct.point.Point2D_I32 as BoofPoint2D_I32
import java.io.File
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

object CardDetector {

    fun detectarContornos(context: Context, bitmap: Bitmap): List<List<Point2D_I32>> {
        // Guardar bitmap original para debug
        try {
            File(context.cacheDir, "debug_original.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Log.d("CardDetector", "Imagen original guardada")
        } catch (e: Exception) {
            Log.e("CardDetector", "Error guardando debug_original", e)
        }

        val gray = GrayU8(bitmap.width, bitmap.height)

        // Conversión manual a gris para asegurar que no haya problemas con ConvertBitmap
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                val grayValue = (r + g + b) / 3
                gray.set(x, y, grayValue)
            }
        }

        // Binarización con umbral global simple
        val binarizada = GrayU8(gray.width, gray.height)
        val umbralGlobal = 120.0
        GThresholdImageOps.threshold(gray, binarizada, umbralGlobal, true)

        // Invertir binarizada para que contornos negros sobre blanco pasen a blanco sobre negro
        val binInvertida = GrayU8(binarizada.width, binarizada.height)
        BinaryImageOps.invert(binarizada, binInvertida)

        // Detectar contornos sobre imagen invertida
        val contornosBoofcv = BinaryImageOps.contour(binInvertida, ConnectRule.EIGHT, null)
        Log.d("CardDetector", "Contornos detectados: ${contornosBoofcv.size}")

        val contornos = contornosBoofcv.map { contorno ->
            contorno.external.map { puntoBoofcv ->
                Point2D_I32(puntoBoofcv.x, puntoBoofcv.y)
            }
        }

        // Guardar imágenes solo si se detectan contornos
        if (contornos.isNotEmpty()) {
            try {
                // Guardar imagen gris
                val grayBitmap = Bitmap.createBitmap(gray.width, gray.height, Bitmap.Config.ARGB_8888)
                ConvertBitmap.grayToBitmap(gray, grayBitmap, null)
                File(context.cacheDir, "debug_gray.png").outputStream().use {
                    grayBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                // Guardar imagen binarizada invertida (para visualizar la que se analiza)
                val binBitmap = Bitmap.createBitmap(binInvertida.width, binInvertida.height, Bitmap.Config.ARGB_8888)
                ConvertBitmap.grayToBitmap(binInvertida, binBitmap, null)
                File(context.cacheDir, "debug_bin.png").outputStream().use {
                    binBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                // Dibujar contornos sobre la imagen original y guardar
                val resultado = dibujarContornos(context, bitmap, contornos)
                File(context.cacheDir, "frame_debug.png").outputStream().use {
                    resultado.compress(Bitmap.CompressFormat.PNG, 100, it)
                }

                Log.d("CardDetector", "Debug imágenes guardadas: debug_gray, debug_bin, frame_debug")
            } catch (e: Exception) {
                Log.e("CardDetector", "Error guardando imágenes debug", e)
            }
        } else {
            Log.d("CardDetector", "No se detectaron contornos, no se guardan imágenes debug")
        }

        return contornos
    }

    fun dibujarContornos(
        context: Context,
        original: Bitmap,
        contornos: List<List<Point2D_I32>>
    ): Bitmap {
        val resultado = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultado)

        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        for (contorno in contornos) {
            if (contorno.size < 3) continue
            val path = Path().apply {
                moveTo(contorno[0].x.toFloat(), contorno[0].y.toFloat())
                for (i in 1 until contorno.size) {
                    lineTo(contorno[i].x.toFloat(), contorno[i].y.toFloat())
                }
                close()
            }
            canvas.drawPath(path, paint)
        }

        return resultado
    }
}
