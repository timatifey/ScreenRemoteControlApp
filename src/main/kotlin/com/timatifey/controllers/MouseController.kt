package com.timatifey.controllers

import com.timatifey.models.Mouse
import com.timatifey.models.Mouse.MouseEventType
import com.timatifey.models.Mouse.MouseButton
import com.timatifey.models.client.Client
import com.timatifey.views.ScreenControlView
import tornadofx.*
import javafx.scene.input.MouseEvent
import java.awt.Toolkit

class MouseController: Controller() {
    private val view: ScreenControlView by inject()
    private val height = view.currentWindow?.height
    private val width = view.currentWindow?.width

    fun sendMouseEvent(eventMouse: MouseEvent) {
        Client.mouseEventSender.putMouseEvent(Mouse(
                MouseEventType.valueOf(eventMouse.eventType.name),
                eventMouse.x,
                eventMouse.y,
                eventMouse.x / width!!,
                eventMouse.y / height!!,
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