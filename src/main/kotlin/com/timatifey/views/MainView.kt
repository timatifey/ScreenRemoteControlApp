package com.timatifey.views

import com.timatifey.app.Styles
import com.timatifey.controllers.ClientController
import com.timatifey.controllers.ServerController
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextFormatter
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.system.exitProcess

private val isClientMode = SimpleBooleanProperty(true)
private val portFilter: (TextFormatter.Change) -> Boolean = { change ->
    !change.isAdded || change.controlNewText.isInt()
}


class MainView : View("Screen Remote Control") {
    private val clientController: ClientController by inject()
    private val serverController: ServerController by inject()
    private val width = 300.0
    private val height = 340.0

    override val root = form {
        addClass(Styles.wrapper)
        setPrefSize(this@MainView.width, this@MainView.height)
        usePrefSize = true
        isCenterShape = true
        vbox {
            spacer()
            hbox {
                spacer()
                togglebutton("SWITCH MODE ON SERVER") {
                    useMaxWidth = true
                    action {
                        this.text = if (text == "SWITCH MODE ON SERVER") "SWITCH MODE ON CLIENT" else
                            "SWITCH MODE ON SERVER"
                        isClientMode.value = !isClientMode.value
                    }
                }
                spacer()
            }
            spacer()
            add(find(ClientForm::class))
            add(find(ServerForm::class))
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            it.consume()
            confirm("Really close?", "Do you want to close?") {
                clientController.stopConnection()
                serverController.stopServer()
                currentWindow?.hide()
            }
        }
        exitProcess(0)
    }
}

class ClientForm: Fragment() {
    private val clientController: ClientController by inject()
    private val model = ViewModel()
    private val ip = model.bind { SimpleStringProperty() }
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        removeWhen(!isClientMode)
        usePrefSize = true
        title = "Client"
        vbox {
            fieldset("IP") { textfield(ip).required() }
            fieldset("PORT") {
                textfield(port) {
                    filterInput(portFilter)
                }.required()
            }
            hbox {
                spacer()
                checkbox("Image") {
                    bind(clientController.imageCheck)
                    isSelected = true
                }
                spacer()
                checkbox("Mouse") {
                    bind(clientController.mouseCheck)
                    isSelected = true
                }
                spacer()
                checkbox("Key") {
                    bind(clientController.keyCheck)
                    isSelected = true
                }
                spacer()
                vboxConstraints { marginBottom = 10.0 }
            }
            spacer()
            button("Connect") {
                enableWhen(model.valid)
                useMaxWidth = true
                action { runAsyncWithProgress { clientController.connect(ip.value, port.value) } }
            }
            spacer()
            label(clientController.statusProperty) { style { textFill = Color.RED } }
            label(clientController.client.status) { style { textFill = Color.BLUE } }
        }
        children.addClass(Styles.wrapper)
    }
}

class ServerForm: Fragment() {
    private val serverController: ServerController by inject()
    private val model = ViewModel()
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        removeWhen(isClientMode)
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
                useMaxWidth = true
                action { runAsyncWithProgress { serverController.start(port.value) } }
                vboxConstraints { marginBottom = 10.0 }
            }
            button("Stop server") {
                enableWhen(serverController.hasStarted)
                useMaxWidth = true
                action { runAsyncWithProgress { serverController.stopServer() } }
            }
        }

        label(serverController.server.statusProperty) { style { textFill = Color.BLUE } }
        label(serverController.server.statusClient) { style { textFill = Color.BLUE } }

        children.addClass(Styles.wrapper)
    }
}