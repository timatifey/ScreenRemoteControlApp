package com.timatifey.models.senders

import com.timatifey.models.data.Message
import java.io.ObjectOutputStream

class MessageSender(output: ObjectOutputStream) : Sender<Message>(output) {
    override val socketName: String
        get() = "MESSAGE_SOCKET"
}

//class MessageSender(private val output: ObjectOutputStream): Runnable {
//    @Volatile private var needStop = false
//    private val queueMessages = LinkedBlockingQueue<String>()
//
//    fun sendMessage(msg: String) { queueMessages.put(msg) }
//
//    override fun run() {
//        try {
//            needStop = false
//
//            //First message
//            val firstMsg = DataPackage(DataPackage.DataType.MESSAGE,
//                message = "$id:MESSAGE_SOCKET")
//            output.writeObject(firstMsg)
//            output.flush()
//            println("Message Sender Start")
//
//            while (!needStop) {
//                if (queueMessages.isEmpty()) continue
//                val msg = queueMessages.take()
//                val data = DataPackage(DataPackage.DataType.MESSAGE, message = msg)
//                output.writeObject(data)
//                output.flush()
//            }
//        } catch (e: IOException) {
//            println("Message sender Socket Error: $e")
//        } finally {
//            needStop = true
//            println("Message sender Stop")
//            try {
//                output.close()
//            } catch (e: SocketException) {}
//        }
//    }
//
//    fun stop() { needStop = true }
//}