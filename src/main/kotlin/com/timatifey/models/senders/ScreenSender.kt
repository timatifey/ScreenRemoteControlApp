package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Image
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintWriter
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
            //val output = PrintWriter(client.getOutputStream(), true)
            while (!needStop) {
//                val byteArrayOutputStream = ByteArrayOutputStream()
                val screenSize = takeScreenSize()
                val rectangle = takeRectangle(screenSize)
                val screen = takeScreen(rectangle)
//                ImageIO.write(screen, "png", byteArrayOutputStream)
//                val byteArray = byteArrayOutputStream.toByteArray()
//
//                val image = Image(screen.height, screen.width, byteArray)
//                val data = DataPackage(DataPackage.DataType.IMAGE, image)
//                val json = Gson().toJson(data)
//                println(json)
//                output.println(json)
                try {
                    ImageIO.write(screen, "png", client.getOutputStream())
                } catch (e: IOException) {
                    println(e.message)
                    e.printStackTrace()
                }
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
