package com.timatifey.models.client

import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.MouseEventSender
import java.io.*
import java.net.Socket

object Client {
    private lateinit var clientSocket: Socket
    lateinit var mouseEventSender: MouseEventSender private set
    lateinit var screenReceiver: ScreenReceiver private set

    fun startConnection(ip: String, port: Int): Boolean {
       try {
            clientSocket = Socket(ip, port)
        } catch (e: IOException) {
            return false
        }
        if (clientSocket.isConnected && !clientSocket.isClosed) println("CLIENT CONNECTED TO $ip:$port")
        mouseEventSender = MouseEventSender(clientSocket)
        val mouseThread = Thread(mouseEventSender)
        mouseThread.start()

        screenReceiver = ScreenReceiver(clientSocket)
        val screenThread = Thread(screenReceiver)
        screenThread.start()
        return true
    }

    fun stopConnection() {
        try {
            clientSocket.close()
            mouseEventSender.stop()
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}