package com.von.pokedex2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.von.pokedex2.ui.theme.Dimens
import com.von.pokedex2.viewmodel.PokemonViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onPokemonClick: (String) -> Unit,
    viewModel: PokemonViewModel = koinViewModel()
)
 {
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.spacingMedium)
    ) {
        IconToggleButton(
            checked = isDarkTheme,
            onCheckedChange = { onToggleTheme() },
            modifier = Modifier
                .align(Alignment.End)
                .offset(y = 50.dp)

        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = if (isDarkTheme) "Dark Mode" else "Light Mode"
            )
        }

        Header()

        Column(
            modifier = Modifier
                .offset(y = (-125).dp)
        ) {
            TypeFilterGrid()

            Spacer(modifier = Modifier.height(Dimens.spacingMedium))

            SearchBar(
                query = searchQuery,
                onQueryChanged = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacingSmall)
            )

            Spacer(modifier = Modifier.height(Dimens.spacingMedium))

            PokemonList(
                viewModel = viewModel,
                onPokemonClick = onPokemonClick
            )
        }
    }
}
