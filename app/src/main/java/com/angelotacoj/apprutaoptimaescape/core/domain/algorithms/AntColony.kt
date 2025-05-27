package com.angelotacoj.apprutaoptimaescape.core.domain.algorithms

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import kotlin.math.pow
import kotlin.random.Random

object AntColony {
    /**
     * Busca la ruta más corta con un algoritmo de colonia de hormigas.
     *
     * @param graph      el grafo
     * @param start      nodo de inicio
     * @param goals      nodos meta (“Salida”)
     * @param ants       número de hormigas por iteración
     * @param iterations número de iteraciones
     * @param alpha      importancia de feromona
     * @param beta       importancia de heurística (1/distancia)
     * @param evapRate   tasa de evaporación (0..1)
     */
    fun findPath(
        graph: Graph,
        start: String,
        goals: List<String>,
        ants: Int = 20,
        iterations: Int = 100,
        alpha: Double = 1.0,
        beta: Double = 2.0,
        evapRate: Double = 0.5
    ): List<String> {
        // 1) Inicializar feromonas en cada arista
        data class Edge(val from: String, val to: String)
        val pheromones = mutableMapOf<Edge, Double>()
        graph.nodes.forEach { n ->
            n.connections.forEach { c ->
                pheromones[Edge(n.id, c.to)] = 1.0
            }
        }

        var bestPath: List<String> = listOf()
        var bestLength = Double.POSITIVE_INFINITY

        repeat(iterations) {
            val allPaths = mutableListOf<Pair<List<String>, Double>>()

            // 2) Cada hormiga construye una ruta
            repeat(ants) {
                val visited = mutableSetOf(start)
                val path = mutableListOf(start)
                var current = start
                while (current !in goals) {
                    // posibles vecinos no visitados
                    val neigh = graph.nodes.find { it.id == current }!!.connections
                        .filter { it.to !in visited }
                    if (neigh.isEmpty()) break
                    // cálculo de probabilidades
                    val probs = neigh.map { c ->
                        val tau = pheromones[Edge(current, c.to)]!!.pow(alpha)
                        val eta = (1.0 / c.distance).pow(beta)
                        tau * eta
                    }
                    val sum = probs.sum()
                    val pick = Random.nextDouble() * sum
                    var acc = 0.0
                    val chosen = neigh[probs.indexOfFirst {
                        acc += it; pick <= acc
                    }]
                    current = chosen.to
                    visited += current
                    path += current
                }
                // longitud total
                val length = path.windowed(2).sumOf { (a, b) ->
                    graph.nodes.find { it.id == a }!!
                        .connections.first { it.to == b }.distance
                }.toDouble()
                allPaths += path to length
                if (length < bestLength && path.last() in goals) {
                    bestLength = length
                    bestPath = path.toList()
                }
            }

            // 3) Evaporar feromonas
            pheromones.keys.forEach { pheromones[it] = pheromones[it]!! * (1 - evapRate) }

            // 4) Depositarlas según calidad de rutas
            allPaths.forEach { (path, len) ->
                val delta = 1.0 / len
                path.windowed(2).forEach { (a, b) ->
                    val e = Edge(a, b)
                    pheromones[e] = (pheromones[e] ?: 0.0) + delta
                }
            }
        }

        return bestPath
    }
}