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
    @Volatile var needStop = false

    override fun run() {
        try {
            needStop = false
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            while (!needStop) {
                synchronized(this) {
                    try {
                        val json = input.readLine()
                        if (json != null) {
                            val data = Gson().fromJson(json, DataPackage::class.java)
                            if (data.dataType == DataPackage.DataType.IMAGE) {
                                val image = ImageIO.read(ByteArrayInputStream(data.image!!.bytes))
                                if (image != null) {
                                    imageScene.value = SwingFXUtils.toFXImage(image, null)
                                }
                            }
                        }
                    } catch (e: IOException) {
                        println(e.message)
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