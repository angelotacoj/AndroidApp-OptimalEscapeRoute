package com.angelotacoj.apprutaoptimaescape.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.angelotacoj.apprutaoptimaescape.features.algorithm_selector.presentation.AlgorithmSelectionScreen
import com.angelotacoj.apprutaoptimaescape.core.presentation.designsystem.AppTopBar
import com.angelotacoj.apprutaoptimaescape.features.map_view.presentation.MapDetailScreen
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapSelectionScreen
import com.angelotacoj.apprutaoptimaescape.core.presentation.ui.PathResultScreen
import com.angelotacoj.apprutaoptimaescape.features.metrics.presentation.MetricsScreen
import com.angelotacoj.apprutaoptimaescape.features.reports.presentation.ReportsScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(navController, startDestination = "mainScreen", modifier = Modifier.padding(paddingValues)) {
        composable("mainScreen"){
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "App Evacuación de Desastres",
                        canNavigateBack = false,
                        navController = navController
                    )
                }
            ) { innerPadding ->
                MainScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
        }
        composable("reports_screen"){
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Reportes",
                        canNavigateBack = true,
                        navController = navController
                    )
                }
            ) { innerPadding ->
                ReportsScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable("mapSelection") {
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Seleccionar Mapa",
                        canNavigateBack = true,
                        navController = navController
                    )
                }
            ) { innerPadding ->
                MapSelectionScreen(
                    modifier = Modifier.padding(innerPadding),
                    paddingValues = paddingValues,
                    onMapSelected = { selectedMap ->
                        navController.navigate("mapDetail/${selectedMap.id}")
                    }
                )
            }
        }
        composable("mapDetail/{mapId}") { backStackEntry ->
            val mapId = backStackEntry.arguments?.getString("mapId")
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Detalle del Mapa",
                        canNavigateBack = true,
                        navController = navController
                    )
                }
            ) { innerPadding ->
                mapId?.let {
                    MapDetailScreen(
                        mapId = it,
                        paddingValues = innerPadding,
                        onContinue = {
                            navController.navigate("algorithmSelection/$it")
                        }
                    )
                } ?: Text("Mapa no encontrado")
            }
        }
        composable("algorithmSelection/{mapId}") { back ->
            val mapId = back.arguments!!.getString("mapId")!!
            Scaffold(
                topBar = { AppTopBar("Elegir Algoritmo", true, navController) }
            ) { inner ->
                AlgorithmSelectionScreen(
                    mapId = mapId,
                    paddingValues = inner
                ) { it ->
                    navController.navigate("pathResult/$mapId/$it")
                }
            }
        }
        composable("pathResult/{mapId}/{algorithm}") { back ->
            val mapId = back.arguments!!.getString("mapId")!!
            val algorithm = back.arguments!!.getString("algorithm")!!
            Scaffold(
                topBar = { AppTopBar("Resultado de Ruta", true, navController) }
            ) { inner ->
                PathResultScreen(
                    mapId = mapId,
                    algorithm = algorithm,
                    paddingValues = inner,
                    navController = navController
                )
            }
        }
        composable("metrics_screen"){
            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "Métricas de evaluación",
                        canNavigateBack = false,
                        navController = navController
                    )
                }
            ) { innerPadding ->
                MetricsScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
        }
    }
}