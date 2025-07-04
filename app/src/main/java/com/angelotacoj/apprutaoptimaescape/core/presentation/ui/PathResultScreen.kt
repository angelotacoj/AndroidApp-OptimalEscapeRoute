package com.angelotacoj.apprutaoptimaescape.core.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.AntColony
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.BFS
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.Dijkstra
import com.angelotacoj.apprutaoptimaescape.core.domain.algorithms.QLearning
import com.angelotacoj.apprutaoptimaescape.features.location.presentation.LocationViewModel
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapsViewModel
import com.angelotacoj.apprutaoptimaescape.features.map_view.presentation.GraphViewIsometric
import com.angelotacoj.apprutaoptimaescape.features.map_view.presentation.projectIsometric
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import org.koin.androidx.compose.koinViewModel
import kotlin.math.cos
import kotlin.math.pow

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("LogNotTimber")
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PathResultScreen(
    mapId: String,
    algorithm: String,
    selectedNodeId: String,
    paddingValues: PaddingValues,
    navController: NavController,
) {
    val mapVm: MapsViewModel = koinViewModel()
    val locVm: LocationViewModel = koinViewModel()
    val graphState    by mapVm.maps.collectAsState()
    val locationState by locVm.location.collectAsState()

    // **Punto de referencia GPS** (esquina conocida de tu plano)
    val REF_LAT = 19.43254
    val REF_LON = -99.13327
    // Conversión aproximada grados→metros en latitud/longitud
    val METERS_PER_DEG_LAT = 111_320f
    val METERS_PER_DEG_LON = cos(Math.toRadians(REF_LAT)) * 111_320f

    fun gpsToLocal(lon: Double, lat: Double): Pair<Float, Float> {
        val dx = ((lon - REF_LON) * METERS_PER_DEG_LON).toFloat()
        val dy = ((lat - REF_LAT) * METERS_PER_DEG_LAT).toFloat()
        return dx to dy
    }

    // 1) Pedir permisos de ubicación
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) locVm.startLocationUpdates()
        if (graphState.isEmpty()) mapVm.loadMaps()
    }

    // 2) Mientras no tengamos grafo O ubicación, mensaje
    val graph    = graphState.find { it.id == mapId }
    //val selectedNode = graph?.nodes?.find { it.id == selectedNodeId }
    val location = locationState
    if (graph == null || location == null) {
        Box(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Text(
                when {
                    graph    == null -> "Cargando mapa…"
                    location == null -> "Obteniendo ubicación…"
                    else             -> ""
                },
                Modifier.align(Alignment.Center)
            )
        }
        return
    }

    // 3) Muestra GPS bruto y convierte a coordenadas locales
    val (gx, gy) = gpsToLocal(location.longitude, location.latitude)
    // 4) Proyecta isométrico para visualizar
    val userOffset = projectIsometric(x = gx, y = gy, z = 0f)

    // 5) Encuentra nodo más cercano (euclídea en isométrico)
    val nearestGpsNode = graph.nodes.minByOrNull { node ->
        val p = projectIsometric(node.lon.toFloat(), node.lat.toFloat(), node.alt.toFloat())
        (p.x - userOffset.x).pow(2) + (p.y - userOffset.y).pow(2)
    }!!.id

    // 6) Calcula ruta con el algoritmo elegido
    val exits = graph.nodes.filter { it.type == "Salida" }.map { it.id }
    val path = remember(graph, algorithm, selectedNodeId) {
        when (algorithm) {
            "Dijkstra" -> Dijkstra.findPath(graph, selectedNodeId, exits)
            "QLearning"  -> QLearning.findPath(graph, selectedNodeId, exits)
            else         -> BFS.findPath(graph, selectedNodeId, exits)
        }
    }

    // 6) Paso actual (sobre la lista `path`)
    var step by remember { mutableIntStateOf(0) }
    val isAtEnd = step >= path.lastIndex
    val currentNode = path.getOrNull(step) ?: selectedNodeId
    val nextNode    = path.getOrNull(step + 1)

    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        // --- MAPA ---
        Box(Modifier.weight(1f)) {
            GraphViewIsometric(
                graph           = graph,
                pathToHighlight = path,
                startNode = null,
                onStartNodeSelected = {}
            )
        }

        Spacer(Modifier.height(8.dp))

        // --- INSTRUCCIONES DINÁMICAS ---
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Text("Estás en: $currentNode", Modifier.padding(horizontal = 16.dp))
            if (nextNode != null) {
                Text("Siguiente: ve al nodo $nextNode", Modifier.padding(horizontal = 16.dp))
            } else {
                Text("¡Llegaste a la salida!", Modifier.padding(horizontal = 16.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- BOTÓN AVANZAR / FINALIZAR ---
        Button(
            onClick = {
                if (!isAtEnd) step++ else navController.navigate("metrics_screen")
            },
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(if (!isAtEnd) "Siguiente paso" else "Finalizar")
        }
    }
}