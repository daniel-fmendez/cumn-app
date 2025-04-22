package com.example.armoryboxkotline


import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.UUID

@Composable
fun SearchScreen(navController: NavController){
    Box (modifier = Modifier
        .fillMaxSize(),
    ){
        SearchBar(navController)
    }
}
data class CardItem (
    val name: String,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchBar(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var searchTriggered by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val items = listOf(
        Card(UUID.randomUUID().toString(), "Command and Conquer", "Attack Action", 1, 2),
        Card(UUID.randomUUID().toString(), "Art of War", "Action", 1, 1),
        Card(UUID.randomUUID().toString(), "Enlightened Strike", "Attack Action", 1, 2),
        Card(UUID.randomUUID().toString(), "Tome of Fyendal", "Action", 2, 1),
        Card(UUID.randomUUID().toString(), "Bloodrush Bellow", "Brute Action", 1, 2),
        Card(UUID.randomUUID().toString(), "Channel Lake Frigid", "Ice Action", 2, 2),
        Card(UUID.randomUUID().toString(), "Spinal Crush", "Attack Action", 3, 2),
        Card(UUID.randomUUID().toString(), "Red in the Ledger", "Arrow Attack", 1, 2),
        Card(UUID.randomUUID().toString(), "Phantasmaclasm", "Attack Action", 3, 1),
        Card(UUID.randomUUID().toString(), "Cranial Crush", "Attack Action", 2, 1),
        Card(UUID.randomUUID().toString(), "Remorseless", "Arrow Attack", 1, 2),
        Card(UUID.randomUUID().toString(), "Soul Reaping", "Runeblade Attack", 2, 1),
        Card(UUID.randomUUID().toString(), "Ravenous Rabble", "Attack Action", 1, 3),
        Card(UUID.randomUUID().toString(), "Sigil of Solace", "Defense Reaction", 1, 3),
        Card(UUID.randomUUID().toString(), "Pummel", "Attack Reaction", 2, 3),
        Card(UUID.randomUUID().toString(), "Scar for a Scar", "Attack Action", 1, 3),
        Card(UUID.randomUUID().toString(), "Flick Knives", "Attack Reaction", 1, 1),
        Card(UUID.randomUUID().toString(), "Death Touch", "Attack Action", 1, 2),
        Card(UUID.randomUUID().toString(), "Reckless Swing", "Defense Reaction", 0, 2),
        Card(UUID.randomUUID().toString(), "Steelblade Supremacy", "Warrior Action", 1, 1),
        Card(UUID.randomUUID().toString(), "Gorganian Tome", "Action", 0, 1)
    )

    // Filtrar cuando se haya activado búsqueda Y considerar searchQuery
    val filteredItems = remember(searchTriggered, searchQuery) {
        if (searchTriggered) {
            items.filter { it.contains(searchQuery, ignoreCase = true) }
        } else emptyList()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            focusManager.clearFocus()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            color = MaterialTheme.colorScheme.surface,
        ) {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Resetear el estado de búsqueda cuando cambia el texto
                    searchTriggered = false
                },
                placeholder = { Text("Buscar...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank()) {
                            searchTriggered = true
                            focusManager.clearFocus()
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.magnify),
                            contentDescription = "Buscar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            searchTriggered = false
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Borrar")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            searchTriggered = true
                            focusManager.clearFocus()
                        }
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (searchTriggered) {
            if (filteredItems.isEmpty()) {
                Text("No se encontraron resultados.", color = MaterialTheme.colorScheme.error)
            } else {
                var size = filteredItems.size
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
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

                    Spacer (modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Añade espaciado vertical
                    ) {
                        filteredItems.forEach { item ->
                            CustomCard(navController,item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomCard(navController: NavController,name: String) {
    //val parts = name.split(" ")

    Box(
        modifier = Modifier
            .width(85.dp)
            .wrapContentHeight()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Esto ayuda a centrar todo
        ) {
            // Caja con borde dorado para la imagen
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .width(75.dp)
                    .height(90.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        //Pasar la id de la carta
                        navController.navigate(Screen.Details.createRoute(name))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Arte",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            // Contenedor para el texto con altura flexible y menos espacio
            Text(
                text = name,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
            Spacer(
                modifier = Modifier.height(6.dp)
            )
        }
    }
}