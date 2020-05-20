package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.ServerController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class MainView : View("Screen Remote Control") {
    private val serverController: ServerController by inject()
    private val clientController: ClientController by inject()

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
        addEventHandler(KeyEvent.ANY) {
            println(it.eventType.name)
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            it.consume()
            confirm("Really close?", "Do you want to close") {
                clientController.stopConnection()
                serverController.stopServer()
                currentWindow?.hide()
            }
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
            button("Stop server") {
                enableWhen(serverController.hasStarted)
                isDefaultButton = true
                useMaxWidth = true
                action {
                    runAsyncWithProgress {
                        serverController.stopServer()
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