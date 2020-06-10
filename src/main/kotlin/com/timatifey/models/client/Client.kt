package com.timatifey.models.client

import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MessageSender
import com.timatifey.models.senders.MouseEventSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.runLater
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import kotlin.random.Random

val id = generateId()

const val idLength = 32
fun generateId(): String {
    val random by lazy { Random }
    return (1..idLength).map { random.nextInt('A'.toInt(), 'Z'.toInt()).toChar() }.joinToString(separator = "")
}

class Client {
    private lateinit var socketMessage: Socket
    private lateinit var socketScreen: Socket
    private lateinit var socketMouse: Socket
    private lateinit var socketKey: Socket

    private var wasInit = false

    lateinit var messageSender: MessageSender private set
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set
    lateinit var keyEventSender: KeyEventSender private set

    val status = SimpleStringProperty("")

    fun startConnection(ip: String, port: Int, dataTypesList: List<DataPackage.DataType>): Boolean {
        try {
            socketMessage = Socket(ip, port)
            messageSender = MessageSender(socketMessage)
            Thread(messageSender).start()

            socketScreen = Socket(ip, port)
            screenReceiver = ScreenReceiver(socketScreen)
            Thread(screenReceiver).start()

            if (DataPackage.DataType.MOUSE in dataTypesList) {
                socketMouse = Socket(ip, port)
                mouseEventSender = MouseEventSender(socketMouse)
                Thread(mouseEventSender).start()
            }

            if (DataPackage.DataType.KEY in dataTypesList) {
                socketKey = Socket(ip, port)
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
                try {
                    if (this::messageSender.isInitialized) {
                        messageSender.sendMessage("$id:STOP")
                        messageSender.stop()
                    }
                    if (this::socketMessage.isInitialized)
                        socketMessage.close()

                    if (this::mouseEventSender.isInitialized)
                        mouseEventSender.stop()
                    if (this::keyEventSender.isInitialized)
                        keyEventSender.stop()
                    if (this::screenReceiver.isInitialized)
                        screenReceiver.stop()
                    if (this::messageSender.isInitialized)
                        messageSender.stop()

                    if (this::socketKey.isInitialized)
                        socketKey.close()
                    if (this::socketMouse.isInitialized)
                        socketMouse.close()
                    if (this::socketScreen.isInitialized)
                        socketScreen.close()
                    if (this::socketMessage.isInitialized)
                        socketMessage.close()
                } catch (e: SocketException) { println(e.message) }
                wasInit = false
            }
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }

}