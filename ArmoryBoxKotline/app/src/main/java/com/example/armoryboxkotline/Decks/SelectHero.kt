package com.example.armoryboxkotline.Decks

import android.provider.DocumentsContract.Root
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.armoryboxkotline.Conection.Controller.CardEdition
import com.example.armoryboxkotline.Conection.Controller.CardRoot
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import com.example.armoryboxkotline.Conection.Controller.HeroesViewModel
import com.example.armoryboxkotline.FakeTopBar
import com.example.armoryboxkotline.R
import com.example.armoryboxkotline.ValueBox

@Composable
fun SelectHeroScreen(onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val heroesViewModel = viewModel<HeroesViewModel>()
    val heroes by heroesViewModel.heroes.collectAsState(initial = emptyList())
    val cardImages by heroesViewModel.cardImages.collectAsState()

    LaunchedEffect (heroesViewModel) {
        heroesViewModel.fetchHeroes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column {
            SubMenuTopBar("Selecciona Heroe",
                onClose = {
                    onDismiss()
                }
            )

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                heroes.forEach{ hero ->
                    var imageUrl = cardImages.get(hero.uniqueId)
                    HeroDisplayCard(hero,imageUrl?:""){ selected ->
                        onSelect(selected)
                    }
                    //HeroDisplayCard()
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroDisplayCard(card: CardRoot, imageUrl: String, onSelect: (String) -> Unit){

    val cardsViewModel = viewModel<CardsViewModel>()

    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(card.uniqueId) {
        cardsViewModel.getFirstImageUrlDirect(card.uniqueId)
        imageUrl = cardsViewModel.getFirstImageUrlDirect(card.uniqueId)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.Top),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                        .width(64.dp)
                        .height(72.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {

                    Box (
                        modifier = Modifier
                            .width(75.dp)
                            .height(80.dp)
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl != null && imageUrl!!.isNotEmpty()) {
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
                }
            }

            Box(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.Top), // Alinea este contenido arriba
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 12.dp)
                ) {
                    Text(
                        text = card.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    fun generateSubtitle(list: List<String> ): String {
                        var result = ""
                        list.forEach{ item ->
                            if(result.isEmpty()){
                                result += item
                            }else{
                                result += (" · "+item)
                            }
                        }
                        return result
                    }
                    Text(
                        text = generateSubtitle(card.types),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(16.dp))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(10.dp) // Añade espaciado vertical
                    ){
                        ValueBox("Health",card.health)
                        ValueBox("Intelligence",card.intelligence)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)

                    .padding(start = 8.dp,end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        onSelect(card.uniqueId)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
@Composable
fun SubMenuTopBar(text: String, onClose: () -> Unit){
    Box (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.CenterStart
    ){

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start // O End, Center, SpaceAround, SpaceBetween, SpaceEvenly
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                //.background(Color.Red)
            ) {
                IconButton(
                    onClick = { onClose()},
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Volver")
                }
            }

            Box(
                modifier = Modifier
                    .weight(4f) // Este espacio es el más grande
                    .fillMaxHeight()
                //.background(Color.Blue)
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                //Introducir algo aqui si se desea
            }
        }
    }
}

