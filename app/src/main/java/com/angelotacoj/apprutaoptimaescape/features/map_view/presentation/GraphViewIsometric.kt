package com.angelotacoj.apprutaoptimaescape.features.map_view.presentation

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
import kotlin.math.max
import kotlin.math.min

fun projectIsometric(x: Float, y: Float, z: Float): Offset {
    val isoX = (x - y) * 60f
    val isoY = (x + y) * 30f - z * 150f
    return Offset(isoX, isoY)
}

@Composable
fun GraphViewIsometric(
    graph: Graph,
    pathToHighlight: List<String> = emptyList(),
    currentStep: Int = -1,
    modifier: Modifier = Modifier
) {
    var selectedNode by remember { mutableStateOf<Node?>(null) }
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

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
                        selectedNode = touched
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
                            color = if (isInPath) Color.Yellow else Color.Gray,
                            start = p1,
                            end = p2,
                            strokeWidth = if (isInPath) 6f else 3f
                        )
                    }
                }

                // 2) Dibuja nodos (único pase, con resaltado)
                graph.nodes.forEach { node ->
                    val pos = nodePositions[node.id] ?: return@forEach
                    val inPath      = node.id in pathToHighlight
                    val isCurrent   = pathToHighlight.getOrNull(currentStep) == node.id
                    val nodeColor   = when {
                        isCurrent -> Color.Magenta
                        inPath    -> Color.Yellow
                        else      -> when (node.alt) {
                            1 -> Color(0xFF2196F3)
                            2 -> Color(0xFF4CAF50)
                            3 -> Color(0xFFFFC107)
                            4 -> Color(0xFFF44336)
                            5 -> Color(0xFF9C27B0)
                            6 -> Color(0xFFFF9800)
                            7 -> Color(0xFF3F51B5)
                            8 -> Color(0xFF009688)
                            9 -> Color(0xFFE91E63)
                            else -> Color.LightGray
                        }
                    }
                    drawCircle(nodeColor, radius = 20f, center = pos, style = Stroke(width = 3f))
                    drawCircle(nodeColor.copy(alpha = 0.5f), radius = 15f, center = pos, style = Fill)
                    nativeCanvas.drawText(node.id, pos.x + 24f, pos.y - 24f, paint)
                }

                restore()
            }
        }

        // Tooltip al seleccionar nodo
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