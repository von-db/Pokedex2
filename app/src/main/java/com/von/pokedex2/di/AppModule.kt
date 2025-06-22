package com.von.pokedex2.di

import com.von.pokedex2.data.remote.PokeApi
import com.von.pokedex2.repository.PokemonRepository
import com.von.pokedex2.viewmodel.PokemonViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<PokeApi> {
        get<Retrofit>().create(PokeApi::class.java)
    }

    single {
        PokemonRepository(get())
    }


    viewModel {
        PokemonViewModel(getKoin().get(), get())
    }
}
