package com.timatifey.core.server

import java.io.*
import java.net.ServerSocket
import java.net.Socket

abstract class Server: Runnable {
    lateinit var server: ServerSocket
    lateinit var clientSocket: Socket
    lateinit var input: BufferedReader
    lateinit var output: PrintWriter

    fun start(port: Int) {
        try {
            server = ServerSocket(port)
            println("SERVER IS WAITING OF CONNECTION")
            clientSocket = server.accept()
            println("CLIENT CONNECTED")
            run()
        } catch (e: IOException) {
            println("Starting Server Error: $e")
        }
    }

    fun stop() {
        try {
            server.close()
        } catch (e: IOException) {
            println("Stopping Server Error: $e")
        }
    }

    fun stopConnection() {
        try {
            input.close()
            output.close()
            clientSocket.close()
        } catch (e: IOException) {
            println("Stopping Connection with server Error: $e")
        }
    }

    override fun run() {}
}