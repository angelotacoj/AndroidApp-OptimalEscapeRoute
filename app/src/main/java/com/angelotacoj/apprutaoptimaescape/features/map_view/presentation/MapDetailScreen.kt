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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapDetailScreen(mapId: String, paddingValues: PaddingValues, onContinue: () -> Unit) {
    println("mapId is $mapId")
    val viewModel: MapsViewModel = koinViewModel()
    val mapsState = viewModel.maps.collectAsState()

    LaunchedEffect(Unit) {
        if (mapsState.value.isEmpty()) {
            viewModel.loadMaps()
        }
    }

    val graph = mapsState.value.find { it.id == mapId }
    println("graph is $graph")
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        if (graph != null) {
            GraphViewIsometric(
                graph = graph,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onContinue,
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