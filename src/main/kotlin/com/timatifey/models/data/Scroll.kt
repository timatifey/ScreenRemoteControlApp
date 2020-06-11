package com.timatifey.models.data

import java.io.Serializable

data class Scroll(
    val eventType: ScrollEventType,
    val totalDeltaY: Double
): Serializable {
    enum class ScrollEventType: Serializable {
        ANY,
        SCROLL,
        SCROLL_STARTED,
        SCROLL_FINISHED
    }
}