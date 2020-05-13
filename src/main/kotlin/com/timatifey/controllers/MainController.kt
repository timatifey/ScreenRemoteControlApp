package com.timatifey.controllers

import com.timatifey.models.client.Client
import com.timatifey.views.MainView
import com.timatifey.views.ScreenRemoteControlView
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.lang.NumberFormatException

class MainController: Controller() {
    val statusProperty = SimpleStringProperty("")
    var status: String by statusProperty

    fun connect(ip: String, port: String) {
        runLater { status = "" }
        try {
            val intPort = port.toInt()
            val result = Client.startConnection(ip, intPort)
            runLater {
                if (result) {
                    find(MainView::class).replaceWith(
                            ScreenRemoteControlView::class,
                            sizeToScene = true,
                            centerOnScreen = true
                    )
                }
            }
        }
        catch (e: NumberFormatException) {
            runLater { status = "Wrong port, use only digits" }
        }
    }

}