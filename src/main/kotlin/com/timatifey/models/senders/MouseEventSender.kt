package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.Mouse
import javafx.scene.input.MouseEvent
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.net.Socket

class MouseEventSender(private val client: Socket): Runnable {

    @Volatile private var needSend = false
    @Volatile private var needStop = false
    @Volatile private lateinit var mouse: Mouse

    fun setEvent(eventMouse: MouseEvent) {
        mouse = Mouse(
                eventMouse.eventType,
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
        needSend = true
        println("$needSend")
    }

    override fun run() {
        try {
            val output = PrintWriter(client.getOutputStream(), true)
                while (!needStop) {
                    println("in thread $needSend")
                    if (needSend) {
                        val json = Gson().toJson(mouse)
                        println("mouse $json")
                        output.println(mouse)
                        needSend = false
                    }
                    sleep(10)
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
