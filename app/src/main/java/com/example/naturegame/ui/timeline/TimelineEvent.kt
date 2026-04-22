package com.example.naturegame.ui.timeline

import com.example.naturegame.data.local.entity.WalkSession
import com.example.naturegame.data.local.entity.NatureSpot

sealed class TimelineEvent {
    data class WalkEvent(val walk: WalkSession) : TimelineEvent()
    data class DiscoveryEvent(val spot: NatureSpot) : TimelineEvent()
}