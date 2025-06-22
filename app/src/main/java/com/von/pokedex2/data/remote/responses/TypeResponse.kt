package com.von.pokedex2.data.remote.responses

data class TypeResponse(
    val pokemon: List<TypePokemonSlot>
)

data class TypePokemonSlot(
    val pokemon: NamedApiResource
)

data class NamedApiResource(
    val name: String,
    val url: String
)
