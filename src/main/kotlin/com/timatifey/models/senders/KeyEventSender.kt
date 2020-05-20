package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Key
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class KeyEventSender(private val client: Socket): Runnable {
    @Volatile private var needStop = false
    private val queueKey = LinkedBlockingQueue<Key>()

    fun putKeyEvent(key: Key) {
        queueKey.put(key)
    }

    override fun run() {
        try {
            val output = PrintWriter(client.getOutputStream(), true)
            needStop = false
            while (!needStop) {
                if (!client.isConnected || client.isClosed) {
                    needStop = true
                    break
                }
                val key = queueKey.take()
                val data = DataPackage(DataPackage.DataType.KEY, key = key)
                val json = Gson().toJson(data)
                output.println(json)
            }
            output.close()
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Key Event Sender Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}
