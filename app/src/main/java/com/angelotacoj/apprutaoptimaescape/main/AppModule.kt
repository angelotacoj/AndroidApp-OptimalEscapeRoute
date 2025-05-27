package com.angelotacoj.apprutaoptimaescape.main

import com.angelotacoj.apprutaoptimaescape.core.domain.graph.DataRepository
import com.angelotacoj.apprutaoptimaescape.features.location.presentation.LocationViewModel
import com.angelotacoj.apprutaoptimaescape.features.map_selector.presentation.MapsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { DataRepository(androidContext()) }
    viewModel { MapsViewModel(get()) }
    viewModel { LocationViewModel(get()) }
}