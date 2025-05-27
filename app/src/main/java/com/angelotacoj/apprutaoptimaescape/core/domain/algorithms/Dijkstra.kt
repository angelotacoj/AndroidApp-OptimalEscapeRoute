package com.angelotacoj.apprutaoptimaescape.core.domain.algorithms

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import java.util.PriorityQueue

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