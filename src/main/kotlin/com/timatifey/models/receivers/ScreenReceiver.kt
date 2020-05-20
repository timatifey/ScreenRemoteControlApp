package com.timatifey.models.receivers

import javafx.beans.property.SimpleObjectProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.nio.ByteBuffer
import javax.imageio.ImageIO


class ScreenReceiver(private val client: Socket): Runnable {
    val image = SimpleObjectProperty<Image?>()
    @Volatile var needStop = false

    override fun run() {
        try {
            needStop = false
            val inputStream: InputStream = client.getInputStream()
            while (!needStop) {
                synchronized(this) {
                    val sizeAr = ByteArray(4)
                    inputStream.read(sizeAr)
                    val size = ByteBuffer.wrap(sizeAr).asIntBuffer().get()
                    val imageAr = ByteArray(size)
                    inputStream.read(imageAr)
                    val image = ImageIO.read(ByteArrayInputStream(imageAr))
                    if (image != null)
                        this.image.value = SwingFXUtils.toFXImage(image, null)
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