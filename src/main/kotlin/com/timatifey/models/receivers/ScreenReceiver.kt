package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.image.Image
import java.io.*
import java.net.Socket
import java.net.SocketException

class ScreenReceiver(private val socket: Socket): Runnable {
    val imageScene = SimpleObjectProperty<Image?>()
    @Volatile var height: Double = 0.0
    @Volatile var width: Double = 0.0
    @Volatile var needStop = false
//    private val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    private val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
    private val inObjStream = ObjectInputStream(socket.getInputStream())

    override fun run() {
        try {
            needStop = false

            //First message
            val firstMsg = Gson().toJson(
                DataPackage(
                    DataPackage.DataType.MESSAGE,
                    message = "${id}:SCREEN_SOCKET"
                )
            )
            output.println(firstMsg)

            //Get screen size
//            val json = input.readLine()
            val data = inObjStream.readObject() as DataPackage
            println(data)
            if (data != null) {
//                val data = Gson().fromJson(json, DataPackage::class.java)
                if (data.dataType == DataPackage.DataType.IMAGE_SIZE) {
                    height = data.imageSize!!.height
                    width = data.imageSize.width
                }
            } else {
                needStop = true
            }

            println("Screen receiver start")

            //Get screen image
            while (!needStop) {
                imageScene.value = Image(inObjStream, width, height, false, false)
            }
        } catch (e: IOException) {
            println("Screen Receiver Client Socket Error: $e")
            e.printStackTrace()
        } finally {
            needStop = true
            println("Screen Receiver Stop")
            setNullImage()
            try {
                inObjStream.close()
                output.close()
//                input.close()
                socket.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }

    private fun setNullImage() { imageScene.value = null }
}