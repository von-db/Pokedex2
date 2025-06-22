package com.von.pokedex2.data.remote
import com.von.pokedex2.data.remote.responses.PokemonListResponse
import com.von.pokedex2.data.remote.responses.TypeResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonByName(
        @Path("name") name: String
    ): com.von.pokedex2.data.remote.responses.Pokemon

    @GET("type/{typeName}")
    suspend fun getPokemonByType(
        @Path("typeName") type: String
    ): TypeResponse

}
