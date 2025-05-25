package com.angelotacoj.apprutaoptimaescape.core.domain.graph

import kotlinx.serialization.Serializable

@Serializable
data class Graph(
    val id: String,
    val name: String,
    val description: String,
    val nodes: List<Node>
)
