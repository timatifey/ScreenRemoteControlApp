package com.timatifey.core.server

import java.awt.AWTException
import java.awt.Robot
import com.google.gson.Gson
import com.timatifey.core.Mouse
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter

object MouseReceiver: Server() {

    fun move(xCoord: Int, yCoord: Int) {
        try {
            val robot = Robot()
            robot.mouseMove(xCoord, yCoord)
        } catch (e: AWTException) {
        }
    }

    override fun run() {
        try {
            input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
            output = PrintWriter(clientSocket.getOutputStream(), true)

            val gson = Gson()
            var inputJson = input.readLine()
            var mouse = gson.fromJson(inputJson, Mouse::class.java)
            while (clientSocket.isConnected && !clientSocket.isClosed) {
                if (mouse == Mouse(0, 0)) {
                    println("DISCONNECT")
                    output.println("DISCONNECT")
                    break
                }
                mouse = gson.fromJson(inputJson, Mouse::class.java)
                println("${mouse.x} ${mouse.y}")
                inputJson = input.readLine()
            }

            stop()

        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}

fun main() {
    MouseReceiver.start(6666)
}