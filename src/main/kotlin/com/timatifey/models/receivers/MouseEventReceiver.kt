package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.models.data.Mouse.MouseEventType
import java.awt.AWTException
import java.awt.Robot
import java.awt.Toolkit
import java.awt.event.InputEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class MouseEventReceiver(private val client: Socket): Runnable {
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
            }
        } catch (e: AWTException) {
            println(e.message)
            e.printStackTrace()
        }
    }

    override fun run() {
        try {
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            needStop = false
            while (!needStop) {
                if (!client.isConnected || client.isClosed) {
                    needStop = true
                    break
                }
                val json = input.readLine()
                if (json != null) {
                    val data = Gson().fromJson(json, DataPackage::class.java)
                    if (data.dataType == DataPackage.DataType.MOUSE) {
                        val mouse = data.mouse!!
                        mouseRealise(mouse)
                        prevMouse[1] = prevMouse[0]
                        prevMouse[0] = mouse.copy()
                    }
                }
            }
            input.close()
            client.close()
        } catch (e: IOException) {
            e.printStackTrace()
            println("Mouse Event Receiver Client Socket Error: $e")
        }
    }

    fun stop() {
        needStop = true
    }
}