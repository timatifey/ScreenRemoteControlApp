package com.timatifey.models.client

import com.timatifey.models.receivers.ScreenReceiver
import com.timatifey.models.senders.MouseEventSender
import java.io.*
import java.net.Socket
import java.util.concurrent.TimeUnit

object Client {
    private lateinit var clientSocket: Socket

    fun startConnection(ip: String, port: Int) {
        while (true) {
            try {
                clientSocket = Socket(ip, port)
                break
            } catch (e: IOException) {
                println("Client Connecting Error: $e")
                TimeUnit.SECONDS.sleep(3)
            }
        }
        val mouseThread = Thread(MouseEventSender(clientSocket))
        mouseThread.start()
        val screenThread = Thread(ScreenReceiver(clientSocket))
        screenThread.start()
    }

    fun stopConnection() {
        try {
            clientSocket.close()
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }
}