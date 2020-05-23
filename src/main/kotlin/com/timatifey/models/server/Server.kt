package com.timatifey.models.server

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.ScreenSender
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class Server: Runnable {
    private lateinit var server: ServerSocket
    private lateinit var serverForKeys: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var socketForKeys: Socket
    private lateinit var mouseEventReceiver: MouseEventReceiver
    private lateinit var keyEventReceiver: KeyEventReceiver
    private lateinit var screenSender: ScreenSender
    private var oldPort = -1
    private var initiatorOfDisconnectIsClient = false
    var wasInit = false
    val statusProperty = SimpleStringProperty("")
    val statusClient = SimpleStringProperty("")

    private val hasConnected = SimpleBooleanProperty(false)

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            serverForKeys = ServerSocket(port + 1)
            oldPort = port
            Thread(this).start()
        } catch (e: IOException) {
            println("Starting Server Error: $e")
        }
    }

    override fun run() {
        println("SERVER IS WAITING OF CONNECTION")
        runLater {
            statusProperty.value = "Server is waiting of connection"
        }
        try {
            clientSocket = server.accept()
            socketForKeys = serverForKeys.accept()
            println("CLIENT HAS CONNECTED")
            hasConnected.value = true
            runLater {
                statusProperty.value = "Server is connected"
                statusClient.value = "Client has connected"
            }

            mouseEventReceiver = MouseEventReceiver(clientSocket)
            Thread(mouseEventReceiver).start()

            keyEventReceiver = KeyEventReceiver(socketForKeys)
            Thread(keyEventReceiver).start()

            screenSender = ScreenSender(clientSocket)
            Thread(screenSender).start()

            wasInit = true
            val input = BufferedReader(InputStreamReader(socketForKeys.getInputStream()))
            while (true) {
                val json = input.readLine()
                if (json != null) {
                    val data = Gson().fromJson(json, DataPackage::class.java)
                    if (data.dataType == DataPackage.DataType.MESSAGE) {
                        val text = data.message!!
                        if (text.equals("stop", ignoreCase = true)) {
                            runLater {
                                statusClient.value = "Client has disconnected,\nrestart server"
                            }
                            hasConnected.value = false
                            println("CLIENT STOP")
                            input.close()
                            break
                        }
                    }
                }
            }
        } catch (e: SocketException) {}
    }

    fun stop() {
        try {
            if (wasInit) {
                mouseEventReceiver.stop()
                keyEventReceiver.stop()
                screenSender.stop()
                try {
                    val output = PrintWriter(socketForKeys.getOutputStream(), true)
                    val data = Gson().toJson(DataPackage(DataPackage.DataType.MESSAGE, message = "stop"))
                    output.println(data)
                    output.close()
                } finally {
                    try {
                        clientSocket.close()
                        socketForKeys.close()
                    } catch (e: SocketException) {}
                }
            }
            server.close()
            serverForKeys.close()
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}