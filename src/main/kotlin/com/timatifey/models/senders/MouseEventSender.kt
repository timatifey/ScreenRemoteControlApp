package com.timatifey.models.senders

import com.timatifey.models.data.Mouse
import java.io.ObjectOutputStream

class MouseEventSender(output: ObjectOutputStream) : EventSender<Mouse>(output) {
    override val socketName = "MOUSE_SOCKET"
}