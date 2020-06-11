package com.timatifey.models.receivers

import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Scroll
import java.awt.Robot
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.net.SocketException

class ScrollEventReceiver(private val input: ObjectInputStream): Runnable {
    @Volatile var needStop = false

    private fun scrollRealise(scroll: Scroll) {
        val robot = Robot()
        try {
            println(scroll)
            when (scroll.eventType) {
                Scroll.ScrollEventType.SCROLL -> {
                    robot.mouseWheel((scroll.deltaY * 0.75).toInt())
                }
                else -> {}
            }
        } catch (e: IllegalArgumentException) {
            println("Scroll realise error: ${e.message}")
        }
    }

    override fun run() {
        try {
            needStop = false
            println("Scroll event receiver has started")

            while (!needStop) {
                try {
                    val data = input.readObject() as DataPackage
                    if (data.dataType == DataPackage.DataType.SCROLL) {
                        val scroll = data.scroll!!
                        scrollRealise(scroll)
                    }
                } catch (e: EOFException) {
                    needStop = true
                } catch (e: SocketException) {
                    needStop = true
                }
            }
        } catch (e: IOException) {
            println("Scroll Event Receiver Client Socket Error: $e")
        } finally {
            needStop = true
            println("Scroll Event Receiver Stop")
            try {
                input.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}