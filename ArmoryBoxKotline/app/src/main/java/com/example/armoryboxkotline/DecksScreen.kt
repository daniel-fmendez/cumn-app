package com.example.armoryboxkotline

import android.os.Parcelable
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import com.example.armoryboxkotline.Conection.Controller.Deck
import com.example.armoryboxkotline.Conection.Controller.DeckCard
import com.example.armoryboxkotline.Conection.Controller.DecksViewModel
import com.example.armoryboxkotline.Conection.SessionManager
import com.example.armoryboxkotline.UserManagement.AccessScreen
import com.example.armoryboxkotline.ui.theme.ArmoryBoxKotlineTheme
import kotlinx.parcelize.Parcelize
import java.util.UUID

/*
@Parcelize
data class Card(
    val id: String,
    val name: String,
    val type: String,
    val pitch: Int = 1,
    var quantity: Int = 0
) : Parcelable {
    fun contains(searchQuery: String, ignoreCase: Boolean): Boolean {
        return name.contains(searchQuery,ignoreCase) ;
    }
}*/

/*@Parcelize
data class Deck(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: DeckType,
    val cards: List<Card>
) : Parcelable*/

enum class DeckType (val cardLimit: Int){
    Blitz (40),
    Classic (80);

    companion object {
        fun fromString(name: String): DeckType {
            return values().firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: Classic
        }
    }
}

class SharedDeckViewModel : ViewModel() {
    var selectedDeck by mutableStateOf<Deck?>(null)
        private set

    var decks by mutableStateOf<List<Deck>>(emptyList())
        private set

    fun selectDeck(deck: Deck?) {
        selectedDeck = deck
    }

    fun updateDeck(updatedDeck: Deck) {
        // actualizarlo
        decks = if (decks.any { it.id == updatedDeck.id }) {
            decks.map { if (it.id == updatedDeck.id) updatedDeck else it }
        } else {
            //añadirlo a la lista
            decks + updatedDeck
        }

        // Actualizar el mazo seleccionado
        if (selectedDeck?.id == updatedDeck.id) {
            selectedDeck = updatedDeck
        }
    }
}

@Composable
fun DecksScreen(navController: NavController, sharedDeckViewModel: SharedDeckViewModel){
    val decksViewModel = viewModel<DecksViewModel>()
    val decks by decksViewModel.decks.collectAsState(initial = emptyList())

    LaunchedEffect(decksViewModel) {
        decksViewModel.userDecks(SessionManager.userId!!)
    }
    /*
    val decks = listOf(
        Deck(
            name = "Fai Blitz",
            type = DeckType.Blitz,
            cards = listOf(
                Card(UUID.randomUUID().toString(), "Lava Burst", "Attack Action", 1, 3),
                Card(UUID.randomUUID().toString(), "Blaze Headlong", "Attack Action", 1, 3)
            )
        ),
        Deck(
            name = "Bravo Classic",
            type = DeckType.Classic,
            cards = listOf(
                Card(UUID.randomUUID().toString(), "Cranial Crush", "Attack Action", 2, 2),
                Card(UUID.randomUUID().toString(), "Spinal Crush", "Attack Action", 3, 2),
                Card(UUID.randomUUID().toString(), "Show Time!", "Action", 1, 1)
            )
        ),
        Deck(
            name = "Iyslander Blitz",
            type = DeckType.Blitz,
            cards = listOf(
                Card(UUID.randomUUID().toString(), "Aether Icevein", "Wizard Action", 2, 3),
                Card(UUID.randomUUID().toString(), "Frosting", "Wizard Action", 1, 3)
            )
        )
    )*/

    Box (modifier = Modifier
        .fillMaxSize(),
    ){
        Column {
            DeckTopbar(navController)
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                decks.forEach{ item ->
                    DeckCard(navController, sharedDeckViewModel,item)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DeckTopbar(navController: NavController){
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
                    .weight(4f) // Este espacio es el más grande
                    .fillMaxHeight()
                    .padding(horizontal = 32.dp)
                //.background(Color.Blue)
            ) {
                Text(
                    text = "Tus Mazos",
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier = Modifier
                    .weight(2f)
                //.background(Color.Red)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.CreateDeck.rout)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(64.dp),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "+ Crear",
                    )
                }
            }
        }
    }
}

@Composable
fun DeckCard(navController: NavController, sharedDeckViewModel: SharedDeckViewModel, deck: Deck){
    val decksViewModel = viewModel<DecksViewModel>()
    val deckCards by decksViewModel.deckCards.collectAsState(initial = emptyList())

    LaunchedEffect(decksViewModel) {
        decksViewModel.loadDeckCards(deck.id)
    }

    fun getQuantity(cards: List<DeckCard>): Int {
        var quantity = 0
        for (card in cards) {
            quantity += card.quantity
        }
        return quantity
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.Top),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                        .width(75.dp)
                        .height(100.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Arte Heroe",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.Top // Cambiado a Top
                ) {
                    // Información superior
                    Column {
                        Text(
                            text = deck.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = deck.type,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    // Espaciador flexible que empuja el contenido restante hacia abajo
                    Spacer(modifier = Modifier.height(18.dp))
                    // Barra de progreso en la parte inferior
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        val deckType = DeckType.fromString(deck.type)
                        val cards = getQuantity(deckCards)
                        val maxCards = deckType.cardLimit
                        val progress = cards.toFloat() / maxCards.toFloat()

                        Text(
                            text = "$cards/$maxCards",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterVertically)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        sharedDeckViewModel.selectDeck(deck)
                        navController.navigate(Screen.DeckDetails.rout)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun PreviewDecks(){
    val navController = rememberNavController()
    val sharedDeckViewModel: SharedDeckViewModel = viewModel()
    ArmoryBoxKotlineTheme () {
        DecksScreen(navController,sharedDeckViewModel)
    }
}