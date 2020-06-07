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

class ScreenSender(private val socket: Socket): Runnable, Sender {
    @Volatile var needStop = false

    override fun run() {
        try {
            val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            println("Screen Sender has started")
            while (!needStop) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val screenSize = takeScreenSize()
                val rectangle = takeRectangle(screenSize)
                val screen = takeScreen(rectangle)
                ImageIO.write(screen, "png", byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                val image = Image(screen.height, screen.width, byteArray)
                val data = DataPackage(DataPackage.DataType.IMAGE, image = image)
                val json = Gson().toJson(data)
                output.println(json)
                sleep(300)

                val inJson = input.readLine()
                if (inJson != null) {
                    val inData = Gson().fromJson(inJson, DataPackage::class.java)
                    if (inData.dataType == DataPackage.DataType.MESSAGE) {
                        val text = inData.message!!.split(":")
                        if (text[1] == "IMAGE_OK") {
                            continue
                        } else {
                            needStop = true
                            break
                        }
                    }
                } else {
                    needStop = true
                    break
                }
            }
            output.close()
            socket.close()
            println("Screen Sender Stop")
            needStop = true
        } catch (e: IOException) {
            println("Screen Sender Client Socket Error: $e")
        } catch (e: SocketException) {
            needStop = true
        }
    }

    fun stop() { needStop = true }
}


