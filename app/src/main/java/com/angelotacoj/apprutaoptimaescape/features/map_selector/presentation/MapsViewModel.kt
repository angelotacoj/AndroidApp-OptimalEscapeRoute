package com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.DataRepository
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    private val repository: DataRepository
):ViewModel() {
    private val _maps = MutableStateFlow<List<Graph>>(emptyList())
    val maps: StateFlow<List<Graph>> = _maps

    fun loadMaps() {
        println("Init function maps")
        viewModelScope.launch(Dispatchers.IO) {
            val loaded = repository.loadMaps()
            loaded?.let {
                _maps.value = it.maps
            }
        }
    }
}