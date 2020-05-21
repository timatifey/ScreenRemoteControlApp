package com.timatifey.controllers

import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseEventType
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.views.ScreenControlView
import javafx.beans.property.SimpleDoubleProperty
import tornadofx.*
import javafx.scene.input.MouseEvent
import java.awt.Toolkit

class MouseController: Controller() {
    private val view: ScreenControlView by inject()
    private val clientController: ClientController by inject()

    fun sendMouseEvent(eventMouse: MouseEvent) {
        val clientWidth =  view.currentStage?.width
        val clientHeight = view.currentStage?.height
        val serverWidth = clientController.client.screenReceiver.width
        val serverHeight = clientController.client.screenReceiver.height
        //if max_width
        var newWidth = clientWidth!!.toDouble()
        var newHeight =  serverHeight / serverWidth * clientWidth
        if (newHeight > clientHeight!!) {
            //else max_height
            newHeight = clientHeight.toDouble()
            newWidth = serverWidth * clientHeight / serverHeight
        }
        val height = newHeight
        val width = newWidth

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