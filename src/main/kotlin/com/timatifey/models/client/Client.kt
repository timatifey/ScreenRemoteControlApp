package com.timatifey.models.client

import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.MouseEventSender
import java.io.*
import java.net.Socket
import java.util.concurrent.TimeUnit

object Client {
    private lateinit var clientSocket: Socket
    private lateinit var mouseEventSender: MouseEventSender
    private lateinit var screenReceiver: ScreenReceiver

    fun startConnection(ip: String, port: Int): Boolean {
        while (true) {
            try {
                clientSocket = Socket(ip, port)
                break
            } catch (e: IOException) {
                println("Client Connecting Error: $e")
                TimeUnit.SECONDS.sleep(3)
            }
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
    fun getMouseSender() = mouseEventSender
    fun getScreenReceiver() = screenReceiver

    fun stopConnection() {
        try {
            clientSocket.close()
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}