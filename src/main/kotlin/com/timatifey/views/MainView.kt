package com.timatifey.views

import com.timatifey.app.Styles
import com.timatifey.controllers.ClientController
import com.timatifey.controllers.ServerController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class MainView : View("Screen Remote Control") {
    private val clientController: ClientController by inject()
    private val serverController: ServerController by inject()

    override val root = tabpane {
        addClass(Styles.wrapper)
        setPrefSize(238.0, 290.0)
        usePrefSize = true
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tab<ClientForm>()
        tab<ServerForm>()

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
    private val portFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.isInt()
    }
    private val clientController: ClientController by inject()
    private val model = ViewModel()
    private val ip = model.bind { SimpleStringProperty() }
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        usePrefSize = true
        title = "Client"
        fieldset("IP" ) {
            usePrefSize = true
            textfield(ip).required()
        }
        fieldset("PORT") {
            usePrefSize = true
            textfield(port) {
                filterInput(portFilter)
            }.required()
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
        label(clientController.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.RED
                fontWeight = FontWeight.BOLD
            }
        }
        label(clientController.client.status) {
            style {
                paddingTop = 10
                textFill = Color.BLUE
                fontWeight = FontWeight.BOLD
            }
        }
        children.addClass(Styles.wrapper)
    }
}

class ServerForm: Fragment() {
    private val serverController: ServerController by inject()
    private val model = ViewModel()
    private val port = model.bind { SimpleStringProperty() }
    private val portFilter: (TextFormatter.Change) -> Boolean = { change ->
        !change.isAdded || change.controlNewText.isInt()
    }

    override val root = form {
        title = "Server"
        usePrefSize = true
        fieldset("PORT") {
            textfield(port) {
                filterInput(portFilter)
            }.required()
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
                vboxConstraints {
                    marginBottom = 10.0
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
        label(serverController.server.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.BLUE
                fontWeight = FontWeight.BOLD
            }
        }
        label(serverController.server.statusClient) {
            style {
                paddingTop = 10
                textFill = Color.BLUE
                fontWeight = FontWeight.BOLD
            }
        }
        children.addClass(Styles.wrapper)
    }
}