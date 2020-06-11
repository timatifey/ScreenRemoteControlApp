package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Key
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue

class KeyEventSender(private val socket: Socket): Runnable {
    @Volatile private var needStop = false
    private val queueKey = LinkedBlockingQueue<Key>()
    private val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
    fun putKeyEvent(key: Key) {
        queueKey.put(key)
    }

    override fun run() {
        try {
            needStop = false

            //First message
            val firstMsg = Gson().toJson(DataPackage(DataPackage.DataType.MESSAGE,
                message = "$id:KEY_SOCKET"))
            output.println(firstMsg)

            println("Key Event Sender Start")

            while (!needStop) {
                if (queueKey.isEmpty()) continue
                val key = queueKey.take()
                val data = DataPackage(DataPackage.DataType.KEY, key = key)
                val json = Gson().toJson(data)
                output.println(json)
            }
        } catch (e: IOException) {
            println("Key Event Sender Client Socket Error: $e")
        } finally {
            needStop = true
            println("Key Event Sender Stop")
            try {
                output.close()
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}
