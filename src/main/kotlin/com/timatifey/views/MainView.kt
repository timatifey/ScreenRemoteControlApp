package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.ServerController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*


class MainView : View("Screen Remote Control") {
    private lateinit var tabPane: TabPane
    private lateinit var tabFront: Tab

    override val root = tabpane {
        setPrefSize(200.0, 250.0)
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        usePrefSize = true
        tab<ClientForm>(){
            usePrefSize = true
        }
        tab<ServerForm> (){
            usePrefSize = true
        }
    }
}

class ClientForm: Fragment() {
    private val clientController: ClientController by inject()
    private val model = ViewModel()
    private val ip = model.bind { SimpleStringProperty() }
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        usePrefSize = true
        title = "Клиент"
        fieldset(labelPosition = Orientation.HORIZONTAL) {
            fieldset("IP") {
                textfield(ip).required()
            }
            fieldset("PORT") {
                textfield(port).required()
            }
            button("Connect") {
                enableWhen(model.valid)
                isDefaultButton = true
                useMaxWidth = true
                action {
                    runAsyncWithProgress {
                        clientController.connect(ip.value, port.value)
                    }
                }
            }
        }
        label(clientController.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.RED
                fontWeight = FontWeight.BOLD
            }
        }
    }
}

class ServerForm: Fragment() {
    private val serverController: ServerController by inject()
    private val model = ViewModel()
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        title = "Сервер"
        usePrefSize = true
        fieldset(labelPosition = Orientation.HORIZONTAL) {
            fieldset("PORT") {
                textfield(port).required()
            }
            vbox {
                button("Start server") {
                    enableWhen(!serverController.hasStarted)
                    enableWhen(model.valid)
                    isDefaultButton = true
                    useMaxWidth = true
                    action {
                        runAsyncWithProgress {
                            serverController.start(port.value)
                        }
                    }
                }
                paddingVertical = 30.0
                button("Stop server") {
                    enableWhen(serverController.hasStarted)
                    isDefaultButton = true
                    useMaxWidth = true
                    action {
                        runAsyncWithProgress {
                            serverController.disconnect()
                        }
                    }
                }
            }
        }
        label(serverController.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.RED
                fontWeight = FontWeight.BOLD
            }
        }
    }
}