package com.timatifey.core.server

import java.awt.AWTException
import java.awt.Robot
import com.google.gson.Gson
import com.timatifey.core.Mouse

object CursorController {
    var info: Any? = null
    fun move(xCoord: Int, yCoord: Int) {
        try {
            val robot = Robot()
            robot.mouseMove(xCoord, yCoord)
        } catch (e: AWTException) {
        }
    }

    fun start() {
        val server = Server()
        server.start(6666)
        while (true) {
            val json = info.toString()
            println(json)
            val mouse = Gson().fromJson(json, Mouse::class.java)
            println("${mouse.x} ${mouse.y}")
        }
    }

    fun getter(newInfo: Any) {
        info = newInfo
    }
}

fun main() {
    CursorController.start()
}