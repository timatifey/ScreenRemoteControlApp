package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Message
import java.io.ObjectInputStream

class MessageEventReceiver(input: ObjectInputStream) : EventReceiver<Message>(input) {
    override val socketName = "MESSAGE_SOCKET"

    override fun realise(obj: Data) {
        val msg = obj as Message
        val text = msg.message.split(":")
        if (text[1] == "STOP") {
            needStop = true
        }
    }
}