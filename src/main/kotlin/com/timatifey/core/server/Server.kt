package com.timatifey.core.server

import java.io.*
import java.net.ServerSocket
import java.net.Socket

open class Server: Runnable {
    private lateinit var server: ServerSocket
    private lateinit var clientSocket: Socket

    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter
    private lateinit var inputLine: String

    fun start(port: Int) {
        server = ServerSocket(port)
        println("SERVER IS WAITING OF CONNECTION")
        clientSocket = server.accept()
        println("CLIENT CONNECTED")
        run()
    }

    private fun stop() {
        clientSocket.close()
        server.close()
        input.close()
        output.close()
    }

    fun isConnected(): Boolean {
        return clientSocket.isConnected
    }

    fun getInfo(): String {
        return inputLine
    }

    override fun run() {
        input = BufferedReader(InputStreamReader(clientSocket.getInputStream()));
        output = PrintWriter(clientSocket.getOutputStream(), true)

        inputLine = input.readLine()
        while (clientSocket.isConnected) {
            if (inputLine.equals("stop", ignoreCase = true)) {
                println("DISCONNECT")
                output.println("DISCONNECT")
                output.flush()
                break
            }
            println(inputLine)
            inputLine = input.readLine()
        }
        stop()
    }
}