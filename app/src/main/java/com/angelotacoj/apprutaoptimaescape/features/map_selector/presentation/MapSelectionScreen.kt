package com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelotacoj.apprutaoptimaescape.core.domain.graph.Graph
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapSelectionScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues? = null,
    onMapSelected: (Graph) -> Unit
) {
    val viewModel: MapsViewModel = koinViewModel()
    val maps = viewModel.maps.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMaps()
    }

    paddingValues?.let {
        modifier
    }?.let {
        LazyColumn(
            modifier = modifier.padding(paddingValues)
        ) {
            item {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "Mapas disponibles",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Estos son los mapas disponibles para ti",
                        fontWeight = FontWeight.Light,
                        fontSize = 20.sp
                    )
                }
            }
            items(maps.value) { map ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onMapSelected(map) }
                ) {
                    Text(text = "${map.name} - ${map.description}", modifier = Modifier.padding(16.dp))
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {  }
                ) {
                    Text(text = "Casa - Casa de 4 pisos", modifier = Modifier.padding(16.dp))
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {  }
                ) {
                    Text(text = "Edificio - Edificio de 7 pisos", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}