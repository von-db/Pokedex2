package com.von.pokedex2.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.von.pokedex2.ui.HomeScreen
import com.von.pokedex2.ui.PokemonDetailOverlay

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            var selectedPokemon by remember { mutableStateOf<String?>(null) }

            Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                HomeScreen(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    onPokemonClick = { selectedPokemon = it }
                )


                selectedPokemon?.let { name ->
                    PokemonDetailOverlay(
                        pokemonName = name,
                        onDismiss = { selectedPokemon = null }
                    )
                }
            }
        }
    }
}
