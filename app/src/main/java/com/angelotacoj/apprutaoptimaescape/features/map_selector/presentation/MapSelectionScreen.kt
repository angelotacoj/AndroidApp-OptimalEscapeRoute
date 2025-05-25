package com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import org.koin.androidx.compose.getViewModel

@Composable
fun MapSelectionScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues? = null,
    onMapSelected: (Graph) -> Unit
) {
    val viewModel: MapsViewModel = getViewModel()
    val maps = viewModel.maps.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMaps()
    }

    paddingValues?.let {
        modifier
    }?.let {
        LazyColumn(
            modifier = modifier
        ) {
            items(maps.value) { map ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onMapSelected(map) }
                ) {
                    Text(text = "${map.name} - ${map.description}", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}