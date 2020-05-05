package com.timatifey.core

data class Mouse(
    val x: Int,
    val y: Int,
    val pressedLeftButton: Boolean = false,
    val pressedRightButton: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mouse

        if (x != other.x) return false
        if (y != other.y) return false
        if (pressedLeftButton != other.pressedLeftButton) return false
        if (pressedRightButton != other.pressedRightButton) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + pressedLeftButton.hashCode()
        result = 31 * result + pressedRightButton.hashCode()
        return result
    }
}