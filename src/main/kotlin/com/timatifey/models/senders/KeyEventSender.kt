package com.timatifey.models.senders

import com.timatifey.models.data.Key
import java.io.ObjectOutputStream

class KeyEventSender(output: ObjectOutputStream) : Sender<Key>(output) {
    override val socketName = "KEY_SOCKET"
}

//class KeyEventSender(private val output: ObjectOutputStream): Runnable {
//    @Volatile private var needStop = false
//    private val queueKey = LinkedBlockingQueue<Key>()
//
//    fun putKeyEvent(key: Key) { queueKey.put(key) }
//
//    override fun run() {
//        try {
//            needStop = false
//
//            //First message
//            val firstMsg = DataPackage(DataPackage.DataType.MESSAGE,
//                message = "$id:KEY_SOCKET")
//            output.writeObject(firstMsg)
//            output.flush()
//
//            println("Key Event Sender Start")
//
//            while (!needStop) {
//                if (queueKey.isEmpty()) continue
//                val key = queueKey.take()
//                val data = DataPackage(DataPackage.DataType.KEY, key = key)
//                output.writeObject(data)
//                output.flush()
//            }
//        } catch (e: IOException) {
//            println("Key Event Sender Client Socket Error: $e")
//        } finally {
//            needStop = true
//            println("Key Event Sender Stop")
//            try {
//                output.close()
//            } catch (e: SocketException) {}
//        }
//    }
//
//    fun stop() { needStop = true }
//}
