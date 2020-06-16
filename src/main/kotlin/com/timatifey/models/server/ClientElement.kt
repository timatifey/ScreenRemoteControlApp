package com.timatifey.models.server

import com.timatifey.models.receivers.EventReceiver
import com.timatifey.models.senders.ScreenSender
import java.net.Socket
import java.net.SocketException

class ClientElement {
    val sockets = mutableListOf<Socket>()
    val receivers = mutableListOf<EventReceiver<*>>()

    lateinit var screenSender: ScreenSender
    @Volatile var needDelete = false

    fun stopAll() {
        try {
            sockets.forEach { it.close() }
        } catch (e: SocketException){}

        for (receiver in receivers)
            receiver.stop()
        if (this::screenSender.isInitialized)
            screenSender.stop()
        needDelete = true
    }

    fun checkAll() {
        var result = true
        for (receiver in receivers)
            result = result && receiver.needStop
        needDelete = result
    }

    override fun toString(): String {
        val mainStringBuilder = StringBuilder("ClientListElement(${sockets.size} sockets, (")

        val sendersAndReceivers = mutableListOf<String>()

        for (receiver in receivers)
            sendersAndReceivers.add(receiver.socketName)

        if (this::screenSender.isInitialized)
            sendersAndReceivers.add("screenSender")

        mainStringBuilder.append(sendersAndReceivers.joinToString(separator = ", "))

        mainStringBuilder.append("))")

        return mainStringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientElement

        if (sockets != other.sockets) return false

        return true
    }

    override fun hashCode(): Int {
        return sockets.hashCode()
    }

}