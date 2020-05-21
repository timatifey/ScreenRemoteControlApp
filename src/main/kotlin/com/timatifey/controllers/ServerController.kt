package com.timatifey.controllers

import com.timatifey.models.server.Server
import com.timatifey.views.MainView
import com.timatifey.views.ServerForm
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.lang.NumberFormatException

class ServerController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty
    var port = ""
    var hasStarted = SimpleBooleanProperty(false)
    private val server = Server()

    fun start(port: String) {
        runLater { status = "" }
        try {
            val intPort = port.toInt()
            runLater {
                status = "Server is waiting of connection"
            }
            hasStarted.value = true
            val isConnected = server.start(intPort)
            runLater {
                this.port = port
                if (isConnected) {
                    status = "Client has connected"
                }
            }
        }
        catch (e: NumberFormatException) {
            runLater { status = "Wrong port, please use only digits" }
        }
    }

    fun stopServer() {
        hasStarted.value = false
        server.stop()
        runLater {
            status = "Server has stopped"
        }
    }
}