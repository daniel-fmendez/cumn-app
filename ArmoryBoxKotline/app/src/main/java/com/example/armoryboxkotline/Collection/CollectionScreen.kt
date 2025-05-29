package com.example.armoryboxkotline.Collection

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.armoryboxkotline.Conection.Controller.CardCollection
import com.example.armoryboxkotline.Conection.Controller.CollectionViewModel
import com.example.armoryboxkotline.R
import com.example.armoryboxkotline.Screen

@Composable
fun CollectionScreen(
    navController: NavController,
    viewModel: CollectionViewModel // ahora se recibe el ViewModel desde fuera
) {

    /*val searchQuery by viewModel.searchQuery.collectAsState()
    val cards by viewModel.filteredCards.collectAsState()*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi Colección",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        /*OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            label = { Text("Buscar cartas") },
            modifier = Modifier.fillMaxWidth()
        )*/

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            /*if (cards.isEmpty()) {
                Text("No hay cartas que coincidan.", modifier = Modifier.padding(8.dp))
            } else {
                cards.forEach { card ->
                    CardItem(
                        card = card,
                        onIncrement = { viewModel.incrementCardQuantity(card.id) },
                        onDecrement = { viewModel.decrementCardQuantity(card.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }*/
        }
    }
}

@Composable
fun CardItem(
    card: CardCollection,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(card.card_id, style = MaterialTheme.typography.titleMedium)
            }

            IconButton(onClick = onDecrement, enabled = card.quantity > 0) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Quitar")
            }
            Text("${card.quantity}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onIncrement) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    }
}

@Preview
@Composable
fun PreviewCollection () {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(75.dp)
                    .height(90.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        //Pasar la id de la carta
                        //navController.navigate(Screen.Details.createRoute(card.uniqueId))
                    },
                contentAlignment = Alignment.Center
            ) {
                var imageUrl: String? = null;
                if (imageUrl != null && imageUrl.isNotEmpty()) {
                    // Cargar la imagen con placeholder para errores y carga
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Card image: {card.name}",
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        error = {
                            // Log error
                            Log.e("ImageLoading", "Failed to load image: $imageUrl")
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.magnify), // Use an appropriate error icon
                                    contentDescription = "Error loading image",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Si no hay URL de imagen
                    Text(
                        text = "Arte",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("card.name", style = MaterialTheme.typography.titleMedium)
                Text("card.type", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }


            /*IconButton(onClick = onDecrement, enabled = card.quantity > 0) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Quitar")
            }*/
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Quitar")
            Text("4", style = MaterialTheme.typography.titleMedium)
            Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
            /*IconButton(onClick = onIncrement) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
            }*/
        }
    }
}