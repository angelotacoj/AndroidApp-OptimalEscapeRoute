package com.angelotacoj.apprutaoptimaescape.features.map_view.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Node
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapDetailScreen(
    modifier: Modifier = Modifier,
    mapId: String,
    paddingValues: PaddingValues,
    onContinue: (String) -> Unit
) {

    val viewModel: MapsViewModel = koinViewModel()
    val mapsState = viewModel.maps.collectAsState()
    val graph = mapsState.value.find { it.id == mapId }
    var startNode by remember { mutableStateOf<Node?>(null) }

    LaunchedEffect(Unit) {
        if (mapsState.value.isEmpty()) {
            viewModel.loadMaps()
        }
    }

    paddingValues?.let {
        modifier
    }?.let {
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            if (graph != null) {
                GraphViewIsometric(
                    graph = graph,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp),
                    startNode = startNode,
                    onStartNodeSelected = { selected ->
                        startNode = selected
                        println("🚀 startNode seleccionado en padre: $selected")
                    }
                )

                Button(
                    onClick = {
                        startNode?.let {
                            onContinue(it.id)
                        }
                    },
                    enabled = startNode != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Continuar")
                }
            } else {
                Text(text = "Mapa no encontrado", modifier = Modifier.padding(16.dp))
            }
        }
    }
}