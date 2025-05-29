package com.example.armoryboxkotline.Collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel // ahora se recibe el ViewModel desde fuera
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cards by viewModel.filteredCards.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi Colección",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::setSearchQuery,
            label = { Text("Buscar cartas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (cards.isEmpty()) {
                Text("No hay cartas que coincidan.", modifier = Modifier.padding(8.dp))
            } else {
                cards.forEach { card ->
                    CardItem(
                        card = card,
                        onIncrement = { viewModel.incrementCardQuantity(card.id) },
                        onDecrement = { viewModel.decrementCardQuantity(card.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: CollectionCard,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(card.name, style = MaterialTheme.typography.titleMedium)
                Text(card.type, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }

            IconButton(onClick = onDecrement, enabled = card.quantity > 0) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Quitar")
            }
            Text("${card.quantity}", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onIncrement) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    }
}
