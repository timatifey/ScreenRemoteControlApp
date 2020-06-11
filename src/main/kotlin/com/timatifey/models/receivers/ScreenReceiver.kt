package com.timatifey.models.receivers

import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException

class ScreenReceiver(private val socket: Socket): Runnable {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0
    @Volatile var needStop = false
    private lateinit var input: ObjectInputStream
    private lateinit var output: ObjectOutputStream

    override fun run() {
        try {
            output = ObjectOutputStream(socket.getOutputStream())
            needStop = false
            //First message
            val firstMsg =
                DataPackage(
                    DataPackage.DataType.MESSAGE,
                    message = "${id}:SCREEN_SOCKET"
                )
            output.writeObject(firstMsg)
            output.flush()

            println("Send first msg")
            //Get screen size
            val data = input.readObject() as DataPackage
            println(data)
            if (data != null) {
                if (data.dataType == DataPackage.DataType.IMAGE_SIZE) {
                    height = data.imageSize!!.height
                    width = data.imageSize.width
                }
            } else {
                needStop = true
            }

            println("Screen receiver start")
            input = ObjectInputStream(socket.getInputStream())
            //Get screen image
            while (!needStop) {
                imageScene.value = Image(input, width, height, false, false)
            }
        } catch (e: IOException) {
            println("Screen Receiver Client Socket Error: $e")
            e.printStackTrace()
        } finally {
            needStop = true
            println("Screen Receiver Stop")
            setNullImage()
            try {
                input.close()
                output.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }

    private fun setNullImage() { imageScene.value = null }
}