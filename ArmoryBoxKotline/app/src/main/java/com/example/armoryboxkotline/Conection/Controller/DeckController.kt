package com.example.armoryboxkotline.Conection.Controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armoryboxkotline.Conection.Config
import com.example.armoryboxkotline.Conection.HttpClientSingleton
import com.example.armoryboxkotline.Conection.SessionManager
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class DecksViewModel : ViewModel() {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks = _decks.asStateFlow()

    private val _deckCards = MutableStateFlow<List<DeckCard>>(emptyList())
    val deckCards = _deckCards.asStateFlow()

    fun userDecks(userId: Int) {
        viewModelScope.launch {
            try {
                val decksList = getUserDecks(userId) // la función que ya tienes
                _decks.value = decksList
            } catch (e: Exception) {
                Log.e("AccessViewModel", "Error al cargar mazos", e)
                _decks.value = emptyList()
            }
        }
    }

    fun removeDeck(deckId: Int) {
        viewModelScope.launch {
            try {
                val removedDeck = removeDeckById(deckId)
                if (removedDeck != null) {
                    // Actualiza la lista de mazos eliminando el mazo borrado
                    _decks.value = _decks.value.filter { it.id != deckId }
                }
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Error al eliminar mazo", e)
            }
        }
    }

    fun createDeck(name: String, userId: Int, heroId: String) {
        viewModelScope.launch {
            try {
                val newDeck = addDeck(name, userId, heroId)
                if (newDeck != null) {
                    // Añade el nuevo mazo a la lista existente
                    _decks.value = _decks.value + newDeck
                }
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Error al crear mazo", e)
            }
        }
    }

    fun loadDeckCards(deckId: Int) {
        viewModelScope.launch {
            try {
                val cards = getDeckCards(deckId)
                _deckCards.value = cards
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Error al cargar cartas del mazo", e)
                _deckCards.value = emptyList()
            }
        }
    }

    fun updateCardInDeck(deckId: Int, cardId: String, quantity: Int) {
        viewModelScope.launch {
            try {
                val updatedCard = updateDeckCard(deckId, cardId, quantity)
                if (updatedCard != null) {
                    // Actualiza la lista de cartas en el mazo
                    val currentCards = _deckCards.value.toMutableList()
                    val existingCardIndex = currentCards.indexOfFirst { it.cardId == cardId && it.deckId == deckId }

                    if(quantity>0){
                        if (existingCardIndex >= 0) {
                            currentCards[existingCardIndex] = updatedCard
                        } else {
                            currentCards.add(updatedCard)
                        }
                    }else{
                        if (existingCardIndex >= 0) {
                            currentCards.removeAt(existingCardIndex)
                        }
                    }

                    _deckCards.value = currentCards
                }
            } catch (e: Exception) {
                Log.e("DecksViewModel", "Error al actualizar carta en mazo", e)
            }
        }
    }
}


@Serializable
data class Deck(
    val id: Int,
    val name: String,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("hero_id")
    val heroId: String,
    val type: String
)

suspend fun getUserDecks(userId: Int): List<Deck> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/decks/$userId") {
        contentType(ContentType.Application.Json)
    }

    if (response.status.value == 200) {
        val decks = response.body<List<Deck>>()
        return decks
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}

suspend fun removeDeckById(id: Int): Deck? {
    val response = HttpClientSingleton.client.post("${Config.BASE_URL}/delete_deck/") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "id" to id
            )
        )
    }

    if(response.status.value == 200){
        val deck = response.body<Deck>()
        return deck
    } else {
        return  null
    }
}

suspend fun addDeck(name: String, userId: Int, heroId: String): Deck? {
    val response = HttpClientSingleton.client.post("${Config.BASE_URL}/add_deck/") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "name" to name,
                "user_id" to userId,
                "hero_id" to heroId
            )
        )
    }

    if (response.status.value == 201) {
        val deck = response.body<Deck>()
        return deck
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return null
    }
}

@Serializable
data class DeckCard(
    @SerialName("deck_id")
    val deckId: Int,
    @SerialName("card_id")
    val cardId: String,
    var quantity: Int,
)

suspend fun updateDeckCard(deckId: Int, cardId: String, quantity: Int): DeckCard? {
    val response = HttpClientSingleton.client.post("${Config.BASE_URL}/update_deck_card/") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "deck_id" to deckId,
                "card_id" to cardId,
                "quantity" to quantity
            )
        )
    }

    if(response.status.value == 201 || response.status.value == 200){
        val deckCard = response.body<DeckCard>()
        return deckCard
    }else {
        return null
    }
}

suspend fun getDeckCards(deckId: Int): List<DeckCard>{
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/deck_cards/$deckId") {
        contentType(ContentType.Application.Json)
    }

    if (response.status.value == 200) {
        val deck_cards = response.body<List<DeckCard>>()
        return deck_cards
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}