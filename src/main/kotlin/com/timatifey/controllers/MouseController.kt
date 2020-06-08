package com.timatifey.controllers

import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseEventType
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.views.ScreenControlView
import tornadofx.*
import javafx.scene.input.MouseEvent

class MouseController: Controller() {
    private val view: ScreenControlView by inject()
    private val clientController: ClientController by inject()

    fun sendMouseEvent(eventMouse: MouseEvent) {
        if (clientController.mouseCheck.value) {
            val clientWidth = view.root.center.boundsInLocal.width
            val clientHeight = view.root.center.boundsInLocal.height

            clientController.client.mouseEventSender.putMouseEvent(Mouse(
                    MouseEventType.valueOf(eventMouse.eventType.name),
                    eventMouse.x,
                    eventMouse.y,
                    eventMouse.x / clientWidth,
                    eventMouse.y / clientHeight,
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
}