package com.example.armoryboxkotline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.example.armoryboxkotline.UserManagement.AccessScreen
import com.example.armoryboxkotline.UserManagement.EditProfileScreen
import com.example.armoryboxkotline.UserManagement.ProfileScreen
import com.example.armoryboxkotline.ui.theme.ArmoryBoxKotlineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArmoryBoxKotlineTheme {
                MainScreen()

            }
        }
    }
}

/**
 * Representacion de juguete de los datos de la carta
 */

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showTopAppBar = when (currentRoute) {
        Screen.Home.rout, Screen.Collection.rout -> true
        Screen.Search.rout, Screen.Decks.rout -> false
        else -> false  // Default behavior
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(
                visible = showTopAppBar,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                TopAppBar(navController)
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        val contentPadding = if (showTopAppBar) {
            innerPadding
        } else {
            // Conserva solo el padding inferior para el BottomNavigationBar
            PaddingValues(
                bottom = innerPadding.calculateBottomPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
        }
        val graph = navController.createGraph(startDestination = Screen.Home.rout) {
            val sharedDeckViewModel: SharedDeckViewModel = viewModel()
            composable(route = Screen.Home.rout) {
                HomeScreen(navController)
            }
            composable(route = Screen.Search.rout) {
                SearchScreen(navController)
            }
            composable(route = Screen.Decks.rout) {
                DecksScreen(navController,sharedDeckViewModel)
            }
            composable(route = Screen.Collection.rout) {
                CollectionScreen()
            }
            composable(
                route = Screen.Details.rout,
                arguments = listOf(
                    navArgument("cardId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val cardId = backStackEntry.arguments?.getString("cardId")
                DetailsScreen(navController,cardId ?: "")
            }
            composable(route = Screen.Profile.rout) {
                ProfileScreen(navController)
            }
            composable(route = Screen.EditProfile.rout) {
                EditProfileScreen(navController)
            }
            composable(route = Screen.AccessScreen.rout) {
                AccessScreen(navController)
            }

            composable(
                route = Screen.DeckDetails.rout,
                arguments = listOf(
                    navArgument("deckId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val deckId = backStackEntry.arguments?.getString("deckId")
                DeckDetails(navController,sharedDeckViewModel)
            }
        }

        NavHost(
            navController = navController,
            graph = graph,
            modifier = Modifier.padding(innerPadding)
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
    //val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    TopAppBar(
        title = {
            Text(
                text = "ArmoryBox",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        actions = {
            Box (
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clickable {
                        //Profile
                        navController.navigate(Screen.Profile.rout)
                        navController.navigate(Screen.AccessScreen.rout)
                   },

                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "U",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
@Composable
fun BottomAppBar() {

}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ArmoryBoxKotlineTheme {
        Greeting("Android")
    }
}