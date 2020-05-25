package com.timatifey.models.server

import com.google.gson.Gson
import com.timatifey.models.data.ClientListElement
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.ScreenSender
import javafx.beans.property.SimpleStringProperty
import tornadofx.runLater
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.concurrent.ConcurrentLinkedDeque

class Server {
    private lateinit var server: ServerSocket
    private val clientList = ConcurrentLinkedDeque<ClientListElement>()
    private var needStop = false
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
            while (!needStop) {
                val client = ClientHandler(server.accept())
                clientList.add(client)
                Thread(client).start()
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
                for (client in clientList) {
                    client.needStop = true
                }
            }
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }

    inner class ClientHandler(private val socket: Socket): Runnable, ClientListElement {
        private lateinit var mouseEventReceiver: MouseEventReceiver
        private lateinit var keyEventReceiver: KeyEventReceiver
        private lateinit var screenSender: ScreenSender

        private lateinit var input: BufferedReader
        private lateinit var output: PrintWriter

        override val ip: String = socket.inetAddress.hostAddress
        override val dataSharingTypes: MutableList<DataPackage.DataType> = mutableListOf()
        @Volatile override var needStop: Boolean = false

        override fun run() {
            try {
                println("$ip has connected")
                runLater { statusClient.value = "$ip has connected" }

                //Confirmation types
                input = BufferedReader(InputStreamReader(socket.getInputStream()))
                output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
                val clientMsg = input.readLine()
                val msg = gson.fromJson(clientMsg, DataPackage::class.java)
                if (msg.message != null) {
                    val dataTypes: List<DataPackage.DataType> = msg.message.split(", ")
                        .map { DataPackage.DataType.valueOf(it) }

                    screenSender = ScreenSender(socket)
                    Thread(screenSender).start()

                    if (DataPackage.DataType.MOUSE in dataTypes) {
                        mouseEventReceiver = MouseEventReceiver(socket)
                        Thread(mouseEventReceiver).start()
                    }
                    if (DataPackage.DataType.KEY in dataTypes) {
                        keyEventReceiver = KeyEventReceiver(socket)
                        Thread(keyEventReceiver).start()
                    }
                } else {
                    println("Client data types are NULL")
                    runLater { statusClient.value = "Client data types are NULL" }
                }
                //Main part
//                while (!needStop) {
//                    val json = input.readLine()
//                    if (json != null) {
//                        try {
//                            val data = Gson().fromJson(json, DataPackage::class.java)
//                            if (data.dataType == DataPackage.DataType.MESSAGE) {
//                                val text = data.message!!
//                                if (text.equals("stop", ignoreCase = true)) {
//                                    runLater { statusClient.value = "Client $ip has disconnected" }
//                                    println("Client $ip has disconnected")
//                                    input.close()
//                                    break
//                                }
//                            }
//                        } catch (e: IllegalStateException) {
//                            println("Server: ${e.message}")
//                        }
//                    }
//                }
//                stop()
            } catch (e: SocketException) {
                println(e.message)
            }
        }

        fun stop() {
            try {
                if (this::mouseEventReceiver.isInitialized)
                    mouseEventReceiver.stop()
                if (this::keyEventReceiver.isInitialized)
                    keyEventReceiver.stop()
                if (this::screenSender.isInitialized)
                    screenSender.stop()

                try {
                    val output = PrintWriter(socket.getOutputStream(), true)
                    val data = Gson().toJson(DataPackage(DataPackage.DataType.MESSAGE, message = "stop"))
                    output.println(data)
                    output.close()
                } finally {
                    try { socket.close() }
                    catch (e: SocketException) { println(e.message) }
                }
                clientList.remove(this)
            } catch (e: IOException) {
                println("Stopping Server Error: $e")
            }
        }
    }
}