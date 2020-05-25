package com.timatifey.controllers

import com.timatifey.models.data.Key
import javafx.scene.input.KeyEvent
import tornadofx.Controller

class KeyController: Controller() {
    private val clientController: ClientController by inject()

    fun sendKeyEvent(eventKey: KeyEvent) {
        clientController.client.keyEventSender.putKeyEvent(Key(
                Key.KeyEventType.valueOf(eventKey.eventType.name),
                eventKey.character,
                eventKey.text,
                eventKey.code,
                eventKey.isShiftDown,
                eventKey.isControlDown,
                eventKey.isAltDown,
                eventKey.isMetaDown
        ))
    }
}