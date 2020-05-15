package com.timatifey.controllers

import com.timatifey.models.client.Client
import tornadofx.*
import javafx.scene.input.MouseEvent

class MouseController: Controller() {

    fun setEvent(event: MouseEvent) {
        Client.mouseEventSender.setEvent(event)
    }

    fun disconnect() {
        Client.stopConnection()
    }

}