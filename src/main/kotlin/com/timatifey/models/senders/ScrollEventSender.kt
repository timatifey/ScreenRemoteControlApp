package com.timatifey.models.senders

import com.timatifey.models.client.id
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Scroll
import java.io.IOException
import java.io.ObjectOutputStream
import java.net.SocketException
import java.util.concurrent.LinkedBlockingQueue

class ScrollEventSender(private val output: ObjectOutputStream): Runnable {
    @Volatile private var needStop = false
    private val queueScroll = LinkedBlockingQueue<Scroll>()

    fun putScrollEvent(scroll: Scroll) { queueScroll.put(scroll) }

    override fun run() {
        try {
            needStop = false

            //First message
            val firstMsg = DataPackage(DataPackage.DataType.MESSAGE,
                    message = "$id:SCROLL_SOCKET")
            output.writeObject(firstMsg)
            output.flush()

            println("Scroll Event Sender Start")

            while (!needStop) {
                if (queueScroll.isEmpty()) continue
                val scroll = queueScroll.take()
                val data = DataPackage(DataPackage.DataType.SCROLL, scroll = scroll)
                output.writeObject(data)
                output.flush()
            }
        } catch (e: IOException) {
            println("Scroll Event Sender Client Socket Error: $e")
        } finally {
            needStop = true
            println("Scroll Event Sender Stop")
            try {
                output.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}
