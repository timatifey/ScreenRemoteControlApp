package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class MessageReceiver (private val socket: Socket): Runnable, Receiver {
    @Volatile var needStop = false

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            println("Message Receiver Start")
            needStop = false
            while (!needStop) {
                val json = input.readLine()
                if (json != null) {
                    try {
                        println(json)
                        val data = Gson().fromJson(json, DataPackage::class.java)
                        if (data.dataType == DataPackage.DataType.MESSAGE) {
                            val text = data.message!!.split(":")
                            if (text[1] == "STOP") {
                                needStop = true
                            }
                        }
                    } catch (e: IllegalStateException) {
                        println("Message Receiver: ${e.message}")
                    }
                } else {
                    needStop = true
                }
            }
            input.close()
            socket.close()
        } catch (e: IOException) {
            println("Message Receiver Socket Error: $e")
        } finally {
            needStop = true
            println("Message Receiver Stop")
        }
    }

    fun stop() { needStop = true }
}