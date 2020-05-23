package com.timatifey.controllers

import com.timatifey.models.client.Client
import com.timatifey.views.MainView
import com.timatifey.views.ScreenControlView
import javafx.beans.property.SimpleStringProperty
import javafx.scene.Scene
import javafx.stage.Stage
import tornadofx.*
import java.lang.NumberFormatException

class ClientController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty
    var ip = ""
    var port = ""
    var client = Client()

    fun connect(ip: String, port: String) {
        client = Client()
        runLater { status = "" }
        try {
            val intPort = port.toInt()
            val isConnected = client.startConnection(ip, intPort)
            runLater {
                if (isConnected) {
                    this.ip = ip
                    this.port = port
                    find(ScreenControlView::class).openModal()
                } else {
                    status = "Connection Error"
                }
            }
        }
        catch (e: NumberFormatException) {
            runLater { status = "Wrong port, please use only digits" }
        }
    }

    fun stopConnection() {
        println("STOP CLIENT")
        client.stopConnection()
    }
}