package com.timatifey.models.client

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MouseEventSender
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import tornadofx.runLater
import java.io.*
import java.net.Socket
import java.net.SocketException
import javax.imageio.ImageIO

class Client: Runnable {
    private lateinit var socket: Socket
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter

    private var wasInit = false
    val gson = Gson()

    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set
    lateinit var keyEventSender: KeyEventSender private set

    val status = SimpleStringProperty("")

    fun startConnection(ip: String, port: Int, dataTypesList: List<DataPackage.DataType>): Boolean {
        try {
            socket = Socket(ip, port)

            println("Client connected to $ip:$port")
            runLater { status.value = "Client connected" }

            input = BufferedReader(InputStreamReader(socket.getInputStream()))
            output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)

            //Confirmation types
            val msg = dataTypesList.joinToString(separator = ", ")
            output.println(gson.toJson(DataPackage(DataPackage.DataType.MESSAGE, message = msg)))

            //Starting threads
            if (DataPackage.DataType.IMAGE in dataTypesList) {
                screenReceiver = ScreenReceiver(socket)
                Thread(screenReceiver).start()
            }

            if (DataPackage.DataType.MOUSE in dataTypesList) {
                mouseEventSender = MouseEventSender(socket)
                Thread(mouseEventSender).start()
            }

            if (DataPackage.DataType.KEY in dataTypesList) {
                keyEventSender = KeyEventSender(socket)
                Thread(keyEventSender).start()
            }

        } catch (e: IOException) {
            return false
        }

        Thread(this).start()
        wasInit = true
        return true
    }

    override fun run() {
        try {
            while (true) {
                //Waiting of message
                val json = input.readLine()
                if (json != null) {
                    val data = Gson().fromJson(json, DataPackage::class.java)

                    if (data.dataType == DataPackage.DataType.MESSAGE) {
                        val text = data.message!!

                        if (text.equals("stop", ignoreCase = true)) {
                            println("Server has stopped connection")
                            runLater { status.value = "Server has stopped connection" }

                            if (this::screenReceiver.isInitialized)
                                setShutdownImage()

                            stopConnection()
                            break
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            println(e.message)
        }
    }

    fun stopConnection() {
        try {
            if (wasInit) {
                if (this::mouseEventSender.isInitialized)
                    mouseEventSender.stop()
                if (this::keyEventSender.isInitialized)
                    keyEventSender.stop()
                if (this::screenReceiver.isInitialized)
                    screenReceiver.stop()

                try {
                    output.println(gson.toJson(DataPackage(DataPackage.DataType.MESSAGE, message = "stop")))
                    output.close()
                    input.close()
                } finally {
                    try { socket.close() } catch (e: SocketException) { println(e.message) }
                }
            }
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }

    private fun setShutdownImage() {
        screenReceiver.imageScene.value =
            SwingFXUtils.toFXImage(ImageIO.read(
                File("server_shutdown.jpg")), null)
    }
}