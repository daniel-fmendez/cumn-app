package com.example.armoryboxkotline

import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.armoryboxkotline.toBitmapFromImageProxy
import kotlin.math.max
import kotlin.math.min

@Composable
fun ScannerScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) { permissionLauncher.launch(android.Manifest.permission.CAMERA) }

    if (hasCameraPermission) {
        CameraPreviewWithContours(lifecycleOwner)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Permiso de cámara denegado. Activa el permiso para usar esta función.")
        }
    }
}

@Composable
fun CameraPreviewWithContours(lifecycleOwner: androidx.lifecycle.LifecycleOwner) {
    val context = LocalContext.current

    var previewWidth by remember { mutableStateOf(0) }
    var previewHeight by remember { mutableStateOf(0) }
    var bitmapWidth by remember { mutableStateOf(1) }
    var bitmapHeight by remember { mutableStateOf(1) }

    var rectsConTiempo by remember { mutableStateOf<List<Pair<Rect, Long>>>(emptyList()) }

    val cartaWidthMm = 63f
    val cartaHeightMm = 88f
    val areaCarta = cartaWidthMm * cartaHeightMm

    LaunchedEffect(rectsConTiempo) {
        val now = System.currentTimeMillis()
        rectsConTiempo = rectsConTiempo.filter { now - it.second <= 1000 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Cambiado de FILL_CENTER a FIT_CENTER para evitar recortes y facilitar escalado correcto
                scaleType = PreviewView.ScaleType.FIT_CENTER

                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    previewWidth = width
                    previewHeight = height
                }

                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                val bitmap = imageProxy.toBitmapFromImageProxy()
                                if (bitmap != null) {
                                    try {
                                        val contornos = CardDetector.detectarContornos(ctx, bitmap)
                                        bitmapWidth = bitmap.width
                                        bitmapHeight = bitmap.height

                                        val nuevosRects = contornos.mapNotNull { contorno ->
                                            if (contorno.isEmpty()) return@mapNotNull null

                                            val minX = contorno.minOf { it.x }
                                            val maxX = contorno.maxOf { it.x }
                                            val minY = contorno.minOf { it.y }
                                            val maxY = contorno.maxOf { it.y }

                                            val ancho = maxX - minX
                                            val alto = maxY - minY
                                            val lado = max(ancho, alto)

                                            val areaContorno = ancho.toFloat() * alto.toFloat()
                                            val escalaPxPorMm = lado.toFloat() / max(cartaWidthMm, cartaHeightMm)
                                            val areaContornoMm2 = areaContorno / (escalaPxPorMm * escalaPxPorMm)

                                            if (areaContornoMm2 < areaCarta * 0.5f) return@mapNotNull null

                                            val centerX = (minX + maxX) / 2
                                            val centerY = (minY + maxY) / 2

                                            val left = centerX - lado / 2
                                            val top = centerY - lado / 2

                                            Rect(left, top, left + lado, top + lado)
                                        }

                                        val now = System.currentTimeMillis()
                                        rectsConTiempo = rectsConTiempo
                                            .filter { now - it.second <= 1000 } +
                                                nuevosRects.map { it to now }

                                    } catch (e: Exception) {
                                        Log.e("ScannerScreen", "Error en detección", e)
                                    }
                                }
                                imageProxy.close()
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        Log.e("ScannerScreen", "Error al iniciar la cámara", e)
                    }

                }, ContextCompat.getMainExecutor(ctx))
            }
        }, modifier = Modifier.fillMaxSize())

        Canvas(modifier = Modifier.fillMaxSize()) {
            val now = System.currentTimeMillis()
            rectsConTiempo.filter { now - it.second <= 1000 }.forEach { (rect, _) ->

                // Escalado manteniendo la relación de aspecto y centrado en pantalla
                val scaleX = size.width / bitmapWidth.toFloat()
                val scaleY = size.height / bitmapHeight.toFloat()
                val scale = min(scaleX, scaleY)

                val offsetX = (size.width - bitmapWidth * scale) / 2f
                val offsetY = (size.height - bitmapHeight * scale) / 2f

                val left = rect.left * scale + offsetX
                val top = rect.top * scale + offsetY
                val right = rect.right * scale + offsetX
                val bottom = rect.bottom * scale + offsetY

                drawRect(
                    color = ComposeColor.Red,
                    topLeft = androidx.compose.ui.geometry.Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
        }
    }
}
