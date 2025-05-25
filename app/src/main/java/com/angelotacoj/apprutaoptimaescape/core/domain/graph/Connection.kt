package com.angelotacoj.apprutaoptimaescape.core.domain.graph

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val to: String,
    val distance: Int
)
