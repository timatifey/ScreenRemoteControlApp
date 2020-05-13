package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.Mouse
import javafx.scene.input.MouseEvent
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.net.Socket

class MouseEventSender(private val client: Socket): Runnable {
    var needSend = false
    lateinit var eventMouse: MouseEvent

//    private fun takeCords() {
//        val mousePoint = MouseInfo.getPointerInfo()
//        val x = mousePoint.location.x
//        val y = mousePoint.location.y
//    }

    fun setEvent(event: MouseEvent) {
        eventMouse = event
        needSend = true
    }

    override fun run() {
        try {
            val output = PrintWriter(client.getOutputStream(), true)
            while (true) {
                println("IN THREAD $needSend")
                if (needSend) {
                    val mouse = Mouse(eventMouse.screenX.toInt(), eventMouse.screenY.toInt())
                    val json = Gson().toJson(mouse)
                    println("mouse $json")
                    output.println(json)
                    needSend = false
                }
                sleep(200)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Sender Client Socket Error: $e")
        }
    }
}
