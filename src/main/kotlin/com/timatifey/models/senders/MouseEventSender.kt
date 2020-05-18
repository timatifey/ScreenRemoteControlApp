package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.Mouse
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class MouseEventSender(private val client: Socket): Runnable {

    @Volatile private var needStop = false
    private val queueMouse = LinkedBlockingQueue<Mouse>()

    fun putMouseEvent(mouse: Mouse) {
        queueMouse.put(mouse)
    }

    override fun run() {
        try {
            val output = PrintWriter(client.getOutputStream(), true)
                while (!needStop) {
                    val mouse = queueMouse.take()
                    val json = Gson().toJson(mouse)
                    output.println(json)
                }
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Mouse event Sender Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}
