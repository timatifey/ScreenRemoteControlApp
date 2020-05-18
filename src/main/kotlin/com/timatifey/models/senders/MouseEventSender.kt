package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.Mouse
import javafx.scene.input.MouseEvent
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

class MouseEventSender(private val client: Socket): Runnable {

    @Volatile private var needStop = false
    private val que = LinkedBlockingQueue<Mouse>()

    fun setEvent(eventMouse: MouseEvent) {
        val mouse = Mouse(
                eventMouse.eventType.name,
                eventMouse.x,
                eventMouse.y,
                eventMouse.screenX,
                eventMouse.screenY,
                eventMouse.button,
                eventMouse.clickCount,
                eventMouse.isShiftDown,
                eventMouse.isControlDown,
                eventMouse.isAltDown,
                eventMouse.isMetaDown,
                eventMouse.isPrimaryButtonDown,
                eventMouse.isMiddleButtonDown,
                eventMouse.isPopupTrigger,
                eventMouse.isStillSincePress,
                eventMouse.isSecondaryButtonDown
        )
        que.put(mouse)
        println("size: ${que.size}")
    }

    override fun run() {
        try {
            val output = PrintWriter(client.getOutputStream(), true)
                while (!needStop) {
                    println("in thread ${que.size}")
                    val mouse = que.take()
                    val json = Gson().toJson(mouse)
                    println("json $json")
                    output.println(json)
                }
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Mouse event Sender Client Socket Error: $e")
        }
    }

    fun stop() {
        println("STOP")
        needStop = true
    }
}
