package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.*
import java.net.Socket
import javax.imageio.ImageIO

class ScreenReceiver(private val client: Socket): Runnable {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0
    @Volatile var needStop = false

    override fun run() {
        try {
            needStop = false
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            while (!needStop) {
                if (!client.isConnected || client.isClosed) {
                    needStop = true
                    break
                }
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
                            }
                        }
                    } catch (e: EOFException) {
                        println(e.message)
                        stop()
                    }
                }
            }
            input.close()
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Screen Receiver Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}