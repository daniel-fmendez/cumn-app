package com.example.armoryboxkotline.Conection.Controller

import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.armoryboxkotline.Conection.Config
import com.example.armoryboxkotline.Conection.HttpClientSingleton
import com.example.armoryboxkotline.Conection.SessionManager
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.MessageDigest

class AccessViewModel : ViewModel() {

    // Estado para saber si el login fue exitoso o no
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    // Función para hacer login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val success = doLogin(email, password) // Llamas a tu función suspendida aquí
            _loginResult.value = success
        }
    }

    fun register(nickname: String, email: String, password: String) {
        viewModelScope.launch {
            val success = doRegister(nickname,email, password) // Llamas a tu función suspendida aquí
            _loginResult.value = success
        }
    }
}

class CollectionViewModel : ViewModel() {

    private val _cardCollection = MutableStateFlow<List<CardCollection>>(emptyList())
    val collection = _cardCollection.asStateFlow()

    fun userCollection(userId: Int) {
        viewModelScope.launch {
            try {
                val collectionList = getCollection(userId) // la función que ya tienes
                _cardCollection.value = collectionList
            } catch (e: Exception) {
                Log.e("AccessViewModel", "Error al cargar mazos", e)
                _cardCollection.value = emptyList()
            }
        }
    }
}


@Serializable
data class LoginResponse(
    val id: Int,
    val nickname: String
)

suspend fun doLogin(email: String, password: String): Boolean {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    val preHashedPassword = bytes.joinToString("") { "%02x".format(it) }

    val response = HttpClientSingleton.client.post("${Config.BASE_URL}/login") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "email" to email,
                "password" to preHashedPassword
            )
        )
    }
    Log.e("LoginHTTP", response.toString())
    if (response.status.value == 200) {

        val loginResponse = response.body<LoginResponse>()

        SessionManager.userId = loginResponse.id
        SessionManager.nickname = loginResponse.nickname
        return true
    } else {
        return false
    }
}

@Serializable
data class RegisterResponse(
    val id: Int,
    val nickname: String,
    val email: String,
    @SerialName("password_hash") val passwordHash: String
)

suspend fun doRegister(nickname: String, email: String, password: String): Boolean {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    val preHashedPassword = bytes.joinToString("") { "%02x".format(it) }

    val response = HttpClientSingleton.client.post("${Config.BASE_URL}/add_user") {
        contentType(ContentType.Application.Json)
        setBody(
            mapOf(
                "nickname" to nickname,
                "email" to email,
                "password" to preHashedPassword
            )
        )
    }
    Log.e("RegisterHTTP", "Waiting Response")
    Log.e("RegisterHTTP", response.toString())
    if (response.status.value == 201) {
        val registerResponse = response.body<RegisterResponse>()
        Log.e("RegisterHTTP", "Response accepted")
        SessionManager.userId = registerResponse.id
        SessionManager.nickname = registerResponse.nickname
        return true
    } else {
        return false
    }
}

@Serializable
data class CardCollection(
    val user_id: Int,
    val card_id: String,
    val quantity: Int
)

suspend fun getCollection(userId: Int): List<CardCollection> {
    val response = HttpClientSingleton.client.get("${Config.BASE_URL}/user_collection/$userId") {
        contentType(ContentType.Application.Json)
    }

    if (response.status.value == 200) {
        val collection = response.body<List<CardCollection>>() // <-- Aquí decimos que espere un array de Deck
        return collection
    } else {
        Log.e("HTTP", "Error al obtener los mazos: ${response.status}")
        return emptyList()
    }
}