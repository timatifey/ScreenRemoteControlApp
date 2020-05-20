package com.timatifey.controllers

import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseEventType
import com.timatifey.models.data.Mouse.MouseButton
import javafx.beans.property.SimpleDoubleProperty
import tornadofx.*
import javafx.scene.input.MouseEvent
import java.awt.Toolkit

class MouseController: Controller() {
    private val clientController: ClientController by inject()
    var height = SimpleDoubleProperty(Toolkit.getDefaultToolkit().screenSize.height.toDouble())
    var width = SimpleDoubleProperty(Toolkit.getDefaultToolkit().screenSize.height.toDouble())

    fun sendMouseEvent(eventMouse: MouseEvent) {
        println("width = ${width.value}   height = ${height.value}")
        clientController.client.mouseEventSender.putMouseEvent(Mouse(
                MouseEventType.valueOf(eventMouse.eventType.name),
                eventMouse.x,
                eventMouse.y,
                eventMouse.x / width.value,
                eventMouse.y / height.value,
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