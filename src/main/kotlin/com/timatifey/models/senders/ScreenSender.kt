package com.timatifey.models.senders

import com.google.gson.Gson
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.net.Socket
import java.nio.ByteBuffer
import javax.imageio.ImageIO


class ScreenSender(private val client: Socket): Runnable {
    @Volatile var needStop = false

    private fun takeScreenSize(): Dimension = Toolkit.getDefaultToolkit().screenSize

    private fun takeRectangle(screenSize: Dimension) = Rectangle(screenSize)

    private fun takeScreen(rectangle: Rectangle) = Robot().createScreenCapture(rectangle)

    override fun run() {
        try {
            needStop = false
            val outputStream = client.getOutputStream()
            while (!needStop) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val screenSize = takeScreenSize()
                val rectangle = takeRectangle(screenSize)
                val screen = takeScreen(rectangle)
                ImageIO.write(screen, "png", byteArrayOutputStream)

                val size: ByteArray = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array()
                outputStream.write(size)
                val json = Gson().toJson(screen)
                println(json)
                println(Gson().toJson(byteArrayOutputStream))
                outputStream.write(byteArrayOutputStream.toByteArray())
                outputStream.flush()
                sleep(200)
            }
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Screen Sender Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}
