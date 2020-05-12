package com.timatifey.models.server

import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.ScreenSender
import java.io.*
import java.net.ServerSocket
import java.net.Socket

object Server  {
    private lateinit var server: ServerSocket
    private lateinit var clientSocket: Socket

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            println("SERVER IS WAITING OF CONNECTION")
            clientSocket = server.accept()
            println("CLIENT CONNECTED")
            Thread(MouseEventReceiver(clientSocket)).start()
            Thread(ScreenSender(clientSocket)).start()
        } catch (e: IOException) {
            println("Starting Server Error: $e")
        }
    }

    fun stop() {
        try {
            clientSocket.close()
            server.close()
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}