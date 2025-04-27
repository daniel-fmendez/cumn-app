package com.example.armoryboxkotline.Conection.Controller

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armoryboxkotline.Conection.Config
import com.example.armoryboxkotline.Conection.HttpClientSingleton
import com.example.armoryboxkotline.Conection.SessionManager
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.security.MessageDigest


class DecksViewModel : ViewModel() {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks = _decks.asStateFlow()

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
}


@Serializable
data class Deck(
    val id: Int,
    val name: String,
    val user_id: Int,
    val hero_id: String
)

suspend fun getUserDecks(userId: Int): List<Deck> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/decks/$userId") {
        contentType(ContentType.Application.Json)
    }

    if (response.status.value == 200) {
        val decks = response.body<List<Deck>>() // <-- Aquí decimos que espere un array de Deck
        return decks
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}
