package com.timatifey.controllers

import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseEventType
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.models.client.Client
import tornadofx.*
import javafx.scene.input.MouseEvent
import java.awt.Toolkit

class MouseController: Controller() {
    private var height = 800.0
    private var width = 1440.0
    private val clientController: ClientController by inject()

    fun sendMouseEvent(eventMouse: MouseEvent) {
        val screen = Toolkit.getDefaultToolkit().screenSize
        clientController.client.mouseEventSender.putMouseEvent(Mouse(
                MouseEventType.valueOf(eventMouse.eventType.name),
                eventMouse.x,
                eventMouse.y,
                eventMouse.x / width,
                eventMouse.y / height,
                eventMouse.screenX,
                eventMouse.screenY,
                MouseButton.valueOf(eventMouse.button.name),
                eventMouse.clickCount,
                eventMouse.isShiftDown,
                eventMouse.isControlDown,
                eventMouse.isAltDown,
                eventMouse.isMetaDown,
                eventMouse.isPrimaryButtonDown,
                eventMouse.isMiddleButtonDown,
                eventMouse.isPopupTrigger,
                eventMouse.isStillSincePress,
                eventMouse.isSecondaryButtonDown
        ))
    }

}