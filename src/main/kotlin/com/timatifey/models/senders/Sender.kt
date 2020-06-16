package com.timatifey.models.senders

import com.timatifey.models.client.id
import com.timatifey.models.data.Data
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Message
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue

abstract class Sender<T> (private val output: ObjectOutputStream): Runnable {
    @Volatile private var needStop = false
    private val queue = LinkedBlockingQueue<T>()

    abstract val socketName: String

    fun putEvent(event: T) { queue.put(event) }

    private fun sendFirstMsg() {
        val firstMsg = DataPackage(Message("$id:$socketName"))
        output.writeObject(firstMsg)
        output.flush()
    }

    override fun run() {
        try {
            needStop = false
            sendFirstMsg()
            while (!needStop) {
                if (queue.isEmpty()) continue
                val obj = queue.take()
                val data = DataPackage(obj as Data)
                output.writeObject(data)
                output.flush()
            }
        } catch (e: IOException) {
            println("$socketName Client Socket Error: $e")
        } finally {
            needStop = true
            println("$socketName Sender Stop")
            try {
                output.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}
