package com.timatifey.models.receivers

import com.google.gson.Gson
import com.timatifey.models.Key
import java.awt.AWTException
import java.awt.Robot
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.Socket

class KeyEventReceiver(private val client: Socket): Runnable {

    private fun keyRealise(key: Key) {
        try {
            val robot = Robot()
            when (key.eventType) {
                Key.KeyEventType.KEY_PRESSED -> {
                    robot.keyPress(key.code.code)
                }
                Key.KeyEventType.KEY_RELEASED -> {
                    robot.keyRelease(key.code.code)
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
            while (true) {
                val json = input.readLine()
                println(json)
                if (json != null) {
                    val key = Gson().fromJson(json, Key::class.java)
                    keyRealise(key)
                }
                sleep(10)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            println("Key Controller Client Socket Error: $e")
        }
    }
}