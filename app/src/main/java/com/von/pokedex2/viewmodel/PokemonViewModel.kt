package com.von.pokedex2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.von.pokedex2.data.remote.responses.Pokemon
import com.von.pokedex2.data.remote.responses.PokemonListEntry
import com.von.pokedex2.repository.PokemonRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class PokemonViewModel(
    application: Application,
    private val repository: PokemonRepository
) : AndroidViewModel(application) {

    private val _pokemonList = MutableStateFlow<List<PokemonListEntry>>(emptyList())
    val pokemonList: StateFlow<List<PokemonListEntry>> = _pokemonList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var currentPage = 0
    private val pageSize = 10

    private var isSearching = false
    private var matchingNames: List<String>? = null

    private var filterJob: Job? = null

    private val searchQueryFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val _selectedTypes = MutableStateFlow<List<String>>(emptyList())
    val selectedTypes: StateFlow<List<String>> = _selectedTypes

    @Volatile
    private var currentSearchSessionId = 0
    private var activeSessionId = 0

    init {
        fetchNext()

        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest {
                    refreshFilteredPokemon()
                }
        }
    }

    fun refreshFilteredPokemon() {
        filterJob?.cancel()

        val thisSessionId = ++currentSearchSessionId
        activeSessionId = thisSessionId
        currentPage = 0
        _isLoading.value = true
        matchingNames = null
        isSearching = false

        filterJob = viewModelScope.launch {
            val query = _searchQuery.value.trim()
            val types = _selectedTypes.value

            _pokemonList.value = emptyList()

            if (query.isBlank() && types.isEmpty()) {
                try {
                    val result = repository.getPokemonList(
                        limit = pageSize,
                        offset = currentPage * pageSize,
                        context = getApplication()
                    )
                    if (thisSessionId == activeSessionId) {
                        _pokemonList.update { it + result }
                        currentPage++
                    }
                } catch (_: Exception) {
                } finally {
                    _isLoading.value = false
                }
                return@launch
            }

            try {
                if (thisSessionId == activeSessionId) {
                    isSearching = true
                }

                val names = withContext(Dispatchers.IO) {
                    val typeFiltered = if (types.isNotEmpty()) {
                        repository.getPokemonNamesByTypes(types)
                    } else repository.getAllPokemonNames()

                    if (query.isNotBlank()) {
                        typeFiltered.filter { it.contains(query, ignoreCase = true) }
                    } else typeFiltered
                }

                if (thisSessionId == activeSessionId) {
                    matchingNames = names

                    val start = currentPage * pageSize
                    val end = (start + pageSize).coerceAtMost(names.size)

                    if (start < end && names.isNotEmpty()) {
                        _isLoading.value = true

                        val result = withContext(Dispatchers.IO) {
                            coroutineScope {
                                names.subList(start, end).map { name ->
                                    async {
                                        repository.getPokemonDetailsByName(name, getApplication())
                                    }
                                }.awaitAll()
                            }
                        }

                        _pokemonList.update { it + result }
                        currentPage++
                    }

                    _isLoading.value = false
                }
            } catch (_: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun toggleType(type: String) {
        _selectedTypes.value = _selectedTypes.value.toMutableList().apply {
            if (contains(type)) remove(type) else add(type)
        }
        refreshFilteredPokemon()
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        searchQueryFlow.tryEmit(newQuery)
    }

    fun fetchNext() {
        if (!isSearching) {
            fetchNextPokemonPage()
        } else {
            val names = matchingNames ?: return
            val start = currentPage * pageSize
            val end = (start + pageSize).coerceAtMost(names.size)
            if (start >= end) return

            _isLoading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = coroutineScope {
                        names.subList(start, end).map { name ->
                            async {
                                repository.getPokemonDetailsByName(name, getApplication())
                            }
                        }.awaitAll()
                    }

                    if (activeSessionId == currentSearchSessionId) {
                        _pokemonList.update { it + result }
                        currentPage++
                    }

                } catch (_: Exception) {
                } finally {
                    withContext(NonCancellable) {
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    suspend fun getPokemonByName(name: String): Pokemon? {
        return try {
            repository.getPokemonByName(name)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getPokemonEntryByName(name: String, context: android.content.Context): PokemonListEntry {
        return repository.getPokemonDetailsByName(name, context)
    }

    fun fetchNextPokemonPage() {
        if (_isLoading.value) return

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.getPokemonList(
                    limit = pageSize,
                    offset = currentPage * pageSize,
                    context = getApplication()
                )
                _pokemonList.update { it + result }
                currentPage++
            } catch (_: Exception) {
            } finally {
                withContext(NonCancellable) {
                    _isLoading.value = false
                }
            }
        }
    }
}
