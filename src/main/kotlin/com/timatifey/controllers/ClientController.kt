package com.timatifey.controllers

import com.timatifey.models.client.Client
import com.timatifey.models.data.DataPackage
import com.timatifey.views.ScreenControlView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class ClientController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty

    var ip = ""
    var port = ""
    @Volatile var client = Client()

    val mouseCheck = SimpleBooleanProperty(true)
    val keyCheck = SimpleBooleanProperty(true)
    lateinit var window: View

    fun connect(ip: String, port: String) {
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
                    window = ScreenControlView()
                    window.title = "$ip:$port"
                    window.openWindow()
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
        println("STOP CLIENT\n")
        client.stopConnection()
    }
}