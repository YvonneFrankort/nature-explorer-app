package com.example.naturegame.utils

import com.example.naturegame.data.local.entity.NatureSpot
import com.example.naturegame.viewmodel.toFormattedDate

fun groupSpotsByDate(
    spots: List<NatureSpot>
): Map<String, List<NatureSpot>> {

    val events = spots.map { spot ->
        val date = spot.timestamp.toFormattedDate().substring(0, 10)
        date to spot
    }

    return events
        .sortedByDescending { it.first }
        .groupBy({ it.first }, { it.second })
}
