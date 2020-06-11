package com.timatifey.models.data

import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.receivers.ScrollEventReceiver
import com.timatifey.models.senders.ScreenSender
import java.net.Socket
import java.net.SocketException

class ClientListElement {
    lateinit var sockets: MutableList<Socket>
    lateinit var screenSender: ScreenSender
    lateinit var mouseEventReceiver: MouseEventReceiver
    lateinit var keyEventReceiver: KeyEventReceiver
    lateinit var messageReceiver: MessageReceiver
    lateinit var scrollEventReceiver: ScrollEventReceiver
    @Volatile var needDelete = false

    fun stopAll() {
        try {
            sockets.forEach { it.close() }
        } catch (e: SocketException){}
        if (this::screenSender.isInitialized)
            screenSender.stop()
        if (this::mouseEventReceiver.isInitialized)
            mouseEventReceiver.stop()
        if (this::keyEventReceiver.isInitialized)
            keyEventReceiver.stop()
        if (this::messageReceiver.isInitialized)
            messageReceiver.stop()
        if (this::scrollEventReceiver.isInitialized)
            scrollEventReceiver.stop()
        needDelete = true
    }

    fun checkAll() {
        var result = true
        if (this::mouseEventReceiver.isInitialized)
            result = result && mouseEventReceiver.needStop
        if (this::keyEventReceiver.isInitialized)
            result = result && keyEventReceiver.needStop
        if (this::scrollEventReceiver.isInitialized)
            result = result && scrollEventReceiver.needStop
        if (this::messageReceiver.isInitialized)
            result = result && messageReceiver.needStop
        needDelete = result
    }

    override fun toString(): String {
        val mainStringBuilder = StringBuilder("ClientListElement(${sockets.size} sockets, (")

        val sendersAndReceivers = mutableListOf<String>()
        if (this::messageReceiver.isInitialized)
            sendersAndReceivers.add("messageReceiver")
        if (this::screenSender.isInitialized)
            sendersAndReceivers.add("screenSender")
        if (this::mouseEventReceiver.isInitialized)
            sendersAndReceivers.add("mouseEventReceiver")
        if (this::keyEventReceiver.isInitialized)
            sendersAndReceivers.add("keyEventReceiver")
        if (this::scrollEventReceiver.isInitialized)
            sendersAndReceivers.add("scrollEventReceiver")

        mainStringBuilder.append(sendersAndReceivers.joinToString(separator = ", "))

        mainStringBuilder.append("))")

        return mainStringBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClientListElement

        if (sockets != other.sockets) return false

        return true
    }

    override fun hashCode(): Int {
        return sockets.hashCode()
    }

}