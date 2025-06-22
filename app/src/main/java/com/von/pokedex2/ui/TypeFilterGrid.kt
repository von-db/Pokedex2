package com.von.pokedex2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.von.pokedex2.viewmodel.PokemonViewModel
import org.koin.androidx.compose.koinViewModel
import com.von.pokedex2.R

@Composable
fun TypeFilterGrid(viewModel: PokemonViewModel = koinViewModel()) {
    val types = listOf("Fire", "Water", "Grass", "Electric", "Dragon", "Psychic", "Ghost", "Dark", "Steel", "Poison")
    val selectedTypes by viewModel.selectedTypes.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        types.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { type ->
                    val isSelected = type in selectedTypes
                    IconToggleButton(
                        checked = isSelected,
                        onCheckedChange = { viewModel.toggleType(type) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 4.dp,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Image(
                                painter = painterResource(id = getTypeIcon(type)), // helper function you define
                                contentDescription = type,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getTypeIcon(type: String): Int {
    return when (type.lowercase()) {
        "fire" -> R.drawable.ic_fire
        "water" -> R.drawable.ic_water
        "grass" -> R.drawable.ic_grass
        "electric" -> R.drawable.ic_electric
        "dragon" -> R.drawable.ic_dragon
        "psychic" -> R.drawable.ic_psychic
        "ghost" -> R.drawable.ic_ghost
        "dark" -> R.drawable.ic_dark
        "steel" -> R.drawable.ic_steel
        "poison" -> R.drawable.ic_poison
        else -> R.drawable.ic_poison
    }
}