package com.timatifey.controllers

import com.timatifey.models.client.Client
import com.timatifey.views.MainView
import com.timatifey.views.ScreenControlView
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.lang.NumberFormatException

class ClientController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty
    var ip = ""
    var port = ""
    val client = Client()

    fun connect(ip: String, port: String) {
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

    fun disconnect() {
        client.stopConnection()
    }
}