package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Scroll
import java.awt.Robot
import java.io.ObjectInputStream
import kotlin.math.sign

class ScrollEventReceiver(input: ObjectInputStream) : EventReceiver<Scroll>(input) {
    override val socketName: String
        get() = "SCROLL_SOCKET"

    override fun realise(obj: Data) {
        val scroll = obj as Scroll
        val robot = Robot()
        try {
            when (scroll.eventType) {
                Scroll.ScrollEventType.SCROLL -> {
                    robot.mouseWheel((scroll.deltaY).toInt().sign * 2)
                }
                else -> {}
            }
        } catch (e: IllegalArgumentException) {
            println("Scroll realise error: ${e.message}")
        }
    }
}