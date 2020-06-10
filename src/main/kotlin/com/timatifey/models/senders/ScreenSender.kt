package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Image
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

            val byteArrayOutputStream = ByteArrayOutputStream()
            val screenSize = takeScreenSize()
            val rectangle = takeRectangle(screenSize)
            lateinit var screen: BufferedImage

            while (!needStop) {
                screen = takeScreen(rectangle)
                ImageIO.write(screen, "png", byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
//                val image = Image(screen.height, screen.width, byteArray)
                val image = Image(byteArray)
                val data = DataPackage(DataPackage.DataType.IMAGE, image = image)
                val json = Gson().toJson(data)
                output.println(json)
                sleep(200)
            }
            output.close()
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


