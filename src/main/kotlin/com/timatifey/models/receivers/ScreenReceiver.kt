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
            //val input = BufferedReader(InputStreamReader(client.getInputStream()))
            while (!needStop) {
                synchronized(this) {
//                    val json = input.readLine()
//                    if (json != null) {
//                        val data = Gson().fromJson(json, DataPackage::class.java)
//                        if (data.dataType == DataPackage.DataType.IMAGE) {
//                            println(data.dataObject)
//                            val image = ImageIO.read(ByteArrayInputStream(
//                                    (data.dataObject as com.timatifey.models.data.Image).bytes
//                            ))
                    try {
                        val image = ImageIO.read(client.getInputStream())
                        if (image != null) {
                            imageScene.value = SwingFXUtils.toFXImage(image, null)
                        }
                    } catch (e: IOException) {
                        println(e.message)
                        e.printStackTrace()
                    }
                }
            }
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