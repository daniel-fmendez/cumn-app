package com.example.armoryboxkotline


import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage

import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.armoryboxkotline.Conection.Controller.CardRoot
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import java.util.UUID

@Composable
fun SearchScreen(navController: NavController){
    val cardsViewModel = viewModel<CardsViewModel>()
    val cards by cardsViewModel.cards.collectAsState()
    val cardImages by cardsViewModel.cardImages.collectAsState()

    Box (modifier = Modifier
        .fillMaxSize(),
    ){
        SearchBar(navController, cardsViewModel, cards, cardImages)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchBar(
    navController: NavController,
    cardsViewModel: CardsViewModel,
    cards: List<CardRoot>,
    cardImages: Map<String, String?>
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchTriggered by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Filtrar cuando se haya activado búsqueda Y considerar searchQuery
    val filteredItems = remember(searchTriggered, cards) {
        if (searchTriggered) cards else emptyList()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            color = MaterialTheme.colorScheme.surface,
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Resetear el estado de búsqueda cuando cambia el texto
                    searchTriggered = false
                },
                placeholder = { Text("Buscar...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) {
                            cardsViewModel.searchCard(searchQuery)
                            searchTriggered = true
                            focusManager.clearFocus()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.magnify),
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            searchTriggered = false
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            cardsViewModel.searchCard(searchQuery)
                            searchTriggered = true
                            focusManager.clearFocus()
                        }
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (searchTriggered) {
            if (filteredItems.isEmpty()) {
                Text("No se encontraron resultados.", color = MaterialTheme.colorScheme.error)
            } else {
                var size = filteredItems.size
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Resultados ($size)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left
                    )

                    Spacer (modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espaciado vertical
                    ) {
                        filteredItems.forEach { card ->
                            //Cambiar por la carta
                            val imageUrl = cardImages[card.uniqueId]
                            CustomCard(navController, card, imageUrl ?: "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCard(navController: NavController, card: CardRoot, imageUrl: String) {
    //val parts = name.split(" ")

    Box(
        modifier = Modifier
            .width(85.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Esto ayuda a centrar todo
        ) {
            // Caja con borde dorado para la imagen
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(75.dp)
                    .height(90.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        //Pasar la id de la carta
                        navController.navigate(Screen.Details.createRoute(card.uniqueId))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null && imageUrl.isNotEmpty()) {
                    // Cargar la imagen con placeholder para errores y carga
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Card image: ${card.name}",
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

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // Contenedor para el texto con altura flexible y menos espacio
            Text(
                text = card.name,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            Spacer(
                modifier = Modifier.height(6.dp)
            )
        }
    }
}