package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.client.Client
import com.timatifey.models.data.ClientListElement
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mode
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class MessageReceiver (
    private val socket: Socket,
    private val mode: Mode,
    private val clientListElement: ClientListElement? = null,
    private val client: Client? = null
): Runnable, Receiver {
    @Volatile var needStop = false

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            needStop = false
            while (!needStop) {
                val json = input.readLine()
                if (json != null) {
                    try {
                        println(json)
                        val data = Gson().fromJson(json, DataPackage::class.java)
                        if (data.dataType == DataPackage.DataType.MESSAGE) {
                            val text = data.message!!
                            println(text)
                            when (mode) {
                                Mode.SERVER -> {
                                    val msg = text.split(":")
                                    if (msg[1] == "stop") {
                                        println("${msg[0]} has disconnected")
                                        clientListElement!!.stopAll()
                                    }
                                }
                                Mode.CLIENT -> {
                                    if (text == "stop") {
                                        client?.setShutdownImage()
                                        client?.stopConnection()
                                    }
                                }
                            }

                        }
                    } catch (e: IllegalStateException) {
                        println("Message Receiver: ${e.message}")
                    }
                }
            }
            input.close()
            socket.close()
        } catch (e: IOException) {
            println("Message Receiver Socket Error: $e")
        }
    }

    fun stop() { needStop = true }
}