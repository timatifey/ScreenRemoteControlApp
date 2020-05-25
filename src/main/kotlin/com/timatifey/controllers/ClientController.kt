package com.timatifey.controllers

import com.timatifey.models.client.Client
import com.timatifey.models.data.DataPackage
import com.timatifey.views.ScreenControlView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.runLater
import tornadofx.setValue

class ClientController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty
    var ip = ""
    var port = ""
    var client = Client()
    val mouseCheck = SimpleBooleanProperty(true)
    val keyCheck = SimpleBooleanProperty(true)

    fun connect(ip: String, port: String) {
        client = Client()
        runLater { status = "" }
        try {
            val intPort = port.toInt()

            val typeList = mutableListOf<DataPackage.DataType>()
            if (mouseCheck.value) typeList.add(DataPackage.DataType.MOUSE)
            if (keyCheck.value) typeList.add(DataPackage.DataType.KEY)

            val isConnected = client.startConnection(ip, intPort, typeList)

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