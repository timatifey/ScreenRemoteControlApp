package com.timatifey.models.data

import java.io.Serializable

data class Scroll(
    val eventType: ScrollEventType,
    val deltaX: Double,
    val deltaY: Double,
    val totalDeltaX: Double,
    val totalDeltaY: Double,
    val textDeltaX: Double,
    val textDeltaY: Double,
    val multiplierX: Double,
    val multiplierY: Double
): Serializable {
    enum class ScrollEventType: Serializable {
        ANY,
        SCROLL,
        SCROLL_STARTED,
        SCROLL_FINISHED
    }
}