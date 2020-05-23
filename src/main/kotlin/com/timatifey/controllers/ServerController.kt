package com.timatifey.controllers

import com.timatifey.models.server.Server
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.lang.NumberFormatException

class ServerController: Controller() {
    var hasStarted = SimpleBooleanProperty(false)
    val server = Server()

    fun start(port: String) {
        runLater { server.statusProperty.value = "" }
        try {
            val intPort = port.toInt()
            hasStarted.value = true
            server.start(intPort)
        }
        catch (e: NumberFormatException) {
            runLater { server.statusProperty.value = "Wrong port, please use only digits" }
        }
    }

    fun stopServer() {
        hasStarted.value = false
        server.stop()
        runLater {
            server.statusProperty.value = "Server has stopped"
        }
    }
}