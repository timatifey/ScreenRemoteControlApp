package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.*
import java.net.Socket
import java.net.SocketException
import javax.imageio.ImageIO

class ScreenReceiver(private val socket: Socket): Runnable, Receiver {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0
    @Volatile var needStop = false

    override fun run() {
        try {
            needStop = false
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            val firstMsg = Gson().toJson(
                DataPackage(
                    DataPackage.DataType.MESSAGE,
                    message = "${id}:SCREEN_SOCKET"
                )
            )
            output.println(firstMsg)

            while (!needStop) {
                val json = input.readLine()
                if (json != null) {
                    try {
                        val data = Gson().fromJson(json, DataPackage::class.java)
                        if (data.dataType == DataPackage.DataType.IMAGE) {
                            val image = ImageIO.read(ByteArrayInputStream(data.image!!.bytes))
                            if (image != null) {
                                height = data.image.height.toDouble()
                                width = data.image.width.toDouble()
                                imageScene.value = SwingFXUtils.toFXImage(image, null)
                                val msg = Gson().toJson(
                                    DataPackage(
                                        DataPackage.DataType.MESSAGE,
                                        message = "${id}:IMAGE_OK"
                                    )
                                )
                                output.println(msg)
                            }
                        }
                    } catch (e: EOFException) {
                        println("Screen receiver: ${e.message}")
                    } catch (e: IllegalStateException) {
                        println("Screen receiver: ${e.message}")
                    } catch (e: SocketException) {
                        needStop = true
                    }
                }
            }
            output.close()
            input.close()
            socket.close()
        } catch (e: IOException) {
            println("Screen Receiver Client Socket Error: $e")
        } finally {
            needStop = true
        }
    }

    fun stop() { needStop = true }
}