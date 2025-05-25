package com.angelotacoj.apprutaoptimaescape.core.domain.algorithms

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Node
import java.util.PriorityQueue
import kotlin.math.hypot

object Dijkstra {
    fun findPath(graph: Graph, start: String, goals: List<String>): List<String> {
        // distancias iniciales + previo
        val dist = mutableMapOf<String, Double>()
        val prev = mutableMapOf<String, String?>()
        graph.nodes.forEach { dist[it.id] = Double.POSITIVE_INFINITY; prev[it.id] = null }

        dist[start] = 0.0

        // Cola de prioridad por distancia acumulada
        val pq = PriorityQueue<String>(compareBy { dist[it] ?: Double.POSITIVE_INFINITY })
        pq.add(start)

        var foundGoal: String? = null
        while (pq.isNotEmpty()) {
            val u = pq.poll()
            if (u in goals) {
                foundGoal = u
                break
            }
            val du = dist[u] ?: continue
            graph.nodes.find { it.id == u }?.connections?.forEach { conn ->
                val alt = du + conn.distance
                if (alt < (dist[conn.to] ?: Double.POSITIVE_INFINITY)) {
                    dist[conn.to] = alt
                    prev[conn.to] = u
                    pq.remove(conn.to)
                    pq.add(conn.to)
                }
            }
        }

        // Reconstruir ruta
        val goal = foundGoal ?: return emptyList()
        val path = mutableListOf<String>()
        var cur: String? = goal
        while (cur != null) {
            path.add(cur)
            cur = prev[cur]
        }
        return path.reversed()
    }
}

/**
 * A*: igual que Dijkstra pero con heurística Euclidiana en 3D.
 */
object AStar {
    private fun heuristic(n1: Node, n2: Node): Double {
        val dx = (n1.lat - n2.lat)
        val dy = (n1.lon - n2.lon)
        val dz = (n1.alt - n2.alt)
        return hypot(hypot(dx, dy), dz.toDouble())
    }

    fun findPath(graph: Graph, start: String, goals: List<String>): List<String> {
        val nodesById = graph.nodes.associateBy { it.id }
        val goalSet = goals.toSet()

        val gScore = mutableMapOf<String, Double>().withDefault { Double.POSITIVE_INFINITY }
        val fScore = mutableMapOf<String, Double>().withDefault { Double.POSITIVE_INFINITY }
        val cameFrom = mutableMapOf<String, String?>()

        gScore[start] = 0.0
        // f = g + h(start, nearest goal)
        val h0 = goals.mapNotNull { nodesById[it] }
            .minOf { heuristic(nodesById[start]!!, it) }
        fScore[start] = h0

        val openSet = PriorityQueue<String>(compareBy { fScore.getValue(it) })
        openSet.add(start)

        var foundGoal: String? = null
        while (openSet.isNotEmpty()) {
            val current = openSet.poll()
            if (current in goalSet) {
                foundGoal = current
                break
            }

            val currentNode = nodesById[current]!!
            graph.nodes.find { it.id == current }?.connections?.forEach { conn ->
                val tentativeG = gScore.getValue(current) + conn.distance
                if (tentativeG < gScore.getValue(conn.to)) {
                    cameFrom[conn.to] = current
                    gScore[conn.to] = tentativeG
                    // f = g + h(neighbor, nearest goal)
                    val hMin = goals.mapNotNull { nodesById[it] }
                        .minOf { heuristic(nodesById[conn.to]!!, it) }
                    fScore[conn.to] = tentativeG + hMin
                    if (!openSet.contains(conn.to)) {
                        openSet.add(conn.to)
                    }
                }
            }
        }

        val goal = foundGoal ?: return emptyList()
        val path = mutableListOf<String>()
        var cur: String? = goal
        while (cur != null) {
            path.add(cur)
            cur = cameFrom[cur]
        }
        return path.reversed()
    }
}

/**
 * BFS: camino con menor número de aristas (ignora peso).
 * Útil si solo importa número de pasos, no distancia.
 */
object BFS {
    fun findPath(graph: Graph, start: String, goals: List<String>): List<String> {
        val queue = ArrayDeque<String>()
        val visited = mutableSetOf<String>()
        val prev = mutableMapOf<String, String?>()

        queue.add(start)
        visited.add(start)
        prev[start] = null

        var foundGoal: String? = null
        while (queue.isNotEmpty()) {
            val u = queue.removeFirst()
            if (u in goals) {
                foundGoal = u
                break
            }
            graph.nodes.find { it.id == u }?.connections?.forEach { conn ->
                if (conn.to !in visited) {
                    visited.add(conn.to)
                    prev[conn.to] = u
                    queue.add(conn.to)
                }
            }
        }

        val goal = foundGoal ?: return emptyList()
        val path = mutableListOf<String>()
        var cur: String? = goal
        while (cur != null) {
            path.add(cur)
            cur = prev[cur]
        }
        return path.reversed()
    }
}