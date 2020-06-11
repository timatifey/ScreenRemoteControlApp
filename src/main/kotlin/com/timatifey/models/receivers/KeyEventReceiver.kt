package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Key
import java.awt.Robot
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.net.SocketException

class KeyEventReceiver(private val socket: Socket): Runnable {
    @Volatile var needStop = false

    private fun keyRealise(key: Key) {
        val robot = Robot()
        try {
            when (key.eventType) {
                Key.KeyEventType.KEY_PRESSED -> robot.keyPress(key.code.code)
                Key.KeyEventType.KEY_RELEASED -> robot.keyRelease(key.code.code)
                else -> {}
            }
        } catch (e: IllegalArgumentException) {
            println("Key realise error: ${e.message}")
        }
    }

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            println("Key event receiver has started")
            needStop = false
            while (!needStop) {
                val json = input.readLine()
                if (json != null) {
                    try {
                        val data = Gson().fromJson(json, DataPackage::class.java)
                        if (data.dataType == DataPackage.DataType.KEY) {
                            val key = data.key!!
                            keyRealise(key)
                        }
                    } catch (e: IllegalStateException) {
                        println("Key event receiver: ${e.message}")
                    }
                } else {
                    needStop = true
                }
            }
            input.close()
        } catch (e: IOException) {
            println("Key Event Receiver Client Socket Error: $e")
        } finally {
            needStop = true
            println("Key Event Receiver Stop")
            try {
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}