package com.timatifey.models.receivers

import java.awt.AWTException
import java.awt.Robot
import java.io.IOException
import java.lang.Thread.sleep
import java.net.Socket

class MouseEventReceiver(private val client: Socket): Runnable {

    fun move(x: Int, y: Int) {
        try {
            val robot = Robot()
            robot.mouseMove(x, y)
        } catch (e: AWTException) {
            println(e.message)
        }
    }

    override fun run() {
        try {

        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}