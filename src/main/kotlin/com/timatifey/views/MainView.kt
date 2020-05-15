package com.timatifey.views

import com.timatifey.controllers.MainController
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class MainView : View("Screen Remote Control") {
    private val mainController: MainController by inject()
    private val model = ViewModel()
    private val ip = model.bind { SimpleStringProperty() }
    private val port = model.bind { SimpleStringProperty() }

    override val root = form {
        setPrefSize(200.0, 200.0)
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
                        mainController.connect(ip.value, port.value)
                    }
                }
            }
        }
        label(mainController.statusProperty) {
            style {
                paddingTop = 10
                textFill = Color.RED
                fontWeight = FontWeight.BOLD
            }
        }
    }

}
