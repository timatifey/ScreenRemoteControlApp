package com.timatifey.core.server

import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO

object ScreenReceiver: Server() {
    var needStop: Boolean = false

    fun createScreenFile(image: BufferedImage) {
        ImageIO.write(image, "png", File ("screenshot.png"))
    }

    override fun run() {
        try {
            var image = ImageIO.read(clientSocket.getInputStream())
            println(image)
            while (clientSocket.isConnected && !clientSocket.isClosed) {
                if (needStop) {
                    println("DISCONNECT")
                    output.println("DISCONNECT")
                    output.flush()
                    break
                }
                if (image != null) createScreenFile(image)
                image = ImageIO.read(clientSocket.getInputStream())
            }

            stop()

        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}

fun main() {
    ScreenReceiver.start(6666)
}