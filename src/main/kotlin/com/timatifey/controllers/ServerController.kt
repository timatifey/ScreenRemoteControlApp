package com.timatifey.controllers

import com.timatifey.models.server.Server
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.lang.NumberFormatException

class ServerController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty
    var port = ""
    val server = Server()

    fun start(port: String) {
        runLater { status = "" }
        try {
            val intPort = port.toInt()
            server.start(intPort)
            runLater {
                this.port = port
            }
        }
        catch (e: NumberFormatException) {
            runLater { status = "Wrong port, please use only digits" }
        }
    }

    fun disconnect() {
        server.stop()
    }
}