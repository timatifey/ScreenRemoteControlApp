package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Message
import java.io.ObjectInputStream

class MessageReceiver(input: ObjectInputStream) : Receiver<Message>(input) {
    override val socketName: String
        get() = "MESSAGE_SOCKET"

    override fun realise(obj: Data) {
        val msg = obj as Message
        val text = msg.message.split(":")
        if (text[1] == "STOP") {
            needStop = true
        }
    }
}

//class MessageReceiver (private val input: ObjectInputStream): Runnable {
//    @Volatile var needStop = false
//
//    override fun run() {
//        try {
//            needStop = false
//            println("Message Receiver Start")
//            while (!needStop) {
//                try {
//                    val data = input.readObject() as DataPackage
//                    if (data.dataType == DataPackage.DataType.MESSAGE) {
//                        val text = data.message!!.split(":")
//                        if (text[1] == "STOP") {
//                            needStop = true
//                        }
//                    }
//                } catch (e: EOFException) {
//                    needStop = true
//                } catch (e: SocketException) {
//                    needStop = true
//                }
//            }
//        } catch (e: IOException) {
//            println("Message Receiver Socket Error: $e")
//        } finally {
//            needStop = true
//            println("Message Receiver Stop")
//            try {
//                input.close()
//            } catch (e: SocketException) {}
//        }
//    }
//
//    fun stop() { needStop = true }
//}