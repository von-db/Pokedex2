package com.von.pokedex2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.rememberAsyncImagePainter
import com.von.pokedex2.viewmodel.PokemonViewModel
import com.von.pokedex2.data.remote.responses.Pokemon
import org.koin.androidx.compose.koinViewModel

@Composable
fun PokemonDetailOverlay(
    pokemonName: String,
    viewModel: PokemonViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    var dominantColor by remember { mutableStateOf(Color.Transparent) }
    var isLoading by remember { mutableStateOf(true) }
    val defaultColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(pokemonName) {
        pokemon = viewModel.getPokemonByName(pokemonName)

        val entry = viewModel.getPokemonEntryByName(pokemonName, context)
        dominantColor = runCatching {
            Color(entry.dominantColorHex.toColorInt())
        }.getOrDefault(defaultColor)

        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
                .clickable(enabled = false) {}
        ) {
            if (isLoading || pokemon == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray
                            )
                        }
                    }

                    Image(
                        painter = rememberAsyncImagePainter(model = pokemon!!.sprites.front_default),
                        contentDescription = pokemon!!.name,
                        modifier = Modifier
                            .size(180.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = pokemon!!.name.replaceFirstChar { it.uppercaseChar() },
                        fontSize = 20.sp,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StatBar("HP", pokemon!!.stats.find { it.stat.name == "hp" }?.base_stat ?: 0, color = dominantColor)
                    StatBar("Attack", pokemon!!.stats.find { it.stat.name == "attack" }?.base_stat ?: 0, color = dominantColor)
                    StatBar("Defense", pokemon!!.stats.find { it.stat.name == "defense" }?.base_stat ?: 0, color = dominantColor)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Types: ${pokemon!!.types.joinToString { it.type.name }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StatBar(statName: String, value: Int, maxValue: Int = 200, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = "$statName: $value",
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = MaterialTheme.typography.bodySmall.fontWeight,
            color = color
        )

        LinearProgressIndicator(
            progress = { value / maxValue.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
