package com.timatifey.models.receivers

import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.models.data.Mouse.MouseEventType
import java.awt.AWTException
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.InputEvent
import java.io.EOFException
import java.io.IOException
import java.io.ObjectInputStream
import java.net.SocketException

class MouseEventReceiver(private val input: ObjectInputStream): Runnable {
    @Volatile var needStop = false
    private var prevMouse = mutableListOf<Mouse?>(null, null)

    private fun mouseRealise(mouse: Mouse) {
        try {
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
        } catch (e: AWTException) {
            println("Mouse realise error: ${e.message}")
        }
    }

    override fun run() {
        try {
            needStop = false
            println("Mouse event receiver has started")
            while (!needStop) {
                try {
                    val data = input.readObject() as DataPackage
                    if (data.dataType == DataPackage.DataType.MOUSE) {
                        val mouse = data.mouse!!
                        mouseRealise(mouse)
                        prevMouse[1] = prevMouse[0]
                        prevMouse[0] = mouse.copy()
                    }
                } catch (e: EOFException) {
                    needStop = true
                }
            }
        } catch (e: IOException) {
            println("Mouse Event Receiver Client Socket Error: $e")
            e.printStackTrace()
        } finally {
            needStop = true
            println("Mouse Event Receiver Stop")
            try {
                input.close()
            } catch (e: SocketException) {}
        }
    }

    fun stop() { needStop = true }
}