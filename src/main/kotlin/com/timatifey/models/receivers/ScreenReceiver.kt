package com.timatifey.models.receivers

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import javax.imageio.ImageIO

class ScreenReceiver(private val client: Socket): Runnable {
    val image = SimpleObjectProperty<Image?>()

    override fun run() {
        try {
            while (true) {
                synchronized(this) {
                    val image = ImageIO.read(client.getInputStream())
                    this.image.value = SwingFXUtils.toFXImage(image, null)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Cursor Controller Client Socket Error: $e")
        }
    }
}