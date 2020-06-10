package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.ImageSize
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketException
import javax.imageio.ImageIO

fun takeScreenSize(): Dimension = Toolkit.getDefaultToolkit().screenSize
private fun takeRectangle(screenSize: Dimension) = Rectangle(screenSize)
private fun takeScreen(rectangle: Rectangle): BufferedImage = Robot().createScreenCapture(rectangle)

class ScreenSender(private val socket: Socket): Runnable {
    @Volatile var needStop = false

    override fun run() {
        try {
            val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            println("Screen Sender has started")

            val screenSize = takeScreenSize()
            val rectangle = takeRectangle(screenSize)

            //Sending screen size
            val dataScreen = DataPackage(
                DataPackage.DataType.IMAGE_SIZE,
                imageSize = ImageSize(screenSize.getHeight(), screenSize.getWidth())
            )
            val jsonScreen = Gson().toJson(dataScreen)
            output.println(jsonScreen)

            //Sending Screen image
            val outScreen = ObjectOutputStream(socket.getOutputStream())
            while (!needStop) {
                val screen = takeScreen(rectangle)
                ImageIO.write(screen, "jpg", outScreen)
                outScreen.flush()
            }
            output.close()
            outScreen.close()
            socket.close()
        } catch (e: IOException) {
            println("Screen Sender Client Socket Error: $e")
        } catch (e: SocketException) {
        } finally {
            needStop = true
            println("Screen Sender Stop")
        }
    }

    fun stop() { needStop = true }
}


