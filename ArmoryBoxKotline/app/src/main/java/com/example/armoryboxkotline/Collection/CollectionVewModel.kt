package com.example.armoryboxkotline.Collection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
/*
data class CollectionCard(
    val id: String,
    val name: String,
    val type: String,
    val quantity: Int
) {
    fun matches(query: String): Boolean =
        name.contains(query, ignoreCase = true)
}

class CollectionViewModel : ViewModel() {
    private val _cards = MutableStateFlow<List<CollectionCard>>(emptyList())
    val cards: StateFlow<List<CollectionCard>> = _cards.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredCards = MutableStateFlow<List<CollectionCard>>(emptyList())
    val filteredCards: StateFlow<List<CollectionCard>> = _filteredCards.asStateFlow()

    init {
        if (_cards.value.isEmpty()) {
            _cards.value = listOf(
                CollectionCard("1", "Lava Burst", "Attack Action", 0),
                CollectionCard("2", "Blaze Headlong", "Attack Action", 0),
                CollectionCard("3", "Cranial Crush", "Attack Action", 0),
                CollectionCard("4", "Frosting", "Wizard Action", 0),
            )
            updateFilteredCards()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredCards()
    }

    private fun updateFilteredCards() {
        val q = _searchQuery.value.trim()
        val filtered = if (q.isEmpty()) _cards.value
        else _cards.value.filter { it.matches(q) }
        _filteredCards.value = filtered
    }

    fun incrementCardQuantity(cardId: String) {
        _cards.update { list ->
            list.map {
                if (it.id == cardId) it.copy(quantity = it.quantity + 1) else it
            }
        }
        updateFilteredCards()
    }

    fun decrementCardQuantity(cardId: String) {
        _cards.update { list ->
            list.map {
                if (it.id == cardId && it.quantity > 0) it.copy(quantity = it.quantity - 1) else it
            }
        }
        updateFilteredCards()
    }
}
*/