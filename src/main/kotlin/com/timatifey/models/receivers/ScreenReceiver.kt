package com.timatifey.models.receivers

import java.awt.image.BufferedImage
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import javax.imageio.ImageIO

class ScreenReceiver(private val client: Socket): Runnable {
    private fun createScreenFile(image: BufferedImage) {
        ImageIO.write(image, "png", File ("src/main/resources/screen.png"))
    }

    override fun run() {
        try {
            while (true) {
                val image = ImageIO.read(client.getInputStream())
                if (image != null) {
                    createScreenFile(image)
                }
                sleep(200)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}