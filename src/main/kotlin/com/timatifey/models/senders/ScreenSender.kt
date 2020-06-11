package com.timatifey.models.senders

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.ImageSize
import java.awt.Dimension
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.ObjectOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketException
import javax.imageio.ImageIO

fun takeScreenSize(): Dimension = Toolkit.getDefaultToolkit().screenSize
private fun takeRectangle(screenSize: Dimension) = Rectangle(screenSize)
private fun takeScreen(rectangle: Rectangle): BufferedImage = Robot().createScreenCapture(rectangle)

class ScreenSender(private val socket: Socket): Runnable {
    @Volatile var needStop = false
    private lateinit var output: ObjectOutputStream

    override fun run() {
        try {
            output = ObjectOutputStream(socket.getOutputStream())
            needStop = false

            val screenSize = takeScreenSize()
            val rectangle = takeRectangle(screenSize)

            //Sending screen size
            val dataScreen = DataPackage(
                DataPackage.DataType.IMAGE_SIZE,
                imageSize = ImageSize(screenSize.getHeight(), screenSize.getWidth())
            )
            output.writeObject(dataScreen)
            output.flush()

            println("Screen Sender has started")

            //Sending Screen image
            var previousImage: BufferedImage? = null
            while (!needStop) {
                val screen = takeScreen(rectangle)
                if (previousImage == null || !compareImages(screen, previousImage)) {
                    ImageIO.write(screen, "jpg", output)
                    output.flush()
                    sleep(100)
                }
                previousImage = screen
            }
        } catch (e: IOException) {
            println("Screen Sender Client Socket Error: $e")
        } catch (e: SocketException) {
        } finally {
            needStop = true
            println("Screen Sender Stop")
            try {
                output.close()
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }

    private fun compareImages(imgA: BufferedImage, imgB: BufferedImage): Boolean {
        val width = imgA.width
        val height = imgA.height
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false
                }
            }
        }
        return true
    }
}


