package com.timatifey.models.senders

import com.timatifey.models.data.Scroll
import java.io.ObjectOutputStream

class ScrollEventSender(output: ObjectOutputStream) : EventSender<Scroll>(output) {
    override val socketName = "SCROLL_SOCKET"
}