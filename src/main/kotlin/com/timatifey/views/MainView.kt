package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.ServerController
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.beans.Visibility


class MainView : View("Screen Remote Control") {

    override val root = borderpane {
        top {
            setPrefSize(250.0, 100.0)
            paddingAll = 10.0
            useMaxHeight = true
            useMaxWidth = true
            togglebutton("Выбрать режим клиента") {
                useMaxWidth = true
                action {
                    text = if (isSelected) "Выбрать режим сервера" else "Выбрать режим клиента"
                    if (isSelected) {
                        // REMOVE ServerForm: Fragment from borderpane.center
                        // ADD ClientForm: Fragment() to borderpane.center
                    } else {
                        // REMOVE ClientForm: Fragment from borderpane.center
                        // ADD ServerForm: Fragment() to borderpane.center
                    }
                }
            }
        }
        center {
            add(find(ClientForm::class))
        }
    }
}

class ClientForm: Fragment() {
    private val clientController: ClientController by inject()
    private val model = ViewModel()
    private val ip = model.bind { SimpleStringProperty() }
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        setPrefSize(200.0, 200.0)
        usePrefSize = true
        fieldset(labelPosition = Orientation.VERTICAL) {
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
        fieldset(labelPosition = Orientation.VERTICAL) {
            fieldset("PORT") {
                textfield(port).required()
            }
            button("Start server") {
                enableWhen(model.valid)
                isDefaultButton = true
                useMaxWidth = true
                action {
                    runAsyncWithProgress {
                        serverController.start(port.value)
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