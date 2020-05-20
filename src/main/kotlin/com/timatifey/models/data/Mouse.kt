package com.timatifey.models.data

data class Mouse(
        val eventType: MouseEventType,
        val x: Double,
        val y: Double,
        val relativelyX: Double,
        val relativelyY: Double,
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
): DataPackage.DataObject() {
    enum class MouseEventType {
        ANY,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_CLICKED,
        MOUSE_ENTERED_TARGET,
        MOUSE_ENTERED,
        MOUSE_EXITED_TARGET,
        MOUSE_EXITED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        DRAG_DETECTED
    }

    enum class MouseButton {
        NONE,
        PRIMARY,
        MIDDLE,
        SECONDARY
    }
}