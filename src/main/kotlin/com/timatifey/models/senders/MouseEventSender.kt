package com.timatifey.models.senders

import java.awt.MouseInfo
import java.awt.event.MouseEvent
import java.io.IOException
import java.net.Socket

class MouseEventSender(private val client: Socket): Runnable {

    override fun run() {
        try {

        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Sender Client Socket Error: $e")
        }
    }
}
