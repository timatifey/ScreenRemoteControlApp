package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.ObjectInputStream
import java.net.Socket
import java.net.SocketException

class MessageReceiver (private val socket: Socket): Runnable {
    @Volatile var needStop = false
    private lateinit var input: ObjectInputStream

    override fun run() {
        try {
            input = ObjectInputStream(socket.getInputStream())
            needStop = false
            println("Message Receiver Start")
            while (!needStop) {
                val data = input.readObject() as DataPackage
                if (data.dataType == DataPackage.DataType.MESSAGE) {
                    val text = data.message!!.split(":")
                    if (text[1] == "STOP") {
                        needStop = true
                    }
                }
            }
        } catch (e: IOException) {
            println("Message Receiver Socket Error: $e")
        } finally {
            needStop = true
            println("Message Receiver Stop")
            try {
                input.close()
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}