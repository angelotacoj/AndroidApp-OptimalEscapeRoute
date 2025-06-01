package com.angelotacoj.apprutaoptimaescape.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.angelotacoj.apprutaoptimaescape.R

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){
    Column(
        modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.img),
            modifier = modifier.size(250.dp).align(Alignment.CenterHorizontally),
            contentDescription = "image_logo"
        )
        Spacer(
            modifier = Modifier.height(20.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "App Evacuación de Desastres",
                textAlign = TextAlign.Center,
                fontSize = 24.sp
            )
        }
        Spacer(
            modifier = Modifier.height(100.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 90.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate("mapSelection")
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Iniciar"
                    )
                    Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.img_1),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.White),
                    )
                }
            }
            Button(
                onClick = {
                    navController.navigate("reports_screen")
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Reportes"
                    )
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.img_2),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
        }
    }
}