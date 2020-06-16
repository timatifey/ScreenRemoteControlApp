package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Key
import java.awt.Robot
import java.io.ObjectInputStream

class KeyEventReceiver(input: ObjectInputStream) : EventReceiver<Key>(input) {
    override val socketName = "KEY_SOCKET"

    override fun realise(obj: Data) {
        val key = obj as Key
        val robot = Robot()
        try {
            when (key.eventType) {
                Key.KeyEventType.KEY_PRESSED -> robot.keyPress(key.code.code)
                Key.KeyEventType.KEY_RELEASED -> robot.keyRelease(key.code.code)
                else -> {}
            }
        } catch (e: IllegalArgumentException) {
            println("Key realise error: ${e.message}")
        } catch (e: NoSuchMethodError) {
            println("Key Get Code Error: $e")
            needStop = true
        }
    }
}