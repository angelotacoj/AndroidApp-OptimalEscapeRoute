package com.angelotacoj.apprutaoptimaescape.core.domain.algorithms

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import kotlin.random.Random

object QLearning {
    /**
     * Encuentra el camino óptimo desde start hasta cualquiera de goals
     * usando Q-Learning.
     *
     * @param graph   grafo con nodos y conexiones (distance)
     * @param start   id del nodo de inicio
     * @param goals   lista de ids de nodos “Salida”
     * @param episodes número de episodios de entrenamiento
     * @param alpha   tasa de aprendizaje (0..1)
     * @param gamma   factor de descuento (0..1)
     * @param epsilon probabilidad de exploración (0..1)
     */
    fun findPath(
        graph: Graph,
        start: String,
        goals: List<String>,
        episodes: Int = 1_000,
        alpha: Double = 0.1,
        gamma: Double = 0.9,
        epsilon: Double = 0.1
    ): List<String> {
        // 1) Inicializa Q-table: estado → (acción → valor)
        val Q = mutableMapOf<String, MutableMap<String, Double>>()
        graph.nodes.forEach { node ->
            Q[node.id] = node.connections.associate { it.to to 0.0 }.toMutableMap()
        }

        // 2) Entrenamiento
        repeat(episodes) {
            var current = start
            // tope de pasos para evitar loops infinitos
            repeat(graph.nodes.size * 2) {
                // 2.1) elegir acción
                val actions = Q[current]!!
                val nextId = if (Random.nextDouble() < epsilon) {
                    actions.keys.random()
                } else {
                    actions.maxByOrNull { it.value }!!.key
                }
                // 2.2) recompensa
                val isGoal = nextId in goals
                val cost = graph.nodes.find { it.id == current }!!
                    .connections.first { it.to == nextId }.distance
                val reward = if (isGoal) 1_000.0 else -cost.toDouble()

                // 2.3) actualización Q
                val qcur = Q[current]!![nextId]!!
                val qnextMax = Q[nextId]!!.values.maxOrNull() ?: 0.0
                Q[current]!![nextId] = qcur + alpha * (reward + gamma * qnextMax - qcur)

                current = nextId
                if (isGoal) return@repeat
            }
        }

        // 3) Extraer ruta greedy desde start
        val path = mutableListOf(start)
        var cur = start
        val visited = mutableSetOf(cur)
        while (cur !in goals) {
            val next = Q[cur]!!.maxByOrNull { it.value }!!.key
            if (next in visited) break  // ciclo detectado
            path += next
            visited += next
            cur = next
        }
        return path
    }
}