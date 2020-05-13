package com.timatifey.models.senders

import java.awt.*
import java.awt.image.BufferedImage
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import javax.imageio.ImageIO

class ScreenSender(private val client: Socket): Runnable {

    private fun takeScreen(): BufferedImage {
        return Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
    }

    override fun run() {
        try {
            while (true) {
                ImageIO.write(takeScreen(), "png", client.getOutputStream())
                sleep(200)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Sender Client Socket Error: $e")
        }
    }
}
