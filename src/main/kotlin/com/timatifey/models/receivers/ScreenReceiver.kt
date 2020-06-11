package com.timatifey.models.receivers

import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketException

class ScreenReceiver(private val socket: Socket): Runnable {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0

    @Volatile var needStop = false

    private lateinit var input: ObjectInputStream
    private lateinit var output: ObjectOutputStream

    private val maxNumReconnection = 5
    private var currentCountReconnection = maxNumReconnection

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
            input = ObjectInputStream(socket.getInputStream())
            try {
                val data = input.readObject() as DataPackage
                if (data.dataType == DataPackage.DataType.IMAGE_SIZE) {
                    height = data.imageSize!!.height
                    width = data.imageSize.width
                }
            } catch (e: EOFException) {
                needStop = true
            } catch (e: SocketException) {
                needStop = true
            }
            println("Screen receiver start")
            //Get screen image
            while (!needStop) {
                imageScene.value = Image(input, width, height, false, false)
                if (imageScene.value == null) {
                    currentCountReconnection--
                    sleep(200)
                } else currentCountReconnection = maxNumReconnection
                if (currentCountReconnection == 0) {
                    needStop = true
                }
            }
        } catch (e: IOException) {
            println("Screen Receiver Client Socket Error: $e")
            e.printStackTrace()
        } finally {
            needStop = true
            println("Screen Receiver Stop")
            setNullImage()
            try {
                if (this::input.isInitialized)
                    input.close()
                if (this::output.isInitialized)
                    output.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }

    private fun setNullImage() { imageScene.value = null }
}