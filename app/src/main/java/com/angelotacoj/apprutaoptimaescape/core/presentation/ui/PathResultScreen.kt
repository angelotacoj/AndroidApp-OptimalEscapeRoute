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
import androidx.compose.ui.geometry.Offset
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

    // 1) Pedir permisos de ubicación
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        locVm.startLocationUpdates()
        locVm.startHeadingUpdates()
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) locVm.startLocationUpdates()
        if (graphState.isEmpty()) mapVm.loadMaps()
    }

    LaunchedEffect(key1 = true) {
        println("Entre a la pantalla desde PathResultScreen")
        println("===== startNode es: $selectedNodeId")
        println("===== selectedNode es: $selectedNodeId")
    }

    val heading by locVm.heading.collectAsState()

    LaunchedEffect(heading) {
        println("⎈ heading del teléfono: $heading grados")
    }

    // 2) Mientras no tengamos grafo O ubicación, mensaje
    val graph    = graphState.find { it.id == mapId }
    val selectedNode = graph?.nodes?.find { it.id == selectedNodeId }
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


    // conviertes la ubicación del usuario a coordenadas locales
    val (userX, userY) = gpsToLocal(location.longitude, location.latitude)

    // conviertes el siguiente nodo a coordenadas locales
    val nextNodeX = nextNode.let { graph.nodes.find { it.id == nextNode } }?.lon?.toFloat() ?: 0f
    val nextNodeY = nextNode.let { graph.nodes.find { it.id == nextNode } }?.lat?.toFloat() ?: 0f

    // proyectar a isométrico si quieres mantener consistencia visual
    val nextNodeOffset = projectIsometric(nextNodeX, nextNodeY, 0f)

    // cálculo de ángulo en radianes
    val deltaX = nextNodeX - userX
    val deltaY = nextNodeY - userY
    val angleToNext = Math.toDegrees(kotlin.math.atan2(deltaY, deltaX).toDouble())

    // diferencia con heading
    val arrowRotation = ((angleToNext - locVm.heading.collectAsState().value + 360) % 360)

    println("➡️ userX=$userX userY=$userY")
    println("➡️ nextNodeX=$nextNodeX nextNodeY=$nextNodeY")
    println("➡️ angleToNext=$angleToNext")
    println("➡️ heading=${locVm.heading.collectAsState().value}")
    println("➡️ arrowRotation=$arrowRotation")

    val userOffset = Offset(400f, 800f)

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
                startNode = graph.nodes.firstOrNull { it.id == currentNode },
                onStartNodeSelected = {},
                arrowRotation   = arrowRotation.toFloat(),
                arrowOrigin = userOffset
            )
        }

        Spacer(Modifier.height(8.dp))

        // --- INSTRUCCIONES DINÁMICAS ---
        Column(
            modifier = Modifier
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
                if (!isAtEnd){
                    step++
                } else {
                    locVm.stopLocationUpdates()
                    locVm.stopHeadingUpdates()
                    navController.navigate("metrics_screen")
                }
            },
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(if (!isAtEnd) "Siguiente paso" else "Finalizar")
        }
    }
}

fun gpsToLocal(lon: Double, lat: Double): Pair<Float, Float> {
    val REF_LAT = 19.43254
    val REF_LON = -99.13327
    val METERS_PER_DEG_LAT = 111_320f
    val METERS_PER_DEG_LON = kotlin.math.cos(Math.toRadians(REF_LAT)) * 111_320f

    val dx = ((lon - REF_LON) * METERS_PER_DEG_LON).toFloat()
    val dy = ((lat - REF_LAT) * METERS_PER_DEG_LAT).toFloat()
    return dx to dy
}