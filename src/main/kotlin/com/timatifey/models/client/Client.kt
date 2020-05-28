package com.timatifey.models.client

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MessageSender
import com.timatifey.models.senders.MouseEventSender
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import tornadofx.runLater
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketException
import javax.imageio.ImageIO
import kotlin.random.Random

val id = generateId()

const val idLength = 32
fun generateId(): String {
    val random by lazy { Random }
    return (1..idLength).map { random.nextInt('A'.toInt(), 'Z'.toInt()).toChar() }.joinToString(separator = "")
}

class Client {
    private lateinit var socket: Socket
    private lateinit var socketScreen: Socket
    private lateinit var socketMouse: Socket
    private lateinit var socketKey: Socket

    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter

    private var wasInit = false
    private val gson = Gson()

    lateinit var messageReceiver: MessageReceiver private set
    lateinit var messageSender: MessageSender private set
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set
    lateinit var keyEventSender: KeyEventSender private set

    val status = SimpleStringProperty("")

    fun startConnection(ip: String, port: Int, dataTypesList: List<DataPackage.DataType>): Boolean {
        try {
            socket = Socket(ip, port)

            input = BufferedReader(InputStreamReader(socket.getInputStream()))
            output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
            val firstMsg = gson.toJson(DataPackage(DataPackage.DataType.MESSAGE,
                message = "$id:MESSAGE_SOCKET"))
            output.println(firstMsg)
            output.close()

            //Starting threads
            messageReceiver = MessageReceiver(socket)
            messageSender = MessageSender(socket)
            Thread(messageReceiver).start()
            Thread(messageSender).start()

            socketScreen = Socket(ip, port)
            sleep(5000)
            screenReceiver = ScreenReceiver(socketScreen)
            Thread(screenReceiver).start()

            if (DataPackage.DataType.MOUSE in dataTypesList) {
                socketMouse = Socket(ip, port)
                sleep(5000)
                mouseEventSender = MouseEventSender(socketMouse)
                Thread(mouseEventSender).start()
            }

            if (DataPackage.DataType.KEY in dataTypesList) {
                socketKey = Socket(ip, port)
                sleep(5000)
                keyEventSender = KeyEventSender(socketKey)
                Thread(keyEventSender).start()
            }

        } catch (e: IOException) {
            return false
        }

        wasInit = true
        println("Client connected to $ip:$port")
        runLater { status.value = "Client connected" }
        return true
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
                    if (this::messageSender.isInitialized) {
                        messageSender.sendMessage("$id:stop")
                        messageSender.stop()
                    }
                    if (this::messageReceiver.isInitialized)
                        messageReceiver.stop()
                } finally {
                    try {
                        if (this::socket.isInitialized)
                            socket.close()
                        if (this::socketKey.isInitialized)
                            socketKey.close()
                        if (this::socketMouse.isInitialized)
                            socketMouse.close()
                        if (this::socketScreen.isInitialized)
                            socketScreen.close()
                    } catch (e: SocketException) { println(e.message) }
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