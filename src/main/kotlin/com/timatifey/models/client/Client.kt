package com.timatifey.models.client

import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.KeyEventSender
import com.timatifey.models.senders.MouseEventSender
import java.io.*
import java.net.Socket

object Client {
    private lateinit var clientSocket: Socket
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set
    lateinit var keyEventSender: KeyEventSender private set
    
    fun startConnection(ip: String, port: Int): Boolean {
        try {
            clientSocket = Socket(ip, port)
        } catch (e: IOException) {
            return false
        }

        if (clientSocket.isConnected && !clientSocket.isClosed) println("CLIENT CONNECTED TO $ip:$port")

        mouseEventSender = MouseEventSender(clientSocket)
        Thread(mouseEventSender).start()

        keyEventSender = KeyEventSender(clientSocket)
        Thread(keyEventSender).start()

        screenReceiver = ScreenReceiver(clientSocket)
        Thread(screenReceiver).start()

        return true
    }

    fun stopConnection() {
        try {
            mouseEventSender.stop()
            keyEventSender.stop()
            screenReceiver.stop()
            clientSocket.close()
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}