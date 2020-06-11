package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mouse
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue

class MouseEventSender(private val socket: Socket): Runnable {
    @Volatile private var needStop = false
    private val queueMouse = LinkedBlockingQueue<Mouse>()
    private lateinit var output: PrintWriter

    fun putMouseEvent(mouse: Mouse) { queueMouse.put(mouse) }

    override fun run() {
        try {
            //output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            val outObject = ObjectOutputStream(socket.getOutputStream())
            needStop = false

            //First message
            val firstMsg = DataPackage(DataPackage.DataType.MESSAGE,
                message = "$id:MOUSE_SOCKET")
            outObject.writeObject(firstMsg)
            outObject.flush()
            //output.println(firstMsg)
            println("Mouse Event Sender Start")

            while (!needStop) {
                if (queueMouse.isEmpty()) continue
                val mouse = queueMouse.take()
                val data = DataPackage(DataPackage.DataType.MOUSE, mouse = mouse)
                val json = Gson().toJson(data)
                //output.println(json)
                outObject.writeObject(data)
                outObject.flush()
            }
        } catch (e: IOException) {
            println("Mouse Event Sender Client Socket Error: $e")
        } finally {
            needStop = true
            println("Mouse Event Sender Stop")
            try {
                output.close()
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}
