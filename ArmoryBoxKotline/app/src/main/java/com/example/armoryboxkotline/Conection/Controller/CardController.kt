package com.example.armoryboxkotline.Conection.Controller

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armoryboxkotline.Conection.Config
import com.example.armoryboxkotline.Conection.HttpClientSingleton
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


class CardsViewModel : ViewModel() {

    private val _editions = MutableStateFlow<Map<String, List<CardEdition>>>(emptyMap())
    val editions = _editions.asStateFlow()

    private val _cards = MutableStateFlow<List<CardRoot>>(emptyList())
    val cards = _cards.asStateFlow()

    private val _heroes = MutableStateFlow<List<CardRoot>>(emptyList())
    val heroes = _heroes.asStateFlow()

    // Para almacenar las URLs de las imágenes asociadas a cada carta
    private val _cardImages = MutableStateFlow<Map<String, String?>>(emptyMap())
    val cardImages = _cardImages.asStateFlow()

    private val _card = MutableLiveData<CardRoot?>()
    val card: LiveData<CardRoot?> get() = _card

    fun getEditions(id: String) {
        viewModelScope.launch {
            try {
                val editionsList = searchEditions(id)

                // Guardamos todas las ediciones para usar en otros lugares
                _editions.value = _editions.value.toMutableMap().apply {
                    put(id, editionsList)
                }

                // Buscamos la primera imagen no vacía
                val firstImageUrl = editionsList
                    .firstOrNull { it.imageUrl.isNotEmpty() }
                    ?.imageUrl ?: "" // Devolverá cadena vacía si no hay imagen

                // Actualizamos el mapa de imágenes
                _cardImages.value = _cardImages.value.toMutableMap().apply {
                    put(id, firstImageUrl)
                }
            } catch (e: Exception) {
                // En caso de error, guardamos lista vacía y null para la imagen
                _editions.value = _editions.value.toMutableMap().apply {
                    put(id, emptyList())
                }
                _cardImages.value = _cardImages.value.toMutableMap().apply {
                    put(id, null)
                }
            }
        }
    }
    fun fetchHeroes(){
        viewModelScope.launch {
            try {
                val heroList = getHeroes()
                _heroes.value = heroList
            }catch (e: Exception) {
                _heroes.value = emptyList()
            }
        }
    }
    fun searchCard(name: String) {
        viewModelScope.launch {
            try {
                val cardsList = searchCardByName(name)
                _cards.value = cardsList

                // Para cada carta encontrada, buscamos sus ediciones
                cardsList.forEach { card ->
                    getEditions(card.uniqueId)
                }
            } catch (e: Exception) {
                _cards.value = emptyList()
            }
        }
    }
    fun fetchCardById(cardId: String) {
        viewModelScope.launch {
            try {
                // Llamamos a la función para obtener la carta
                val fetchedCard = getCardById(cardId)
                _card.value = fetchedCard
            } catch (e: Exception) {
                _card.value = null
            }
        }
    }
    suspend fun getCardByIdDirect(cardId: String): CardRoot? {
        return try {
            val card = getCardById(cardId)
            card
        } catch (e: Exception) {
            Log.e("CardsViewModel", "Error getting card $cardId: ${e.message}")
            null
        }
    }

    // Función para obtener ediciones de una carta específica
    fun getCardEditions(cardId: String): List<CardEdition> {
        return _editions.value[cardId] ?: emptyList()
    }

    // Función para obtener la URL de la primera imagen disponible
    suspend fun getFirstImageUrlDirect(cardId: String): String? {
        return try {
            val editionsList = searchEditions(cardId)
            editionsList
                .firstOrNull { it.imageUrl.isNotEmpty() }
                ?.imageUrl
        } catch (e: Exception) {
            Log.e("CardsViewModel", "Error getting image for card $cardId: ${e.message}")
            null
        }
    }
}

class HeroesViewModel : ViewModel() {

    private val _heroes = MutableStateFlow<List<CardRoot>>(emptyList())
    val heroes = _heroes.asStateFlow()

    private val _cardImages = MutableStateFlow<Map<String, String?>>(emptyMap())
    val cardImages = _cardImages.asStateFlow()

    private val _hero = MutableLiveData<CardRoot?>()
    val hero: LiveData<CardRoot?> get() = _hero

    fun fetchHeroes(){
        viewModelScope.launch {
            try {
                val heroList = getHeroes()
                _heroes.value = heroList

                heroList.forEach { card ->
                    getEditions(card.uniqueId)
                }
            }catch (e: Exception) {
                _heroes.value = emptyList()
            }
        }
    }

    fun getEditions(id: String) {
        viewModelScope.launch {
            try {
                val editionsList = searchEditions(id)

                // Buscamos la primera imagen no vacía
                val firstImageUrl = editionsList
                    .firstOrNull { it.imageUrl.isNotEmpty() }
                    ?.imageUrl ?: "" // Devolverá cadena vacía si no hay imagen

                // Actualizamos el mapa de imágenes
                _cardImages.value = _cardImages.value.toMutableMap().apply {
                    put(id, firstImageUrl)
                }
            } catch (e: Exception) {
                _cardImages.value = _cardImages.value.toMutableMap().apply {
                    put(id, null)
                }
            }
        }
    }
    fun fetchHeroById(heroId: String) {
        viewModelScope.launch {
            try {
                // Llamamos a la función para obtener la carta
                val fetchedCard = getCardById(heroId)
                _hero.value = fetchedCard
            } catch (e: Exception) {
                _hero.value = null
            }
        }
    }
}

