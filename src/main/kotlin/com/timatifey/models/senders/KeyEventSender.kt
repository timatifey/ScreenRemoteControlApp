package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.Key
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
            while (!needStop) {
                val key = queueKey.take()
                val json = Gson().toJson(key)
                output.println(json)
            }
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Key event Sender Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}
