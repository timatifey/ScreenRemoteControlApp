package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.Mouse
import javafx.scene.input.MouseEvent
import java.awt.AWTException
import java.awt.Robot
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
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
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            while (true) {
                val json = input.readLine()
                println(json)
                if (json != null) {
                    val mouse = Gson().fromJson(json, Mouse::class.java)
                    println("mouse $mouse")
                    move(mouse.x.toInt(), mouse.y.toInt())
                }
                sleep(10)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}