package com.timatifey.models.client

import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Message
import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MessageEventSender
import com.timatifey.models.senders.MouseEventSender
import com.timatifey.models.senders.ScrollEventSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.Socket
import java.net.SocketException
import kotlin.random.Random

var id = "emptyID"

const val idLength = 32
fun generateId(): String {
    val random by lazy { Random }
    return (1..idLength).map { random.nextInt('A'.toInt(), 'Z'.toInt()).toChar() }.joinToString(separator = "")
}

class Client {
    private var wasInit = false

    private lateinit var socketMessage: Socket
    private lateinit var socketScreen: Socket
    private lateinit var socketMouse: Socket
    private lateinit var socketKey: Socket
    private lateinit var socketScroll: Socket

    lateinit var messageSender: MessageEventSender private set
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var keyEventSender: KeyEventSender private set
    lateinit var scrollEventSender: ScrollEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set

    val status = SimpleStringProperty("")

    fun startConnection(ip: String, port: Int, dataTypesList: List<DataPackage.DataType>): Boolean {
        try {
            id = generateId()

            socketMessage = Socket(ip, port)
            messageSender = MessageEventSender(ObjectOutputStream(socketMessage.getOutputStream()))
            Thread(messageSender).start()

            socketScreen = Socket(ip, port)
            screenReceiver = ScreenReceiver(socketScreen)
            Thread(screenReceiver).start()

            if (DataPackage.DataType.MOUSE in dataTypesList) {
                socketMouse = Socket(ip, port)
                mouseEventSender = MouseEventSender(ObjectOutputStream(socketMouse.getOutputStream()))
                Thread(mouseEventSender).start()

                socketScroll = Socket(ip, port)
                scrollEventSender = ScrollEventSender(ObjectOutputStream(socketScroll.getOutputStream()))
                Thread(scrollEventSender).start()
            }

            if (DataPackage.DataType.KEY in dataTypesList) {
                socketKey = Socket(ip, port)
                keyEventSender = KeyEventSender(ObjectOutputStream(socketKey.getOutputStream()))
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
                        messageSender.putEvent(Message("$id:STOP"))
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
                    if (this::scrollEventSender.isInitialized)
                        scrollEventSender.stop()

                    if (this::socketKey.isInitialized)
                        socketKey.close()
                    if (this::socketMouse.isInitialized)
                        socketMouse.close()
                    if (this::socketScreen.isInitialized)
                        socketScreen.close()
                    if (this::socketMessage.isInitialized)
                        socketMessage.close()
                    if (this::socketScroll.isInitialized)
                        socketScroll.close()
                } catch (e: SocketException) { println(e.message) }
                wasInit = false
            }
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }

}