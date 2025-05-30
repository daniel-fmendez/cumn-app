package com.example.armoryboxkotline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.armoryboxkotline.Collection.CollectionScreen
import com.example.armoryboxkotline.Conection.Controller.CollectionViewModel
import com.example.armoryboxkotline.Conection.SessionManager
import com.example.armoryboxkotline.Decks.CreateDeck
import com.example.armoryboxkotline.Decks.DeckDetails
import com.example.armoryboxkotline.Decks.DecksScreen
import com.example.armoryboxkotline.Decks.SharedDeckViewModel
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

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showTopAppBar = when (currentRoute) {
        Screen.Home.rout, Screen.Collection.rout -> true
        Screen.Search.rout, Screen.Decks.rout -> false
        else -> false
    }

    val sharedDeckViewModel: SharedDeckViewModel = viewModel()
    val collectionViewModel: CollectionViewModel = viewModel()

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
            PaddingValues(
                bottom = innerPadding.calculateBottomPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            )
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Home.rout,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = Screen.Home.rout) {
                HomeScreen(navController)
            }
            composable(route = Screen.Search.rout) {
                SearchScreen(navController)
            }
            composable(route = Screen.Decks.rout) {
                val id = SessionManager.userId ?: -1
                if (id != -1) {
                    DecksScreen(navController, sharedDeckViewModel)
                } else {
                    EmpryScreen("Inicia sesión para ver tus mazos")
                }
            }
            composable(route = Screen.Collection.rout) {
                val id = SessionManager.userId ?: -1
                if (id != -1) {
                    CollectionScreen(navController)
                } else {
                    EmpryScreen("Inicia sesión para ver tu colección")
                }
            }
            composable(
                route = Screen.Details.rout,
                arguments = listOf(navArgument("cardId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cardId = backStackEntry.arguments?.getString("cardId")
                DetailsScreen(navController, cardId ?: "")
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
            composable(route = Screen.DeckDetails.rout) {
                DeckDetails(navController, sharedDeckViewModel)
            }
            composable(route = Screen.CreateDeck.rout) {
                CreateDeck(navController)
            }
            composable(route = Screen.Scanner.rout) {
                ScannerScreen() // Asegúrate de que esté definido en otro archivo.
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController) {
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
            Box(
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
                        if (SessionManager.userId != null) {
                            navController.navigate(Screen.Profile.rout)
                        } else {
                            navController.navigate(Screen.AccessScreen.rout)
                        }
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
    // Deja en blanco o implementa tu barra inferior
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
