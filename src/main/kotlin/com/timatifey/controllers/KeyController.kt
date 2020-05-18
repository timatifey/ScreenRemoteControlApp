package com.timatifey.controllers

import com.timatifey.models.Key
import com.timatifey.models.Mouse
import com.timatifey.models.Mouse.MouseEventType
import com.timatifey.models.Mouse.MouseButton
import com.timatifey.models.client.Client
import javafx.scene.input.KeyEvent
import tornadofx.*
import javafx.scene.input.MouseEvent
import java.awt.Toolkit

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