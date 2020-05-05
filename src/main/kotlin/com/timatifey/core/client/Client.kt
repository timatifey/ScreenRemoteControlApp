package com.timatifey.core.client

import java.io.*
import java.net.Socket

abstract class Client: Runnable {
    lateinit var clientSocket: Socket
    lateinit var output: PrintWriter

    fun startConnection(ip: String, port: Int) {
        try {
            clientSocket = Socket(ip, port)
            run()
        } catch (e: IOException) {
            println("Client Connecting Error: $e")
        }
    }

    fun stopConnection() {
        try {
            output.close()
            clientSocket.close()
        } catch (e: IOException) {
            println("Client Stop connection Error: $e")
        }
    }

    override fun run() {}
}