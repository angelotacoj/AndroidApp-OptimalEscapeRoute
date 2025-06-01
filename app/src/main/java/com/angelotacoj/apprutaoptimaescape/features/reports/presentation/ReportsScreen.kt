package com.angelotacoj.apprutaoptimaescape.features.reports.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelotacoj.apprutaoptimaescape.R

@Composable
fun ReportsScreen(
    modifier: Modifier = Modifier
){
    val algorithms = listOf(
        "Reporte N~1 - Edificio 1",
        "Reporte N~2 - Edificio 1",
        "Reporte N~3 - Casa 2",
        "Reporte N~4 - Edificio 2",
    )
    LazyColumn(modifier = modifier) {

        item {
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = "Reportes de simulaciones",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Aqui encontraras los reportes anteriores",
                    fontWeight = FontWeight.Light,
                    fontSize = 20.sp
                )
            }
        }

        items(algorithms) { it ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {  }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(it, Modifier.padding(16.dp))
                    Image(
                        modifier = Modifier.padding(16.dp).size(24.dp),
                        painter = painterResource(R.drawable.img_2),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.Black)
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {  }
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                       text = stringResource(R.string.lorem_ipsum) ,
                       modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}