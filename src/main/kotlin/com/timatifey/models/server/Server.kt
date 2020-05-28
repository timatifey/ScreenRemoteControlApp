package com.timatifey.models.server

import com.google.gson.Gson
import com.timatifey.models.data.ClientListElement
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mode
import com.timatifey.models.data.Mouse
import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.MessageSender
import com.timatifey.models.senders.ScreenSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.runLater
import java.io.*
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

class Server: Runnable {
    private lateinit var server: ServerSocket
    private val clientMap = ConcurrentHashMap<String, ClientListElement>()
    @Volatile private var needStop = false
    private var wasInit = false

    val gson = Gson()
    val statusProperty = SimpleStringProperty("")
    val statusClient = SimpleStringProperty("")

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            Thread(this).start()
            println("Server is waiting of connection")
            runLater { statusProperty.value = "Server is ready for connections" }
            wasInit = true
            needStop = false
            while (!needStop) {
                println("Server is waiting")
                val socket = server.accept()
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val json = input.readLine()
                val firstMsgFromSocket = gson.fromJson(json, DataPackage::class.java)

                if (firstMsgFromSocket.message != null) {
                    val msg = firstMsgFromSocket.message.split(":")
                    val clientId = msg[0]
                    if (clientMap.keys.contains(clientId)) clientMap[clientId]?.sockets?.add(socket)
                    else {
                        clientMap[clientId]?.sockets = mutableListOf(socket)
                        println("${socket.inetAddress.hostAddress} has connected")
                        runLater { statusClient.value = "${socket.inetAddress.hostAddress} has connected" }
                    }

                    when (msg[1]) {
                        "MESSAGE_SOCKET" -> {
                            val messageSender = MessageSender(socket)
                            val messageReceiver = MessageReceiver(
                                socket,
                                Mode.SERVER,
                                clientListElement = clientMap[clientId]
                            )
                            Thread(messageSender).start()
                            Thread(messageReceiver).start()
                            clientMap[clientId]?.messageReceiver = messageReceiver
                            clientMap[clientId]?.messageSender = messageSender
                        }
                        "SCREEN_SOCKET" -> {
                            val screenSender = ScreenSender(socket)
                            Thread(screenSender).start()
                            clientMap[clientId]?.screenSender = screenSender
                        }
                        "MOUSE_SOCKET" -> {
                            val mouseEventReceiver = MouseEventReceiver(socket)
                            Thread(mouseEventReceiver).start()
                            clientMap[clientId]?.mouseEventReceiver = mouseEventReceiver
                        }
                        "KEY_SOCKET" -> {
                            val keyEventReceiver = KeyEventReceiver(socket)
                            Thread(keyEventReceiver).start()
                            clientMap[clientId]?.keyEventReceiver = keyEventReceiver
                        }
                    }
                }
                println(clientMap)
            }
        } catch (e: IOException) {
            println("Starting server error: $e")
        }
    }

    override fun run() {
        while (!needStop) {
            println(clientMap)
            clientMap.entries.forEach {
                if (it.value.needDelete) {
                    runLater { statusClient.value = "${it.value.sockets[0].inetAddress.hostAddress} has disconnected" }
                    clientMap.remove(it.key)
                    sleep(200)
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
                clientMap.forEachValue(1) {
                    it.messageSender.sendMessage("stop")
                    it.stopAll()
                }
            }
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}