@Serializable
data class CardEdition(
    @SerialName("unique_id")
    val uniqueId: String,
    @SerialName("card_unique_id")
    val cardUniqueId: String,
    @SerialName("set_printing_unique_id")
    val setPrintingUniqueId: String,
    @SerialName("card_id")
    val cardId: String,
    @SerialName("set_id")
    val setId: String,
    val edition: String,
    val foiling: String,
    val rarity: String,
    val artists: String,
    @SerialName("art_variations")
    val artVariations: String,
    @SerialName("expansion_slot")
    val expansionSlot: Boolean,
    @SerialName("flavor_text")
    val flavorText: String,
    @SerialName("flavor_text_plain")
    val flavorTextPlain: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("image_rotation_degrees")
    val imageRotationDegrees: Long,
    @SerialName("tcgplayer_product_id")
    val tcgplayerProductId: String,
    @SerialName("tcgplayer_url")
    val tcgplayerUrl: String,
)

suspend fun searchEditions(id: String): List<CardEdition> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/editions") {
        parameter("id", id)
    }

    if (response.status.value == 200) {
        val cards = response.body<List<CardEdition>>() // <-- Aquí decimos que espere un array de Deck
        return cards
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}

@Serializable
data class CardRoot(
    @SerialName("unique_id")
    val uniqueId: String,
    val name: String,
    val pitch: String,
    val cost: String,
    val power: String,
    val defense: String,
    val health: String,
    val intelligence: String,
    val arcane: String,
    val types: List<String>,
    @SerialName("card_keywords")
    val cardKeywords: List<String>,
    @SerialName("abilities_and_effects")
    val abilitiesAndEffects: List<String>,
    @SerialName("ability_and_effect_keywords")
    val abilityAndEffectKeywords: List<String>,
    @SerialName("granted_keywords")
    val grantedKeywords: List<String>,
    @SerialName("removed_keywords")
    val removedKeywords: List<String>,
    @SerialName("interacts_with_keywords")
    val interactsWithKeywords: List<String>,
    @SerialName("functional_text")
    val functionalText: String,
    @SerialName("functional_text_plain")
    val functionalTextPlain: String,
    @SerialName("type_text")
    val typeText: String,
    @SerialName("played_horizontally")
    val playedHorizontally: Boolean,
    @SerialName("blitz_legal")
    val blitzLegal: Boolean,
    @SerialName("cc_legal")
    val ccLegal: Boolean,
    @SerialName("commoner_legal")
    val commonerLegal: Boolean,
    @SerialName("ll_legal")
    val llLegal: Boolean,
    @SerialName("blitz_living_legend")
    val blitzLivingLegend: Boolean,
    @SerialName("blitz_living_legend_start")
    val blitzLivingLegendStart: String?,
    @SerialName("cc_living_legend")
    val ccLivingLegend: Boolean,
    @SerialName("cc_living_legend_start")
    val ccLivingLegendStart: String?,
    @SerialName("blitz_banned")
    val blitzBanned: Boolean,
    @SerialName("blitz_banned_start")
    val blitzBannedStart: String?,
    @SerialName("cc_banned")
    val ccBanned: Boolean,
    @SerialName("cc_banned_start")
    val ccBannedStart: String?,
    @SerialName("ll_banned")
    val llBanned: Boolean,
    @SerialName("ll_banned_start")
    val llBannedStart: String?,
    @SerialName("upf_banned")
    val upfBanned: Boolean,
    @SerialName("upf_banned_start")
    val upfBannedStart: String?,
    @SerialName("commoner_banned")
    val commonerBanned: Boolean,
    @SerialName("commoner_banned_start")
    val commonerBannedStart: String?,
    @SerialName("blitz_suspended")
    val blitzSuspended: Boolean,
    @SerialName("blitz_suspended_start")
    val blitzSuspendedStart: JsonElement?,
    @SerialName("blitz_suspended_end")
    val blitzSuspendedEnd: JsonElement?,
    @SerialName("cc_suspended")
    val ccSuspended: Boolean,
    @SerialName("cc_suspended_start")
    val ccSuspendedStart: JsonElement?,
    @SerialName("cc_suspended_end")
    val ccSuspendedEnd: JsonElement?,
    @SerialName("commoner_suspended")
    val commonerSuspended: Boolean,
    @SerialName("commoner_suspended_start")
    val commonerSuspendedStart: JsonElement?,
    @SerialName("commoner_suspended_end")
    val commonerSuspendedEnd: JsonElement?,
    @SerialName("ll_restricted")
    val llRestricted: Boolean,
    @SerialName("ll_restricted_affects_full_cycle")
    val llRestrictedAffectsFullCycle: Boolean?,
    @SerialName("ll_restricted_start")
    val llRestrictedStart: String?,
)
suspend fun getHeroes(): List<CardRoot> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/heroes") {
    }

    if (response.status.value == 200) {
        val cards = response.body<List<CardRoot>>()
        return cards
    } else {
        Log.e("HTTP", "Error al obtener los heroes: ${response.status}")
        return emptyList()
    }
}
suspend fun searchCardByName(name: String): List<CardRoot> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/cards") {
        parameter("name", name)
    }

    if (response.status.value == 200) {
        val cards = response.body<List<CardRoot>>() // <-- Aquí decimos que espere un array de Deck
        return cards
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}

suspend fun getCardById(cardId: String): CardRoot? {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/card/$cardId") {
        contentType(ContentType.Application.Json)
    }

    if (response.status.value == 200) {
        val card = response.body<CardRoot>() // <-- Aquí decimos que espere un array de Deck
        return card
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return null
    }
}