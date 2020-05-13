package com.timatifey.controllers

import com.timatifey.models.client.Client
import tornadofx.*
import javafx.scene.input.MouseEvent

class MouseController: Controller() {
    private val mouseSender = Client.getMouseSender()

    fun setEvent(event: MouseEvent) {
        println("send $event")
        mouseSender.setEvent(event)
    }

}