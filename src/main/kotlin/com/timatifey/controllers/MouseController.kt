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
    private val clientController: ClientController by inject()
    val clientWidth = Toolkit.getDefaultToolkit().screenSize.width
    val clientHeight = Toolkit.getDefaultToolkit().screenSize.height
    private val serverWidth = clientController.client.screenReceiver.imageScene.value!!.width
    private val serverHeight = clientController.client.screenReceiver.imageScene.value!!.height
    private val height: Double
    private val width: Double

    init {
        //if max_width
        var newWidth = clientWidth.toDouble()
        var newHeight =  serverHeight * clientWidth / serverWidth
        if (newHeight > clientHeight) {
            //else max_height
            newHeight = clientHeight.toDouble()
            newWidth = serverWidth * clientHeight / serverHeight
        }
        height = newHeight
        width = newWidth
    }

    fun sendMouseEvent(eventMouse: MouseEvent) {
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