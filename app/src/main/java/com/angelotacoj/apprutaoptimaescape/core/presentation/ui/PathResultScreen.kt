package com.angelotacoj.apprutaoptimaescape.core.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.AStar
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.BFS
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.Dijkstra
import com.angelotacoj.apprutaoptimaescape.features.map_view.presentation.GraphViewIsometric
import com.angelotacoj.apprutaoptimaescape.features.map_view.presentation.projectIsometric
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapsViewModel
import org.koin.androidx.compose.getViewModel
import kotlin.math.pow

@Composable
fun PathResultScreen(
    mapId: String,
    algorithm: String,
    paddingValues: PaddingValues,
    navController: NavController,
) {
    // 1) Cargo el grafo
    val viewModel: MapsViewModel = getViewModel()
    val graphState = viewModel.maps.collectAsState()
    LaunchedEffect(Unit) {
        if (graphState.value.isEmpty()) {
            viewModel.loadMaps()
        }
    }
    val graph = graphState.value.find { it.id == mapId }
    if (graph == null) {
        // mientras cargamos…
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            Text("Cargando mapa…", Modifier.align(Alignment.Center))
        }
        return
    }

    // 2) Simulamos o pedimos ubicación real
    var userPosition by remember { mutableStateOf(Offset.Zero) }
    // Aquí invocarías fused location y transformarías a nuestras coords si quieres
    LaunchedEffect(Unit) {
        // TODO: usar FusedLocationClient para obtener lat/lon y convertirlas
        // Actualmente simulamos en el nodo A9:
        val startNode = graph.nodes.first { it.id == "E2" }
        userPosition = projectIsometric(startNode.lon.toFloat(), startNode.lat.toFloat(), startNode.alt.toFloat())
    }

    // 3) Calcular camino óptimo
    val path = remember(graph, algorithm, userPosition) {
        // 3.1) Hallar nodo más cercano a userPosition
        val nearest = graph.nodes.minByOrNull { node ->
            val p = projectIsometric(node.lon.toFloat(), node.lat.toFloat(), node.alt.toFloat())
            (p.x - userPosition.x).pow(2) + (p.y - userPosition.y).pow(2)
        }!!
        // 3.2) Encontrar todos los nodos tipo “Salida”
        val exits = graph.nodes.filter { it.type == "Salida" }
        // 3.3) Ejecutar algoritmo
        when (algorithm) {
            "Dijkstra" -> Dijkstra.findPath(graph, nearest.id, exits.map { it.id })
            "A*"       -> AStar.findPath(graph, nearest.id, exits.map { it.id })
            else       -> BFS.findPath(graph, nearest.id, exits.map { it.id })
        }
    }

    // 4) Estado de “paso actual”
    var step by remember { mutableStateOf(0) }
    val current = path.getOrNull(step)
    val next    = path.getOrNull(step + 1)


    Column(
        Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // 5) Mapa con highlight de ruta y paso
        Box(Modifier.weight(1f)) {
            GraphViewIsometric(
                graph = graph,
                pathToHighlight = path,
                currentStep = step
            )
        }

        Spacer(Modifier.height(8.dp))
        // 6) Indicaciones textuales
        current?.let {
            Text("Estás en el nodo: $it", Modifier.padding(horizontal = 16.dp))
        }
        if (next != null) {
            Text("Siguiente paso: ve al nodo $next", Modifier.padding(horizontal = 16.dp))
        } else {
            Text("¡Has llegado a la salida!", Modifier.padding(horizontal = 16.dp))
        }

        Spacer(Modifier.height(16.dp))
        // 7) Botón para avanzar
        /*Button(
            onClick = { if (step < path.lastIndex) step++ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(if (next != null) "Ir al siguiente paso" else "Reiniciar ruta").also {
                if (next == null) step = 0
            }
        }*/
        val isAtEnd = step >= path.lastIndex
        Button(
            onClick = { if (!isAtEnd) step++ else navController.popBackStack() },
            enabled = true
        ) { Text(if (!isAtEnd) "Siguiente paso" else "Finalizar") }
    }
}