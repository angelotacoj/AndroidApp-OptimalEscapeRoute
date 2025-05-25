package com.angelotacoj.apprutaoptimaescape.features.algorithm_selector.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlgorithmSelectionScreen(
    mapId: String,
    paddingValues: PaddingValues,
    onAlgorithmSelected: (String) -> Unit
) {
    val algorithms = listOf("Dijkstra", "A*", "BFS", "DFS")
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(algorithms) { it ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onAlgorithmSelected(it) }
            ) {
                Text(it, Modifier.padding(16.dp))
            }
        }
    }
}