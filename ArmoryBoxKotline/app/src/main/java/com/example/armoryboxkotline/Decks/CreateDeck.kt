package com.example.armoryboxkotline.Decks

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.armoryboxkotline.Conection.Controller.CardRoot
import com.example.armoryboxkotline.Conection.Controller.CardsViewModel
import com.example.armoryboxkotline.Conection.Controller.DeckCard
import com.example.armoryboxkotline.Conection.Controller.DecksViewModel
import com.example.armoryboxkotline.Conection.SessionManager
import com.example.armoryboxkotline.FakeTopBar
import com.example.armoryboxkotline.R
import com.example.armoryboxkotline.Screen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateDeck(navController: NavController){
    val decksViewModel = viewModel<DecksViewModel>()

    val cardsViewModel = viewModel<CardsViewModel>()
    val cards by cardsViewModel.cards.collectAsState(initial = emptyList())
    val cardImages by cardsViewModel.cardImages.collectAsState()


    var deckName by remember { mutableStateOf("") }
    var selectedDeckType by remember { mutableStateOf(DeckType.Blitz) }

    var deckCards by remember { mutableStateOf<List<DeckCardPair>>(emptyList()) }

    val totalCards = deckCards.sumOf { it.deckCard.quantity }
    val maxCards = selectedDeckType.cardLimit


    //Search
    var searchQuery by remember { mutableStateOf("") }
    var searchTriggered by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    //Seleccion de heroe
    var showHeroSelection by remember { mutableStateOf(false) }
    var heroId by remember { mutableStateOf("") }

    fun updateCardQuantity(cardRoot: CardRoot, newQuantity: Int) {
        val newDeckCard = DeckCard(
            deckId = 0,
            cardId = cardRoot.uniqueId,
            quantity = newQuantity
        )

        if (newQuantity <= 0) {
            // Remove the card from the deck
            deckCards = deckCards.filter { it.deckCard.cardId != newDeckCard.cardId }
        } else {
            // Find if the card is already in the deck
            val existingCardIndex =
                deckCards.indexOfFirst { it.deckCard.cardId == newDeckCard.cardId }

            if (existingCardIndex != -1) {
                // Update the existing card quantity
                Log.d("DeckUpdate", "Carta ya en mazo. Actualizando cantidad.")
                deckCards = deckCards.toMutableList().apply {
                    this[existingCardIndex] = DeckCardPair(
                        deckCard = this[existingCardIndex].deckCard.copy(quantity = newQuantity),
                        cardRoot = this[existingCardIndex].cardRoot
                    )
                }
            } else {
                // Add new card to the deck
                Log.d("DeckUpdate", "Carta nueva. Añadiendo con cantidad: $newQuantity")
                deckCards = deckCards + DeckCardPair(
                    deckCard = newDeckCard,
                    cardRoot = cardRoot
                )
            }
        }
    }

    fun canAddMoreCards(): Boolean {
        return totalCards < maxCards
    }
    val filteredItems = remember(searchTriggered, cards) {
        if (searchTriggered) cards else emptyList()
    }

    if(showHeroSelection){
        SelectHeroScreen(
            onSelect = { selection ->
                showHeroSelection = false
                heroId = selection
            },
            onDismiss = {
                showHeroSelection = false
            }
        )
    }else{
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar siempre fijo en la parte superior
            FakeTopBar(navController, "Crear Mazo")

            // Contenido principal con scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Esto ocupa el espacio disponible entre la TopBar y los botones
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Sección de nombre del mazo
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Column {
                                Text(
                                    text = "Nombre del mazo",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(32.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    BasicTextField(
                                        value = deckName,
                                        onValueChange = { deckName = it },
                                        textStyle = MaterialTheme.typography.titleSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                    ) { innerTextField ->
                                        Box {
                                            // Placeholder que se muestra solo cuando el campo está vacío
                                            if (deckName.isEmpty()) {
                                                Text(
                                                    text = "Nombre",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                            }
                                            // Campo de texto real
                                            innerTextField()
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Formato",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                DeckTypeSelector(
                                    selectedType = selectedDeckType,
                                    onTypeSelected = { newType ->
                                        selectedDeckType = newType
                                        // Aquí puedes realizar cualquier otra acción que necesites cuando cambie el tipo
                                    }
                                )
                            }
                        }
                    }

                    // Espaciador
                    item { Spacer(Modifier.height(16.dp)) }

                    // Sección de héroe
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ){
                            Column {
                                Text(
                                    text = "Héroe",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    //modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                //Carta del heroe
                                if(heroId.isEmpty()){
                                    SelectHeroCard(
                                        onSelect = {
                                            showHeroSelection = true
                                            Log.d("SelectHero", "Click")
                                        }
                                    )
                                }else{
                                    HeroCard(heroId, onDelete = {
                                        heroId = "";
                                    }, cardsViewModel)
                                }
                            }
                        }
                    }

                    // Espaciador
                    item { Spacer(Modifier.height(16.dp)) }

                    // Estadísticas del mazo
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp),
                            contentAlignment = Alignment.CenterStart,
                        ){
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Estadísticas del mazo",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(4f),
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        text = "$totalCards/$maxCards",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.End
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                var progress = totalCards.toFloat()/maxCards.toFloat()
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }

                    // Espaciador
                    item { Spacer(Modifier.height(16.dp)) }

                    // Buscar cartas
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ){
                            val interactionSource = remember { MutableInteractionSource() }
                            val isFocused by interactionSource.collectIsFocusedAsState()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(32.dp)
                                    ),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                // Icono de búsqueda al inicio
                                IconButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            cardsViewModel.searchCard(searchQuery)
                                            searchTriggered = true
                                            focusManager.clearFocus()
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.magnify),
                                        contentDescription = "Buscar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                // Área de texto
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = {
                                        searchQuery = it
                                        searchTriggered = false
                                    },
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                    singleLine = true,
                                    interactionSource = interactionSource,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                    keyboardActions = KeyboardActions(
                                        onSearch = {
                                            if (searchQuery.isNotBlank()) {
                                                cardsViewModel.searchCard(searchQuery)
                                                searchTriggered = true
                                                focusManager.clearFocus()
                                            }
                                        }
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 48.dp, end = 48.dp)
                                ) { innerTextField ->
                                    Box(
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchQuery.isEmpty()) {
                                            Text(
                                                text = "Buscar...",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        innerTextField()
                                    }
                                }

                                // Icono para borrar al final (solo visible cuando hay texto)
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            searchQuery = ""
                                            searchTriggered = false
                                            focusManager.clearFocus()
                                        },
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Borrar",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Espaciador
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    if (!searchTriggered && deckCards.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                fun getQuantity(cards: List<DeckCardPair>): Int {
                                    var quantity = 0
                                    for (card in cards) {
                                        quantity += card.deckCard.quantity
                                    }
                                    return quantity
                                }
                                Text(
                                    text = "Cartas en el mazo (${getQuantity(deckCards)})",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Left
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        item {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                deckCards.forEach { deckCardPair  ->
                                    val maxAllowed = maxCards - (totalCards - deckCardPair.deckCard.quantity)

                                    CardInDeck (
                                        deckCardPair = deckCardPair,
                                        maxAllowed = maxAllowed,
                                        onQuantityChanged = { card, newQuantity ->
                                            updateCardQuantity(card, newQuantity)
                                        },
                                        cardsViewModel
                                    )
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }

                    // Resultados de búsqueda
                    if (searchTriggered) {
                        item {
                            if (filteredItems.isEmpty()) {
                                Text("No se encontraron resultados.", color = MaterialTheme.colorScheme.error)
                            } else {
                                val size = filteredItems.size

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
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

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        // Si hay resultados, mostrar las tarjetas de manera individual
                        if (filteredItems.isNotEmpty()) {
                            item {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    filteredItems.forEach { card ->
                                        // Buscar si la carta ya está en el mazo para obtener su cantidad
                                        val existingPair = deckCards.find { it.deckCard.cardId == card.uniqueId }

                                        if (existingPair != null) {
                                            // Card is already in the deck, use existing pair
                                            val maxAllowed = maxCards - (totalCards - existingPair.deckCard.quantity)

                                            CardInDeck(
                                                deckCardPair = existingPair,
                                                maxAllowed = maxAllowed,
                                                onQuantityChanged = { c, newQuantity ->
                                                    updateCardQuantity(c, newQuantity)
                                                },
                                                cardsViewModel
                                            )
                                        } else {
                                            // Card is not in the deck, create a temporary DeckCardPair
                                            val tempDeckCard = DeckCard(
                                                deckId = 0,
                                                cardId = card.uniqueId,
                                                quantity = 0
                                            )

                                            val tempPair = DeckCardPair(
                                                deckCard = tempDeckCard,
                                                cardRoot = card
                                            )

                                            CardInDeck(
                                                deckCardPair = tempPair,
                                                maxAllowed = maxCards - totalCards,
                                                onQuantityChanged = { c, newQuantity ->
                                                    updateCardQuantity(c, newQuantity)
                                                },
                                                cardsViewModel
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botones fijos en la parte inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botón Cancelar
                    Button(
                        onClick = { /* Acción cancelar */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    // Botón Guardar
                    Button(
                        onClick = {
                            if(deckName.isNotEmpty() && heroId.isNotEmpty()){
                                decksViewModel.createDeckWithCards(deckName, SessionManager.userId!!,heroId, selectedDeckType.toString(),deckCards)

                                navController.popBackStack()
                            }else {
                                Log.d("DeckCreate", "No se creo")
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun SelectHeroCard(onSelect: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        IconButton(
            onClick = {
                onSelect()
            },
            modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
