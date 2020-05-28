package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mouse
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class MouseEventSender(private val socket: Socket): Runnable {

    @Volatile private var needStop = false
    private val queueMouse = LinkedBlockingQueue<Mouse>()

    fun putMouseEvent(mouse: Mouse) { queueMouse.put(mouse) }

    override fun run() {
        try {
            val output = PrintWriter(socket.getOutputStream(), true)
            println("Mouse has connected")
            val firstMsg = Gson().toJson(DataPackage(DataPackage.DataType.MESSAGE,
                message = "$id:MOUSE_SOCKET"))
            output.println(firstMsg)

            while (!needStop) {
                val mouse = queueMouse.take()
                val data = DataPackage(DataPackage.DataType.MOUSE, mouse = mouse)
                val json = Gson().toJson(data)
                output.println(json)
            }
            output.close()
            socket.close()
        } catch (e: IOException) {
            println("Mouse Event Sender Client Socket Error: $e")
        }
    }

    fun stop() { needStop = true }
}
