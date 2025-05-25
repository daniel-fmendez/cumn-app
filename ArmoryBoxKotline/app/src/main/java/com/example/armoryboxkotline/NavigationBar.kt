package com.example.armoryboxkotline

import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost


data class NavigationItem (
    val title: String,
    val icon: Int,
    val selectedIcon: Int,
    val route: String
)
sealed class Screen(val rout: String) {
    object Home: Screen("home_screen")
    object Search: Screen("search_screen")
    object Collection: Screen("collection_screen")
    object Decks: Screen("decks_screen")
    object Scanner: Screen("scanner_screen")
    object Details : Screen("details/{cardId}") {
        fun createRoute(cardId: String) = "details/$cardId"
    }
    object Profile : Screen("profile")
    object EditProfile : Screen("editProfile")
    object AccessScreen : Screen("accessScreen")
    object DeckDetails : Screen("decks/details/{deckId}")
    object CreateDeck : Screen("decks/create")
}
val navigationItems = listOf(
    NavigationItem(
        title = "Inicio",
        icon = R.drawable.home_outline,
        selectedIcon = R.drawable.home,
        route = Screen.Home.rout
    ),
    NavigationItem(
        title = "Buscar",
        icon = R.drawable.magnify,
        selectedIcon = R.drawable.magnify,
        route = Screen.Search.rout
    ),
    NavigationItem(
        title = "Colección",
        icon = R.drawable.cards_outline,
        selectedIcon =  R.drawable.cards,
        route = Screen.Collection.rout
    ),
    NavigationItem(
        title = "Mazos",
        icon = R.drawable.archive_outline,
        selectedIcon =  R.drawable.archive,
        route = Screen.Decks.rout
    ),
    NavigationItem(
        title = "Escaner",
        icon = R.drawable.outline_camera_alt_24,
        selectedIcon =  R.drawable.baseline_camera_alt_24,
        route = Screen.Scanner.rout
    )
)

@Composable
fun BottomNavigationBar (navController: NavController) {
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    var source = if(index == selectedNavigationIndex.intValue)
                        item.selectedIcon
                    else item.icon
                    Icon(painter = painterResource(id = source), contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color = if (index == selectedNavigationIndex.intValue)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )

            )
        }
    }
}