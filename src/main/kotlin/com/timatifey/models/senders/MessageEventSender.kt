package com.timatifey.models.senders

import com.timatifey.models.data.Message
import java.io.ObjectOutputStream

class MessageEventSender(output: ObjectOutputStream) : EventSender<Message>(output) {
    override val socketName = "MESSAGE_SOCKET"
}