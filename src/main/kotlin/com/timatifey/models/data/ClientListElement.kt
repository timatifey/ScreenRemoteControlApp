package com.timatifey.models.data

import com.timatifey.models.receivers.KeyEventReceiver
import com.timatifey.models.receivers.MessageReceiver
import com.timatifey.models.receivers.MouseEventReceiver
import com.timatifey.models.senders.MessageSender
import com.timatifey.models.senders.ScreenSender
import java.net.Socket

class ClientListElement {
    lateinit var sockets: MutableList<Socket>
    lateinit var messageSender: MessageSender
    lateinit var messageReceiver: MessageReceiver
    lateinit var screenSender: ScreenSender
    lateinit var mouseEventReceiver: MouseEventReceiver
    lateinit var keyEventReceiver: KeyEventReceiver
    @Volatile var needDelete = false

    fun stopAll() {
        sockets.forEach { it.close() }
        if (this::messageSender.isInitialized)
            messageSender.stop()
        if (this::messageReceiver.isInitialized)
            messageReceiver.stop()
        if (this::screenSender.isInitialized)
            screenSender.stop()
        if (this::mouseEventReceiver.isInitialized)
            mouseEventReceiver.stop()
        if (this::keyEventReceiver.isInitialized)
            keyEventReceiver.stop()
        needDelete = true
    }
}