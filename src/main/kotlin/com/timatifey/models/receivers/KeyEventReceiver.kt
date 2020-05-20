package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Key
import java.awt.AWTException
import java.awt.Robot
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class KeyEventReceiver(private val client: Socket): Runnable {
    @Volatile var needStop = false

    private fun keyRealise(key: Key) {
        try {
            val robot = Robot()
            when (key.eventType) {
                Key.KeyEventType.KEY_PRESSED -> {
                    robot.keyPress(key.code.code)
                }
                Key.KeyEventType.KEY_RELEASED -> {
                    robot.keyRelease(key.code.code)
                }
            }
        } catch (e: AWTException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            needStop = false
            while (!needStop) {
                if (!client.isConnected) {
                    needStop = false
                    break
                }
                val json = input.readLine()
                if (json != null) {
                    val data = Gson().fromJson(json, DataPackage::class.java)
                    if (data.dataType == DataPackage.DataType.KEY) {
                        println(data.dataObject)
                        val k = data.dataObject as Key
                        println(k)
                        //keyRealise(data.dataObject as Key)
                    }
                }
            }
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Key Event Receiver Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}