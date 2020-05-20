package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.data.DataPackage
import com.timatifey.models.data.Mouse
import com.timatifey.models.data.Mouse.MouseButton
import com.timatifey.models.data.Mouse.MouseEventType
import java.awt.event.*
import java.awt.AWTException
import java.awt.Robot
import java.awt.Toolkit
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class MouseEventReceiver(private val client: Socket): Runnable {
    @Volatile var needStop = false

    private fun mouseRealise(mouse: Mouse) {
        try {
            val screenSize = Toolkit.getDefaultToolkit().screenSize
            val robot = Robot()
            when (mouse.eventType) {
                MouseEventType.MOUSE_MOVED -> {
                    robot.mouseMove(
                            (mouse.relativelyX * screenSize.width).toInt(),
                            (mouse.relativelyY * screenSize.height).toInt()
                    )
                }
                MouseEventType.MOUSE_CLICKED -> {
                    val button = when (mouse.button) {
                        MouseButton.PRIMARY -> InputEvent.BUTTON1_DOWN_MASK
                        MouseButton.SECONDARY -> InputEvent.BUTTON2_DOWN_MASK
                        MouseButton.MIDDLE -> InputEvent.BUTTON3_DOWN_MASK
                        else -> null
                    }
                    if (button != null) {
                        for (count in 1..mouse.clickCount) {
                            robot.mousePress(button)
                            robot.mouseRelease(button)
                        }
                    }
                }
                MouseEventType.MOUSE_PRESSED -> {
                    val button = when (mouse.button) {
                        MouseButton.PRIMARY -> InputEvent.BUTTON1_DOWN_MASK
                        MouseButton.SECONDARY -> InputEvent.BUTTON2_DOWN_MASK
                        MouseButton.MIDDLE -> InputEvent.BUTTON3_DOWN_MASK
                        else -> null
                    }
                    if (button != null) {
                        robot.mousePress(button)
                        robot.mouseRelease(button)
                    }
                }
                MouseEventType.MOUSE_DRAGGED -> {
                    val button = when (mouse.button) {
                        MouseButton.PRIMARY -> InputEvent.BUTTON1_DOWN_MASK
                        MouseButton.SECONDARY -> InputEvent.BUTTON2_DOWN_MASK
                        MouseButton.MIDDLE -> InputEvent.BUTTON3_DOWN_MASK
                        else -> null
                    }
                    if (button != null) {
                        robot.mousePress(button)
                        robot.mouseRelease(button)
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
                if (!client.isConnected) {
                    needStop = false
                    break
                }
                val json = input.readLine()
                if (json != null) {
                    val data = Gson().fromJson(json, DataPackage::class.java)
                    if (data.dataType == DataPackage.DataType.MOUSE) {
                        println(data.dataObject)
                        val m =  data.dataObject as Mouse
                        println(m)
                        //mouseRealise(data.dataObject as Mouse)
                    }
                }
            }
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