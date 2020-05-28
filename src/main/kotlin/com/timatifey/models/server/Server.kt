package com.timatifey.models.server

import com.google.gson.Gson
import com.timatifey.models.data.ClientListElement
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.MessageSender
import com.timatifey.models.senders.ScreenSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.runLater
import java.io.*
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

class Server {
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
            println("Server is waiting of connection")
            runLater { statusProperty.value = "Server is ready for connections" }
            wasInit = true
            needStop = false
            while (!needStop) {
                println("Server is waiting")
                val socket = server.accept()
                println("Something has connected")
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val json = input.readLine()
                val firstMsgFromSocket = gson.fromJson(json, DataPackage::class.java)
                println(firstMsgFromSocket)

                if (firstMsgFromSocket.message != null) {
                    val msg = firstMsgFromSocket.message.split(":")
                    val clientId = msg[0]
                    var isNormal = true
                    when (msg[1]) {
                        "MESSAGE_SOCKET" -> {
                            Thread(MessageSender(socket)).start()
                            Thread(MessageReceiver(socket)).start()
                        }
                        "SCREEN_SOCKET" -> Thread(ScreenSender(socket)).start()
                        "MOUSE_SOCKET" -> Thread(MouseEventReceiver(socket)).start()
                        "KEY_SOCKET" -> Thread(KeyEventReceiver(socket)).start()
                        else -> isNormal = false
                    }
                    if (isNormal) {
                        if (clientMap.keys.contains(clientId)) {
                            clientMap[clientId]?.sockets?.add(socket)
                        } else {
                            clientMap[clientId]?.sockets = mutableListOf()
                            clientMap[clientId]?.needStop = false
                            println("${socket.inetAddress.hostAddress} has connected")
                            runLater { statusClient.value = "${socket.inetAddress.hostAddress} has connected" }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            println("Starting server error: $e")
        }
    }

    fun stop() {
        try {
            if (wasInit) {
                needStop = true
                server.close()
                clientMap.forEachValue(1) {
                    it.sockets.forEach { socket ->
                        socket.close()
                    }
                    it.needStop = true
                }
            }
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}