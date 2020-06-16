package com.timatifey.models.senders

import com.timatifey.models.data.Key
import java.io.ObjectOutputStream

class KeyEventSender(output: ObjectOutputStream) : EventSender<Key>(output) {
    override val socketName = "KEY_SOCKET"
}
