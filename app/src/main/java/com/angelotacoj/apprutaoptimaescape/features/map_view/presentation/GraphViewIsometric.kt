package com.angelotacoj.apprutaoptimaescape.features.map_view.presentation

import android.R.attr.startX
import android.R.attr.startY
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Node
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

fun projectIsometric(x: Float, y: Float, z: Float): Offset {
    val isoX = (x - y) * 60f
    val isoY = (x + y) * 30f - z * 150f
    return Offset(isoX, isoY)
}

@Composable
fun GraphViewIsometric(
    modifier: Modifier = Modifier,
    graph: Graph,
    pathToHighlight: List<String> = emptyList(),
    startNode: Node?,
    onStartNodeSelected: (Node) -> Unit,
    arrowRotation: Float = 0f,
    arrowOrigin: Offset? = null
) {

    var selectedNode by remember { mutableStateOf<Node?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(selectedNode) {
        println("selectedNode cambió a: $selectedNode")
    }

    LaunchedEffect(key1 = true) {
        println("Entre a la pantalla desde PathResultScreen")
        println("===== startNode es: $startNode")
        println("===== selectedNode es: $selectedNode")
    }

    // Proyección isométrica de cada nodo
    val nodePositions = remember(graph) {
        graph.nodes.associate { node ->
            node.id to projectIsometric(
                x = node.lon.toFloat(),
                y = node.lat.toFloat(),
                z = node.alt.toFloat()
            )
        }
    }

    val localArrowOrigin = (arrowOrigin?.minus(offset))?.div(scale)

    // Calcula bounds del grafo para centrar
    val bounds = remember(nodePositions) {
        nodePositions.values.fold<Offset, RectF?>(null) { acc, point ->
            acc?.apply {
                left   = min(left, point.x)
                top    = min(top, point.y)
                right  = max(right, point.x)
                bottom = max(bottom, point.y)
            } ?: RectF(point.x, point.y, point.x, point.y)
        }
    }

    // Centrado automático (valores fijos de desplazamiento)
    LaunchedEffect(bounds) {
        bounds?.let {
            val center = Offset(
                x = -(it.left + it.right) / 2f + 400f,
                y = -(it.top + it.bottom) / 2f + 800f
            )
            offset = center
            scale = 1f
        }
    }

    Box(
        modifier = modifier
            .pointerInput(graph) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.3f, 5f)
                    offset += pan
                }
                detectTapGestures { tap ->
                    val p = (tap - offset) / scale
                    val touched = nodePositions.entries
                        .find { (_, pos) -> (p - pos).getDistance() <= 25f }
                        ?.key
                        ?.let { id -> graph.nodes.first { it.id == id } }

                    if (startNode == null && touched != null) {
                        selectedNode = touched
                        println("selectedNode: $selectedNode")
                        onStartNodeSelected(touched)
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(graph) {
                    detectTapGestures { tap ->
                        val p = (tap - offset) / scale
                        val touched = nodePositions.entries
                            .find { (_, pos) -> (p - pos).getDistance() <= 25f }
                            ?.key
                            ?.let { id -> graph.nodes.first { it.id == id } }
                        /*selectedNode = touched*/
                        if (startNode == null && touched != null) {
                            selectedNode = touched
                            println("selectedNode 2: $selectedNode")
                            onStartNodeSelected(touched)
                        }
                    }
                }
        ) {
            with(drawContext.canvas) {
                save()
                translate(offset.x, offset.y)
                scale(scale, scale)

                // Paint para etiquetas
                val paint = Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                    isAntiAlias = true
                }

                // Precompute edges en ruta como sets bidireccionales
                val pathEdges = pathToHighlight
                    .windowed(2)
                    .map { it.toSet() }

                // 1) Dibuja aristas
                graph.nodes.forEach { node ->
                    val p1 = nodePositions[node.id] ?: return@forEach
                    node.connections.forEach { c ->
                        val p2 = nodePositions[c.to] ?: return@forEach
                        val isInPath = pathEdges.contains(setOf(node.id, c.to))
                        drawLine(
                            color = if (isInPath) Color.Red else Color.Gray,
                            start = p1,
                            end = p2,
                            strokeWidth = if (isInPath) 6f else 3f
                        )
                    }
                }

                // 2) Dibuja nodos
                graph.nodes.forEach { node ->
                    val pos = nodePositions[node.id] ?: return@forEach
                    val nodeColor = when {
                        //startNode == null && selectedNode == node -> Color.Magenta
                        startNode == node -> Color.Cyan
                        else -> Color.Gray
                    }
                    drawCircle(nodeColor, radius = 20f, center = pos, style = Stroke(width = 3f))
                    drawCircle(nodeColor.copy(alpha = 0.5f), radius = 15f, center = pos, style = Fill)
                }

                startNode?.let {
                    arrowOrigin?.let { origin ->
                        val localOrigin = (origin - offset) / scale

                        val arrowLength = 120f
                        val radians = Math.toRadians(arrowRotation.toDouble())
                        val arrowEndX = localOrigin.x + cos(radians) * arrowLength
                        val arrowEndY = localOrigin.y + sin(radians) * arrowLength
                        val arrowEnd = Offset(arrowEndX.toFloat(), arrowEndY.toFloat())

                        // Dibuja el cuerpo de la flecha
                        drawLine(
                            color = Color.Black,
                            start = localOrigin,
                            end = arrowEnd,
                            strokeWidth = 16f
                        )

                        // tamaño de la cabeza de la flecha
                        val headSize = 24f
                        val angle = Math.PI / 6 // 30 grados de apertura de la cabeza

                        val leftX = arrowEnd.x - headSize * cos(radians - angle).toFloat()
                        val leftY = arrowEnd.y - headSize * sin(radians - angle).toFloat()
                        val rightX = arrowEnd.x - headSize * cos(radians + angle).toFloat()
                        val rightY = arrowEnd.y - headSize * sin(radians + angle).toFloat()

                        // lado izquierdo de la cabeza de flecha
                        drawLine(
                            color = Color.Black,
                            start = arrowEnd,
                            end = Offset(leftX, leftY),
                            strokeWidth = 16f
                        )

                        // lado derecho de la cabeza de flecha
                        drawLine(
                            color = Color.Black,
                            start = arrowEnd,
                            end = Offset(rightX, rightY),
                            strokeWidth = 16f
                        )
                    }
                }

                restore()
            }
        }

        selectedNode?.let { node ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nodo seleccionado", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ID: ${node.id}", style = MaterialTheme.typography.bodyLarge)
                    Text("Lat: ${node.lat}, Lon: ${node.lon}", style = MaterialTheme.typography.bodyLarge)
                    Text("Alt: ${node.alt}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { selectedNode = null }) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}