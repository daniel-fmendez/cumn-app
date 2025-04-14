package com.example.armoryboxkotline

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope

/**
 * Extrae el área del arte de una carta de Flesh and Blood
 * @param originalBitmap el bitmap original de la carta
 * @param isFullArt indica si la carta es full art, lo que cambiará la región de recorte
 * @return un nuevo bitmap que contiene solo el área del arte
 */
fun extractCardArtwork(originalBitmap: Bitmap, isFullArt: Boolean = false): Bitmap {
    val width = originalBitmap.width
    val height = originalBitmap.height

    // Diferentes parámetros de recorte dependiendo del tipo de carta
    val cropRect = if (isFullArt) {
        // Para cartas full art, tomamos un área más grande que incluye casi toda la carta
        // excepto los bordes y la información de copyright
        Rect(
            (width * 0.05).toInt(),  // 5% desde la izquierda
            (height * 0.05).toInt(), // 5% desde arriba
            (width * 0.95).toInt(),  // 95% desde la izquierda
            (height * 0.75).toInt()  // 75% desde arriba (evita la parte inferior con texto)
        )
    } else {
        // Para cartas regulares, recortamos solo el rectángulo de arte estándar
        Rect(
            (width * 0.1).toInt(),   // 10% desde la izquierda
            (height * 0.1).toInt(),  // 10% desde arriba
            (width * 0.9).toInt(),   // 90% desde la izquierda
            (height * 0.6).toInt()   // 60% desde arriba (solo el cuadro de arte)
        )
    }

    // Crear un nuevo bitmap del tamaño recortado
    val resultBitmap = Bitmap.createBitmap(
        cropRect.width(),
        cropRect.height(),
        Bitmap.Config.ARGB_8888
    )

    // Dibujar la región recortada en el nuevo bitmap
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(
        originalBitmap,
        cropRect,
        Rect(0, 0, cropRect.width(), cropRect.height()),
        null
    )

    return resultBitmap
}

/**
 * Componente para mostrar una carta en modo normal y permitir ampliarla a pantalla completa
 */
@Composable
fun ExpandableCardView(resourceId: Int) {
    var showFullScreen by remember { mutableStateOf(false) }

    // Vista normal de la carta (esta es la que se ve en la pantalla de detalles)
    Box(
        modifier = Modifier
            .clickable { showFullScreen = true }
    ) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "Card Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Pantalla completa cuando se hace clic
    if (showFullScreen) {
        FullScreenCardDialog(
            resourceId = resourceId,
            onDismiss = { showFullScreen = false }
        )
    }
}

/**
 * Diálogo de pantalla completa con zoom y gestos
 */
@Composable
fun FullScreenCardDialog(resourceId: Int, onDismiss: () -> Unit) {
    // Variables para controlar el zoom y posición
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            // Restablecer zoom y posición con doble clic
                            scale = if (scale > 1f) 1f else 2f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        onTap = {
                            // Mantener esto vacío para que el clic simple no cierre el diálogo
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Actualizar el zoom (con límites)
                        scale = (scale * zoom).coerceIn(0.5f, 3f)

                        // Ajustar la posición con límites basados en el zoom
                        val maxX = (scale - 1) * size.width / 2
                        val maxY = (scale - 1) * size.height / 2

                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    }
                }
        ) {
            // La imagen ampliable
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Full Screen Card",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            )

            // Botón para cerrar
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Versión que extraerá solo el arte al hacer clic
 */
@Composable
fun ExpandableCardArtView(resourceId: Int, isFullArt: Boolean = false) {
    var showFullScreen by remember { mutableStateOf(false) }
    var showOnlyArt by remember { mutableStateOf(false) }

    // Vista normal de la carta o del arte
    Box(
        modifier = Modifier
            .clickable { showFullScreen = true }
    ) {
        // Mostrar la carta completa inicialmente
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "Card Image",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxSize()
        )
    }

    // Pantalla completa cuando se hace clic
    if (showFullScreen) {
        FullScreenCardWithOptionsDialog(
            resourceId = resourceId,
            isFullArt = isFullArt,
            onDismiss = { showFullScreen = false },
            initialShowOnlyArt = showOnlyArt,
            onArtToggle = { showOnlyArt = it }
        )
    }
}

/**
 * Diálogo de pantalla completa con opción para mostrar solo el arte
 */
@Composable
fun FullScreenCardWithOptionsDialog(
    resourceId: Int,
    isFullArt: Boolean,
    onDismiss: () -> Unit,
    initialShowOnlyArt: Boolean = false,
    onArtToggle: (Boolean) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var showOnlyArt by remember { mutableStateOf(initialShowOnlyArt) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale > 1f) 1f else 2f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 3f)

                        val maxX = (scale - 1) * size.width / 2
                        val maxY = (scale - 1) * size.height / 2

                        offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                        offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)
                    }
                }
        ) {
            val context = LocalContext.current

            // Mostrar carta completa o solo el arte según la selección
            if (showOnlyArt) {
                val originalBitmap = context.resources.getDrawable(resourceId, null).toBitmap()
                val artworkBitmap = extractCardArtwork(originalBitmap, isFullArt)

                Image(
                    bitmap = artworkBitmap.asImageBitmap(),
                    contentDescription = "Card Artwork",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            } else {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Full Card",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            }

            // Controles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Botón para cerrar
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }

                // Toggle para cambiar entre carta completa y solo arte
                Switch(
                    checked = showOnlyArt,
                    onCheckedChange = {
                        showOnlyArt = it
                        onArtToggle(it)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .padding(8.dp)
                )
            }
        }
    }
}
