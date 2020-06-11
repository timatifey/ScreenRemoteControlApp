package com.timatifey.models.server

import com.timatifey.models.data.ClientListElement
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.receivers.ScrollEventReceiver
import com.timatifey.models.senders.ScreenSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class Server(private val isConsole: Boolean = false): Runnable {
    private lateinit var server: ServerSocket
    @Volatile private var clientMap = ConcurrentHashMap<String, ClientListElement>()
    @Volatile private var needStop = false
    private var wasInit = false

    val statusProperty = SimpleStringProperty("")
    val statusClient = SimpleStringProperty("")

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            Thread(this).start()
            if (!isConsole) runLater { statusProperty.value = "Server is ready for connections" }
            wasInit = true
            needStop = false
            while (!needStop) {
                println("Server is waiting of connection")
                val socket = server.accept()
                val input = ObjectInputStream(socket.getInputStream())
                try {
                    val firstMsgFromSocket = input.readObject() as DataPackage
                    if (firstMsgFromSocket.message != null) {
                        val msg = firstMsgFromSocket.message.split(":")
                        val clientId = msg[0]
                        if (clientMap.keys.contains(clientId)) clientMap[clientId]?.sockets?.add(socket)
                        else {
                            clientMap[clientId] = ClientListElement()
                            clientMap[clientId]?.sockets = mutableListOf(socket)
                            println("${socket.inetAddress.hostAddress} has connected ( ID = $clientId )")
                            if (!isConsole)
                                runLater { statusClient.value = "${socket.inetAddress.hostAddress} has connected" }
                        }
                        when (msg[1]) {
                            "MESSAGE_SOCKET" -> {
                                val messageReceiver = MessageReceiver(input)
                                Thread(messageReceiver).start()
                                clientMap[clientId]?.messageReceiver = messageReceiver
                            }
                            "SCREEN_SOCKET" -> {
                                val screenSender = ScreenSender(ObjectOutputStream(socket.getOutputStream()))
                                Thread(screenSender).start()
                                clientMap[clientId]?.screenSender = screenSender
                            }
                            "MOUSE_SOCKET" -> {
                                val mouseEventReceiver = MouseEventReceiver(input)
                                Thread(mouseEventReceiver).start()
                                clientMap[clientId]?.mouseEventReceiver = mouseEventReceiver
                            }
                            "KEY_SOCKET" -> {
                                val keyEventReceiver = KeyEventReceiver(input)
                                Thread(keyEventReceiver).start()
                                clientMap[clientId]?.keyEventReceiver = keyEventReceiver
                            }
                            "SCROLL_SOCKET" -> {
                                val scrollEventReceiver = ScrollEventReceiver(input)
                                Thread(scrollEventReceiver).start()
                                clientMap[clientId]?.scrollEventReceiver = scrollEventReceiver
                            }
                        }
                    }
                } catch (e: EOFException) {
                    needStop = true
                    print("Server first message error: $e")
                } catch (e: SocketException) {
                    needStop = true
                    print("Server first message error: $e")
                }
                println()
            }
        } catch (e: IOException) {
                println("Starting server error: $e")
        }
    }

    override fun run() {
        while (!needStop) {
            clientMap.entries.forEach {
                it.value.checkAll()
                if (it.value.needDelete) {
                    it.value.stopAll()
                    if (!isConsole) runLater {
                            statusClient.value = "${it.value.sockets[0].inetAddress.hostAddress} has disconnected"
                        }
                    println("${it.value.sockets[0].inetAddress.hostAddress} has disconnected")
                    clientMap.remove(it.key)
                }
            }
            sleep(1000)
        }
    }

    fun stop() {
        try {
            if (wasInit) {
                needStop = true
                server.close()
                clientMap.forEachValue(1) { it.stopAll() }
                clientMap.clear()
            }
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}