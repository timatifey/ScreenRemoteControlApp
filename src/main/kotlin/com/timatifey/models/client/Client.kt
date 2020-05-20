package com.timatifey.models.client

import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MouseEventSender
import java.io.*
import java.net.Socket
import kotlin.system.exitProcess

class Client {
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

        return true
    }

    fun stopConnection() {
        try {
            if (wasInit) {
                mouseEventSender.stop()
                keyEventSender.stop()
                screenReceiver.stop()

                clientSocket.close()
                socketForKeys.close()
            }
            exitProcess(0)
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}