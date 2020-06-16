package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.DataPackage
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.net.SocketException

abstract class Receiver<T>(private val input: ObjectInputStream): Runnable {
    @Volatile var needStop = false
    abstract val socketName: String

    abstract fun realise(obj: Data)

    override fun run() {
        try {
            needStop = false
            println("$socketName receiver has started")
            while (!needStop) {
                try {
                    val data = input.readObject() as DataPackage
                    val obj = data.data
                    realise(obj)
                } catch (e: EOFException) {
                    needStop = true
                } catch (e: SocketException) {
                    needStop = true
                }
            }
        } catch (e: IOException) {
            println("$socketName Receiver Client Socket Error: $e")
        } finally {
            needStop = true
            println("$socketName Receiver Stop")
            try {
                input.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}