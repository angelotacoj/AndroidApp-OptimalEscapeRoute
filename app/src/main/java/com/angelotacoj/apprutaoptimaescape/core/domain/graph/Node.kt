package com.angelotacoj.apprutaoptimaescape.core.domain.graph

import kotlinx.serialization.Serializable

@Serializable
data class Node(
    val id: String,
    val lon: Double,
    val lat: Double,
    val alt: Int,
    val connections: List<Connection>,
    val number: Int,
    val floor: String,
    val type: String,
    val risk_block_a: Int,
    val risk_block_b: Int,
)
