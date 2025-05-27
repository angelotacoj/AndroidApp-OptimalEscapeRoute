package com.angelotacoj.apprutaoptimaescape.core.domain.algorithms

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph

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