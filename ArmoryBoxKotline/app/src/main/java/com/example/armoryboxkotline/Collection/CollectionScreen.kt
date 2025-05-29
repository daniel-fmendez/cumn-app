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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.armoryboxkotline.Conection.Controller.CardCollection
import com.example.armoryboxkotline.Conection.Controller.CardRoot
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import com.example.armoryboxkotline.Conection.Controller.CollectionViewModel
import com.example.armoryboxkotline.Conection.Controller.DecksViewModel
import com.example.armoryboxkotline.Conection.SessionManager
import com.example.armoryboxkotline.R
import com.example.armoryboxkotline.Screen
import kotlinx.coroutines.async

@Composable
fun CollectionScreen(
    navController: NavController,
     // ahora se recibe el ViewModel desde fuera
) {
    val decksViewModel = viewModel<DecksViewModel>()
    val cardsViewModel = viewModel<CardsViewModel>()
    val collectionViewModel = viewModel<CollectionViewModel>()

    val cards by collectionViewModel.collection.collectAsState(initial = emptyList())


    LaunchedEffect (cards) {
        collectionViewModel.userCollection(SessionManager.userId!!)
    }

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

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (cards.isEmpty()) {
                Text("No hay cartas que coincidan.", modifier = Modifier.padding(8.dp))
            } else {
                cards.forEach { card ->
                    CardItem(
                        navController,
                        cardCollection = card,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CardItem(
    navController: NavController,
    cardCollection: CardCollection,
) {
    val collectionViewModel = viewModel<CollectionViewModel>()
    val cardsViewModel = viewModel<CardsViewModel>()
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var card by remember { mutableStateOf<CardRoot?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(cardCollection.card_id) {
        isLoading = true
        try {
            // Ejecutar ambas operaciones en paralelo si es posible
            val imageUrlDeferred = async { cardsViewModel.getFirstImageUrlDirect(cardCollection.card_id) }
            val cardDeferred = async { cardsViewModel.getCardByIdDirect(cardCollection.card_id) }

            imageUrl = imageUrlDeferred.await()
            card = cardDeferred.await()
        } catch (e: Exception) {
            Log.e("CardItem", "Error loading card data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

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
                        card?.let {
                            // Pasar la id de la carta
                            navController.navigate(Screen.Details.createRoute(it.uniqueId))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null && imageUrl!!.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Card image",
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
                            Log.e("ImageLoading", "Failed to load image: $imageUrl")
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.magnify),
                                    contentDescription = "Error loading image",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
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
                if (isLoading) {
                    Text("Cargando...", style = MaterialTheme.typography.titleMedium)
                } else {
                    card?.let { cardData ->
                        Text(cardData.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            generateSubtitle(cardData.types),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } ?: run {
                        Text("Error al cargar", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        collectionViewModel.updateCollection(SessionManager.userId!!,cardCollection.card_id,cardCollection.quantity-1)
                        collectionViewModel.userCollection(SessionManager.userId!!)
                    },
                    enabled = cardCollection.quantity > 0
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Quitar")
                }
                Text(
                    cardCollection.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = {
                    collectionViewModel.updateCollection(SessionManager.userId!!,cardCollection.card_id,cardCollection.quantity+1)
                    collectionViewModel.userCollection(SessionManager.userId!!)
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        }
    }
}

// Función auxiliar fuera del Composable
private fun generateSubtitle(list: List<String>): String {
    return list.joinToString(" · ")
}