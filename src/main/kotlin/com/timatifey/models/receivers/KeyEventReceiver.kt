package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Key
import java.awt.Robot
import java.io.ObjectInputStream

class KeyEventReceiver(input: ObjectInputStream) : Receiver<Key>(input) {
    override val socketName: String
        get() = "KEY_SOCKET"

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

//class KeyEventReceiver(private val input: ObjectInputStream): Runnable {
//    @Volatile var needStop = false
//
//    private fun keyRealise(key: Key) {
//        val robot = Robot()
//        try {
//            when (key.eventType) {
//                Key.KeyEventType.KEY_PRESSED -> robot.keyPress(key.code.code)
//                Key.KeyEventType.KEY_RELEASED -> robot.keyRelease(key.code.code)
//                else -> {}
//            }
//        } catch (e: IllegalArgumentException) {
//            println("Key realise error: ${e.message}")
//        } catch (e: NoSuchMethodError) {
//            println("Key Get Code Error: $e")
//            needStop = true
//        }
//    }
//
//    override fun run() {
//        try {
//            needStop = false
//            println("Key event receiver has started")
//
//            while (!needStop) {
//                try {
//                    val data = input.readObject() as DataPackage
//                    if (data.dataType == DataPackage.DataType.KEY) {
//                        val key = data.key!!
//                        keyRealise(key)
//                    }
//                } catch (e: EOFException) {
//                    needStop = true
//                } catch (e: SocketException) {
//                    needStop = true
//                }
//            }
//        } catch (e: IOException) {
//            println("Key Event Receiver Client Socket Error: $e")
//        } finally {
//            needStop = true
//            println("Key Event Receiver Stop")
//            try {
//                input.close()
//            } catch (e: SocketException) {}
//        }
//    }
//
//    fun stop() { needStop = true }
//}