package com.example.armoryboxkotline.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


val DarkBackground = Color(0xFF121212)     // Fondo principal oscuro
val DarkSurface = Color(0xFF1E1E1E)        // Superficie de tarjetas/barras
val DarkSurfaceVariant = Color(0xFF2A2A2A) // Variante más clara para elementos
val DarkSurfaceContainer = Color(0xFF333333) // Para contenedores secundarios
val Gold = Color(0xFFD4AF37)               // Color de acento principal
val Bronze = Color(0xFFB87333)             // Color alternativo para elementos
val Silver = Color(0xFFC0C0C0)             // Para textos secundarios
val White = Color(0xFFFFFFFF)               // Para textos principales

private val DarkColorScheme = darkColorScheme(
    primary = Gold,                // Color de acento principal (botones, elementos destacados)
    onPrimary = Color.Black,       // Color de texto sobre elementos primary
    secondary = Silver,            // Color secundario para elementos menos destacados
    onSecondary = Color.Black,     // Color de texto sobre elementos secondary
    tertiary = Bronze,             // Color terciario para variedad visual
    background = DarkBackground,   // Color de fondo de la pantalla
    surface = DarkSurface,         // Color de tarjetas y superficies elevadas
    surfaceVariant = DarkSurfaceContainer, //Color para otras tarjetas
    onBackground = White,          // Color de texto sobre el fondo
    onSurface = White,              // Color de texto sobre superficies
    onSurfaceVariant = White
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = Silver.copy(alpha = 0.7f),  // Versión más suave para tema claro
    onSecondary = Color.Black,
    tertiary = Bronze,
    background = Color.White,
    surface = Color(0xFFF5F5F5),
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun ArmoryBoxKotlineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}