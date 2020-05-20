package com.timatifey.models.server

import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.ScreenSender
import java.io.*
import java.net.ServerSocket
import java.net.Socket

object Server  {
    private lateinit var server: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var mouseEventReceiver: MouseEventReceiver
    private lateinit var keyEventReceiver: KeyEventReceiver
    private lateinit var screenSender: ScreenSender

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            println("SERVER IS WAITING OF CONNECTION")
            clientSocket = server.accept()
            println("CLIENT CONNECTED")

            mouseEventReceiver = MouseEventReceiver(clientSocket)
            Thread(mouseEventReceiver).start()

            keyEventReceiver = KeyEventReceiver(clientSocket)
            Thread(keyEventReceiver).start()

            screenSender = ScreenSender(clientSocket)
            Thread(screenSender).start()

        } catch (e: IOException) {
            println("Starting Server Error: $e")
        }
    }

    fun stop() {
        try {
            clientSocket.close()
            mouseEventReceiver.stop()
            keyEventReceiver.stop()
            screenSender.stop()
            server.close()
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }
}