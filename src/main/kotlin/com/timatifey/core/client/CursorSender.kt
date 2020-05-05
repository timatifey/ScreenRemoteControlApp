package com.timatifey.core.client

import java.awt.MouseInfo
import com.timatifey.core.client.Client

object CursorSender {
    fun takeCords(): String {
        val mousePoint = MouseInfo.getPointerInfo()
        val x = mousePoint.location.x
        val y = mousePoint.location.y
        return """
            "x":$x,"y":$y
        """.trimIndent()
    }

    fun start() {
        val client = Client(::takeCords)
        client.startConnection("localhost", 6666)
    }
}

fun main() {
    CursorSender.start()
}