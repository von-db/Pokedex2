package com.von.pokedex2.data.remote.responses

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonListItem>
)

data class PokemonListItem(
    val name: String,
    val url: String
)



data class PokemonListEntry(
    val name: String,
    val imageUrl: String,
    val dominantColorHex: String
)

