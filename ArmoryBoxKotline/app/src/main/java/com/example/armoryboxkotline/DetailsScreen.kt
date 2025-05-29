package com.example.armoryboxkotline

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.armoryboxkotline.Collection.CollectionScreen
import com.example.armoryboxkotline.Conection.Controller.CardEdition
import com.example.armoryboxkotline.Conection.Controller.CardRoot
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import com.example.armoryboxkotline.Conection.Controller.CollectionViewModel
import com.example.armoryboxkotline.Conection.SessionManager


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(navController: NavController, cardId: String){
    val cardsViewModel = viewModel<CardsViewModel>()
    val card by cardsViewModel.card.observeAsState(initial = null)
    val editionsMap by cardsViewModel.editions.collectAsState()
    var editions = editionsMap[cardId].orEmpty()
    val scrollState = rememberLazyListState()

    val collectionViewModel = viewModel<CollectionViewModel>()
    val collection by collectionViewModel.collection.collectAsState(initial = emptyList())
    val id = SessionManager.userId ?: -1
    var buttonVisible by remember { mutableStateOf(true) }
    LaunchedEffect(cardId) {
        cardsViewModel.fetchCardById(cardId)
        cardsViewModel.getEditions(cardId)

        if (id != -1) {
            collectionViewModel.userCollection(id)
        }
    }
    if(card == null){
        EmpryScreen("Problema al encontrar la carta")
    }else{
        val thisCard = card!!
        // Determinar si el usuario ha hecho scroll para mostrar la topbar
        val showTopBar by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex > 0 ||
                        (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset > 250)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Contenido principal con la imagen y el texto
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    // Imagen superior
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if(editions.isNotEmpty()){
                                ExpandableCardArtView(
                                    imageUrl = editions.get(0).imageUrl,
                                    isFullArt = false // Cambia a true para cartas full art
                                )
                            }

                        }

                        // Solo mostramos este botón de regreso si la topbar no está visible
                        if (!showTopBar) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.TopStart)
                                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Atrás",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                item {
                    Column() {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(
                                        bottomStart = 16.dp,
                                        bottomEnd = 16.dp
                                    )
                                )
                                .padding(16.dp),
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = thisCard.name,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(
                                            modifier = Modifier.height(2.dp)
                                        )
                                        fun generateSubtitle(list: List<String>): String {
                                            var result = ""
                                            list.forEach { item ->
                                                if (result.isEmpty()) {
                                                    result += item
                                                } else {
                                                    result += (" · " + item)
                                                }
                                            }
                                            return result
                                        }
                                        Text(
                                            text = generateSubtitle(thisCard.types),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    val id = SessionManager.userId ?: -1
                                    if (id != -1) {
                                        if(!collection.any { it.card_id == cardId }){
                                            Button(
                                                onClick = {
                                                    collectionViewModel.updateCollection(id,cardId,1)
                                                    buttonVisible = false
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                enabled = buttonVisible,
                                                modifier = Modifier.alpha(if (buttonVisible) 1f else 0f)
                                            ) {
                                                Text(
                                                    text = "Añadir",
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(16.dp)
                                )

                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (!thisCard.pitch.isEmpty()) {
                                        ValueBox("Pitch", thisCard.pitch)
                                    }
                                    if (!thisCard.cost.isEmpty()) {
                                        ValueBox("Cost", thisCard.cost)
                                    }
                                    if (!thisCard.power.isEmpty()) {
                                        ValueBox("Power", thisCard.power)
                                    }
                                    if (!thisCard.defense.isEmpty()) {
                                        ValueBox("Defense", thisCard.defense)
                                    }
                                    if (!thisCard.health.isEmpty()) {
                                        ValueBox("Health", thisCard.health)
                                    }
                                    if (!thisCard.intelligence.isEmpty()) {
                                        ValueBox("Intelligence", thisCard.intelligence)
                                    }
                                    if (!thisCard.arcane.isEmpty()) {
                                        ValueBox("Arcane", thisCard.arcane)
                                    }
                                }

                                Spacer(
                                    modifier = Modifier.height(16.dp)
                                )
                                CardText(thisCard.functionalText)
                            }
                        }
                        fun getSets(editions: List<CardEdition>): Map<String, List<CardEdition>> {
                            return editions.groupBy { it.setId }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (editions.isNotEmpty()) {
                            val sets: Map<String, List<CardEdition>> = getSets(editions)

                            for ((setId, editionsInSet) in sets) {
                                TitleBar(setId, editionsInSet.size)
                                Spacer(modifier = Modifier.height(16.dp))

                                for (edition in editionsInSet) {
                                    VersionCard(edition)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                       // Text("Mucho contenido...".repeat(100))
                    }
                }
            }

            // TopBar que aparece solo al hacer scroll
            AnimatedVisibility(
                visible = showTopBar,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                FakeTopBar(navController, name = thisCard.name)
            }
        }
    }

}

@Composable
fun FakeTopBar(navController: NavController,name: String){
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
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }

            Box(
                modifier = Modifier
                    .weight(4f) // Este espacio es el más grande
                    .fillMaxHeight()
                    //.background(Color.Blue)
            ) {
                Text(
                    text = name,
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

@Composable
fun ValueBox(title: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface){
    Box(
        modifier =  Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),

        contentAlignment = Alignment.Center
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, start = 16.dp, end = 16.dp)
            )
            Text(
                text = value,
                fontSize = 18.sp,
                color = color,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp,bottom = 6.dp) // Asegura que el texto se centre en el ancho del Box
            )
        }

    }
}

@Composable
fun CardText(text: String){
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ){

        Text(
            text = "Texto de la Carta",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ){
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TitleBar(title: String, versions: Int){
    Surface (
        modifier = Modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ){
        Row {
            Box (
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxSize()
                    .wrapContentHeight(align = Alignment.Top),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp,start = 16.dp)
                )
            }
            Box (
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .wrapContentHeight(align = Alignment.Top),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "$versions versiones",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp,end = 8.dp)
                )
            }
        }
    }
}

enum class Rarity(val code: String, val displayName: String) {
    TOKEN("T", "Token"),
    COMMON("C", "Common"),
    RARE("R", "Rare"),
    SUPER_RARE("S", "Super Rare"),
    MAJESTIC("M", "Majestic"),
    LEGENDARY("L", "Legendary"),
    MARVEL("F", "Marvel"),
    PROMO("P", "Promo"),

    UNKNOWN("UNKNOWN", "Desconocido");

    companion object {
        fun fromCode(code: String): Rarity {
            return when {
                code.contains("MV") || code.contains("F") -> MARVEL
                else -> values().firstOrNull { it.code == code } ?: UNKNOWN
            }
        }
    }
}


@Composable
fun VersionCard(cardEdition: CardEdition) {
    val cardsViewModel = viewModel<CardsViewModel>()
    val card by cardsViewModel.card.observeAsState(initial = null)

    var aux_rarity = Rarity.fromCode(cardEdition.rarity)
    var rarity = aux_rarity.displayName

    LaunchedEffect(cardEdition.cardId) {
        cardsViewModel.fetchCardById(cardEdition.cardUniqueId)

        var aux_rarity = Rarity.fromCode(cardEdition.rarity)
        rarity = aux_rarity.displayName
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
                    var imageUrl = cardEdition.imageUrl

                    if(card != null && imageUrl != null){
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(cardEdition.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Card image: ${card!!.name}",
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
                    }else{
                        Text(
                            text = "Arte",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                        text = card!!.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Rareza: $rarity",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            /*
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { /* No hace nada por ahora */ },
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
            }*/

        }
    }
}

