package com.timatifey.controllers

import com.timatifey.models.data.Key
import com.timatifey.models.client.Client
import javafx.scene.input.KeyEvent
import tornadofx.*

class KeyController: Controller() {
    fun sendKeyEvent(eventKey: KeyEvent) {
        Client.keyEventSender.putKeyEvent(Key(
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