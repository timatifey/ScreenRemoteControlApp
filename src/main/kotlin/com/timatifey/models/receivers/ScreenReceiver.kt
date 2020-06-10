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

class ScreenReceiver(private val socket: Socket): Runnable {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0
    @Volatile var needStop = false

    private val maxTryingReconnect = 5
    var countTryingReconnect = maxTryingReconnect

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
            println("Sended first msg")
            val json = input.readLine()
            if (json != null) {
                val data = Gson().fromJson(json, DataPackage::class.java)
                if (data.dataType == DataPackage.DataType.IMAGE_SIZE) {
                    height = data.imageSize!!.height
                    width = data.imageSize!!.width
                }
            }
            println("Has sizes: $height, $width")
            output.close()
            input.close()

            val inObjStream = ObjectInputStream(socket.getInputStream())
            println("Opened stream")
            while (!needStop) {
//                val json = input.readLine()
//                println(json)
//                if (json != null) {
//                    try {
//                        if (countTryingReconnect < maxTryingReconnect)
//                            countTryingReconnect = maxTryingReconnect
//                        val data = Gson().fromJson(json, DataPackage::class.java)
//                        if (data.dataType == DataPackage.DataType.IMAGE) {
//                            val image = ImageIO.read(ByteArrayInputStream(data.image!!))
//                            if (image != null) {
//                                val sceneImage = SwingFXUtils.toFXImage(image, null)
//                                imageScene.value = sceneImage
//                                height = sceneImage.height
//                                width = sceneImage.width
//                            }
//                        }
//                    } catch (e: EOFException) {
//                        println("Screen receiver: ${e.message}")
//                    } catch (e: IllegalStateException) {
//                        println("Screen receiver: ${e.message}")
//                        setNullImage()
//                    } catch (e: SocketException) {
//                    } finally {
//                        countTryingReconnect--
//                        if (countTryingReconnect == 0)
//                            needStop = true
//                    }
//                }
                imageScene.value = Image(inObjStream, width, height, false, false)
            }
            inObjStream.close()
            socket.close()
        } catch (e: IOException) {
            println("Screen Receiver Client Socket Error: $e")
        } finally {
            needStop = true
            setNullImage()
        }
    }

    fun stop() { needStop = true }

    private fun setNullImage() {
        imageScene.value = null
    }
}