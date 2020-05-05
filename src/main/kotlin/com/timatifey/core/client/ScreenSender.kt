package com.timatifey.core.client

import java.awt.*
import java.awt.image.BufferedImage
import java.io.*
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

object ScreenSender: Client() {
    var needStop: Boolean = false

    private fun takeScreen(): BufferedImage {
        return Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
    }

    override fun run() {
        try {
            if (clientSocket.isConnected && !clientSocket.isClosed) println("CLIENT CONNECTED")
            var prevImage: BufferedImage = takeScreen()
            while (clientSocket.isConnected && !clientSocket.isClosed) {
                val image = takeScreen()
                if (prevImage != image)
                ImageIO.write(image, "png", clientSocket.getOutputStream())
                if (needStop) {
                    println("DISCONNECT")
                    break
                }
                prevImage = image
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
    ScreenSender.startConnection("localhost", 6666)
}