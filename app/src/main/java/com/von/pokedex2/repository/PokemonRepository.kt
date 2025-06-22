package com.von.pokedex2.repository

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.von.pokedex2.data.remote.PokeApi
import com.von.pokedex2.data.remote.responses.Pokemon
import com.von.pokedex2.data.remote.responses.PokemonListEntry
import com.von.pokedex2.data.remote.responses.PokemonListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class PokemonRepository(
    private val api: PokeApi
) {
    private var allPokemonNames: List<String>? = null

    suspend fun getPokemonList(limit: Int, offset: Int, context: Context): List<PokemonListEntry> = coroutineScope {
        val listResponse = api.getPokemonList(limit, offset)

        listResponse.results.map { item: PokemonListItem ->
            async {
                val pokemon = api.getPokemonByName(item.name)
                val spriteUrl = pokemon.sprites.front_default
                val color = getDominantColor(spriteUrl, context)
                PokemonListEntry(
                    name = pokemon.name.replaceFirstChar { it.uppercaseChar() },
                    imageUrl = spriteUrl,
                    dominantColorHex = color
                )
            }
        }.awaitAll()
    }

    suspend fun getPokemonNamesByType(type: String): List<String> {
        return try {
            val response = api.getPokemonByType(type)
            response.pokemon.map { it.pokemon.name.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getPokemonByName(name: String): Pokemon {
        return api.getPokemonByName(name)
    }

    suspend fun getAllPokemonNames(): List<String> {
        if (allPokemonNames != null) return allPokemonNames!!

        val response = api.getPokemonList(limit = 10000, offset = 0)
        val names = response.results.map { it.name }
        allPokemonNames = names
        return names
    }

    suspend fun getPokemonDetailsByName(name: String, context: Context): PokemonListEntry {
        val pokemon = api.getPokemonByName(name)
        val spriteUrl = pokemon.sprites.front_default
        val color = getDominantColor(spriteUrl, context)
        return PokemonListEntry(
            name = pokemon.name.replaceFirstChar { it.uppercaseChar() },
            imageUrl = spriteUrl,
            dominantColorHex = color
        )
    }

    suspend fun getPokemonNamesByTypes(types: List<String>): List<String> {
        if (types.isEmpty()) return emptyList()
        val typeNameLists = types.map { getPokemonNamesByType(it) }
        return typeNameLists.reduce { acc, list -> acc.intersect(list).toList() }
    }

    private suspend fun getDominantColor(imageUrl: String, context: Context): String {
        return withContext(Dispatchers.IO) {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.drawable.toBitmap()
                val palette = Palette.from(bitmap).generate()
                val dominantColor = palette.getDominantColor(Color.Gray.toArgb())
                String.format("#%06X", 0xFFFFFF and dominantColor)
            } else "#CCCCCC"
        }
    }
}
