package com.timatifey.models.receivers

import com.timatifey.models.data.DataPackage
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.net.SocketException

class MessageReceiver (private val input: ObjectInputStream): Runnable {
    @Volatile var needStop = false

    override fun run() {
        try {
            needStop = false
            println("Message Receiver Start")
            while (!needStop) {
                try {
                    val data = input.readObject() as DataPackage
                    if (data.dataType == DataPackage.DataType.MESSAGE) {
                        val text = data.message!!.split(":")
                        if (text[1] == "STOP") {
                            needStop = true
                        }
                    }
                } catch (e: EOFException) {
                    needStop = true
                } catch (e: SocketException) {
                    needStop = true
                }
            }
        } catch (e: IOException) {
            println("Message Receiver Socket Error: $e")
        } finally {
            needStop = true
            println("Message Receiver Stop")
            try {
                input.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}