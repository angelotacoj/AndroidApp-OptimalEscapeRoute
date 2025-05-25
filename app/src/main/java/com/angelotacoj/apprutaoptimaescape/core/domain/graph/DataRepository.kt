package com.angelotacoj.apprutaoptimaescape.core.domain.graph

import android.content.Context
import com.angelotacoj.apprutaoptimaescape.R
import kotlinx.serialization.json.Json

class DataRepository(
    private val context: Context
) {
    fun loadMaps(): MapData? {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.maps)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            Json.decodeFromString<MapData>(jsonString)
        }catch (e:Exception){
            e.printStackTrace()
            null
        }
    }
}