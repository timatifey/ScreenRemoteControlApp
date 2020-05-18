package com.timatifey

import javafx.event.EventType
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

data class Mouse(
    val nameEvent: String,
    val x: Double,
    val y: Double,
    val screenX: Double,
    val screenY: Double,
    val button: MouseButton,
    val clickCount: Int,
    val shiftDown: Boolean,
    val controlDown: Boolean,
    val altDown: Boolean,
    val metaDown: Boolean,
    val primaryButtonDown: Boolean,
    val middleButtonDown: Boolean,
    val popupTrigger: Boolean,
    val stillSincePress: Boolean,
    val secondaryButtonDown: Boolean
)