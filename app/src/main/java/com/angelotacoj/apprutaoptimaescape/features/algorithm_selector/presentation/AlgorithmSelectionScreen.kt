package com.angelotacoj.apprutaoptimaescape.features.algorithm_selector.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.angelotacoj.apprutaoptimaescape.R

@Composable
fun AlgorithmSelectionScreen(
    paddingValues: PaddingValues,
    onAlgorithmSelected: (String) -> Unit
) {
    //val algorithms = listOf("Ant Colony", "A*", "BFS", "DFS")
    val algorithms = listOf(
        "Dijkstra",
        "QLearning",
    )
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(algorithms) { algorithm ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onAlgorithmSelected(algorithm) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(algorithm, Modifier.padding(16.dp))
                    Image(
                        modifier = Modifier.padding(16.dp).size(24.dp),
                        painter = painterResource(R.drawable.img_3),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
            }
        }
    }
}