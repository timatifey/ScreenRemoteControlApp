package com.timatifey.models.data

import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.ScreenSender
import java.net.Socket
import java.net.SocketException

class ClientListElement {
    lateinit var sockets: MutableList<Socket>
    lateinit var screenSender: ScreenSender
    lateinit var mouseEventReceiver: MouseEventReceiver
    lateinit var keyEventReceiver: KeyEventReceiver
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
        needDelete = true
    }

    fun checkAll() {
        var result = true
        result = result && screenSender.needStop
        result = result && mouseEventReceiver.needStop
        result = result && keyEventReceiver.needStop
        needDelete = result
    }

    override fun toString(): String {
        return "ClientListElement(sockets=$sockets, screenSender=$screenSender, mouseEventReceiver=$mouseEventReceiver, keyEventReceiver=$keyEventReceiver, needDelete=$needDelete)"
    }

}