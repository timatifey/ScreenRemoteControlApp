package com.timatifey.models.receivers

import com.timatifey.models.data.Data
import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.models.data.Mouse.MouseEventType
import java.awt.AWTException
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.InputEvent
import java.io.ObjectInputStream

class MouseEventReceiver(input: ObjectInputStream): EventReceiver<Mouse>(input) {
    override val socketName: String
        get() = "MOUSE_SOCKET"

    private var prevMouse = mutableListOf<Mouse?>(null, null)

    override fun realise(obj: Data) {
        try {
            val mouse = obj as Mouse
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val robot = Robot()
            val button = when (mouse.button) {
                MouseButton.PRIMARY -> InputEvent.BUTTON1_DOWN_MASK
                MouseButton.SECONDARY -> InputEvent.BUTTON3_DOWN_MASK
                MouseButton.MIDDLE -> InputEvent.BUTTON2_DOWN_MASK
                else -> null
            }
            when (mouse.eventType) {
                MouseEventType.MOUSE_MOVED -> {
                    robot.mouseMove(
                            (mouse.relativelyX * screenSize.width).toInt(),
                            (mouse.relativelyY * screenSize.height).toInt()
                    )
                }
                MouseEventType.MOUSE_CLICKED -> {
                    if (button != null) {
                        if (prevMouse[0] == null || prevMouse[1] == null ||
                                !(prevMouse[0]!!.eventType == MouseEventType.MOUSE_RELEASED &&
                                        prevMouse[1]!!.eventType == MouseEventType.MOUSE_DRAGGED)) {
                            for (click in 1..mouse.clickCount) {
                                robot.mousePress(button)
                                robot.mouseRelease(button)
                            }
                        }
                    }
                }
                MouseEventType.MOUSE_RELEASED -> {
                    if (button != null) {
                        if (prevMouse[0] == null || prevMouse[0]!!.eventType == MouseEventType.MOUSE_DRAGGED) {
                            robot.mouseRelease(button)
                        }
                    }
                }
                MouseEventType.MOUSE_DRAGGED -> {
                    if (button != null) {
                        if (prevMouse[0] == null || prevMouse[0]!!.eventType != MouseEventType.MOUSE_DRAGGED) {
                            robot.mousePress(button)
                        }
                    }
                    robot.mouseMove(
                            (mouse.relativelyX * screenSize.width).toInt(),
                            (mouse.relativelyY * screenSize.height).toInt()
                    )
                }
                else -> {}
            }
            prevMouse[1] = prevMouse[0]
            prevMouse[0] = mouse.copy()
        } catch (e: AWTException) {
            println("Mouse realise error: ${e.message}")
        }
    }
}