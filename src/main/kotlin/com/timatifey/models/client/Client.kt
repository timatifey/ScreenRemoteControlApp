package com.timatifey.models.client

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MouseEventSender
import javafx.embed.swing.SwingFXUtils
import java.io.*
import java.net.Socket
import javax.imageio.ImageIO
import kotlin.system.exitProcess

class Client: Runnable {
    private lateinit var clientSocket: Socket
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set
    lateinit var keyEventSender: KeyEventSender private set
    lateinit var socketForKeys: Socket
    var wasInit = false

    fun startConnection(ip: String, port: Int): Boolean {
        try {
            clientSocket = Socket(ip, port)
        } catch (e: IOException) {
            return false
        }

        if (clientSocket.isConnected && !clientSocket.isClosed) println("CLIENT CONNECTED TO $ip:$port")

        mouseEventSender = MouseEventSender(clientSocket)
        Thread(mouseEventSender).start()

        socketForKeys = Socket(ip, port + 1)
        keyEventSender = KeyEventSender(socketForKeys)
        Thread(keyEventSender).start()

        screenReceiver = ScreenReceiver(clientSocket)
        Thread(screenReceiver).start()
        wasInit = true

        Thread(this).start()

        return true
    }

    override fun run() {
        val input = BufferedReader(InputStreamReader(socketForKeys.getInputStream()))
        while (true) {
            val json = input.readLine()
            if (json != null) {
                val data = Gson().fromJson(json, DataPackage::class.java)
                if (data.dataType == DataPackage.DataType.MESSAGE) {
                    val text = data.message!!
                    if (text.equals("stop", ignoreCase = true)) {
                        println("SERVER STOP")
                        screenReceiver.imageScene.value =
                                SwingFXUtils.toFXImage(ImageIO.read(File("server_shutdown.jpg")), null)
                        input.close()
                        stopConnection()
                        break
                    }
                }
            }

        }
    }

    fun stopConnection() {
        try {
            if (wasInit) {
                mouseEventSender.stop()
                keyEventSender.stop()
                screenReceiver.stop()
                try {
                    val output = PrintWriter(socketForKeys.getOutputStream(), true)
                    val data = Gson().toJson(DataPackage(DataPackage.DataType.MESSAGE, message = "stop"))
                    output.println(data)
                    output.close()
                } finally {
                    clientSocket.close()
                    socketForKeys.close()
                }
            }
            exitProcess(0)
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}