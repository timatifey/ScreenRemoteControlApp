package com.timatifey.views

import com.timatifey.controllers.MainController
import com.timatifey.controllers.MouseController
import com.timatifey.controllers.ScreenControlController
import com.timatifey.models.client.Client
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import tornadofx.*

class ScreenControlView : View("View") {
    private val screenControlController: ScreenControlController by inject()
    private val mainController: MainController by inject()
    private val mouseController: MouseController by inject()

    //private val screenControlController: ScreenControlController by inject()

    override val root = form {
        setPrefSize(640.0, 500.0)
        fieldset() {
            label("${mainController.ip}:${mainController.port}")
            spacer()
            button("DISCONNECT") {
                mouseController.disconnect()
                action {
                    screenControlController.disconnect()
                }
            }
        }
        spacer()
        imageview(screenControlController.urlImage) {
            setPrefSize(640.0, 360.0)
        }
        setOnMouseMoved {
            mouseController.setEvent(it!!)
        }
    }
}
