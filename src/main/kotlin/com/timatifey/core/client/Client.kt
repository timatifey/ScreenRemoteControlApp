package com.timatifey.core.client

import java.io.*
import java.net.Socket
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

class Client(private val infoForSending:() -> Any): Runnable {
    private lateinit var clientSocket: Socket
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter

    fun startConnection(ip: String, port: Int) {
        clientSocket = Socket(ip, port)
        run()
    }

    private fun stopConnection() {
        clientSocket.close()
        output.close()
        input.close()
    }

    override fun run() {
        input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        output = PrintWriter(clientSocket.getOutputStream(), true)

        if (clientSocket.isConnected) println("CLIENT CONNECTED")

        while (clientSocket.isConnected) {
            val info = infoForSending()
            output.println(info)
            output.flush()
            TimeUnit.MILLISECONDS.sleep(500)
        }
        /*
        val reader = BufferedReader(InputStreamReader(System.`in`))

        print("msg: ")
        var inputLine: String? = reader.readLine()

        while (clientSocket.isConnected) {
            if (inputLine.equals("stop", ignoreCase = true)) {
                output.println("stop")
                output.flush()
                break
            }
            sendMsg(inputLine)
            print("msg: ")
            inputLine = reader.readLine()
        }
        */
        stopConnection()
    }
}