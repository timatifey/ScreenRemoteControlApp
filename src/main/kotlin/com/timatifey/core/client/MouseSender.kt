package com.timatifey.core.client

import com.google.gson.Gson
import com.timatifey.core.Mouse
import java.awt.MouseInfo
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.concurrent.TimeUnit

object MouseSender: Client() {

    private fun takeCords(): Mouse {
        val mousePoint = MouseInfo.getPointerInfo()
        val x = mousePoint.location.x
        val y = mousePoint.location.y
        return Mouse(x, y)
    }

    override fun run() {
        try {
            output = PrintWriter(clientSocket.getOutputStream(), true)

            if (clientSocket.isConnected && !clientSocket.isClosed) println("CLIENT CONNECTED")

            while (clientSocket.isConnected && !clientSocket.isClosed) {
                val info = takeCords()
                output.println(Gson().toJson(info))
                if (info == Mouse(0, 0)) {
                    println("DISCONNECT")
                    break
                }
                TimeUnit.MILLISECONDS.sleep(500)
            }

            stopConnection()

        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Sender Client Socket Error: $e")
        }
    }
}

fun main() {
    MouseSender.startConnection("localhost", 6666)
}