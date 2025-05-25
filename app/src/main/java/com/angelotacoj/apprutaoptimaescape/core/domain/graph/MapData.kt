package com.angelotacoj.apprutaoptimaescape.core.domain.graph

import kotlinx.serialization.Serializable

@Serializable
data class MapData(
    val maps: List<Graph>
)
