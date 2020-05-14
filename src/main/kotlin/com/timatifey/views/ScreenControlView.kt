package com.timatifey.views

import com.timatifey.controllers.MainController
import com.timatifey.controllers.MouseController
import com.timatifey.controllers.ScreenControlController
import com.timatifey.models.client.Client
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import tornadofx.*

class ScreenControlView : View("View") {
    private val mainController: MainController by inject()
    private val mouseController: MouseController by inject()
    //private val screenControlController: ScreenControlController by inject()

    override val root = vbox {
        setPrefSize(500.0, 500.0)
        fieldset(labelPosition = Orientation.HORIZONTAL) {
            label("${mainController.ip}:${mainController.port}")
            button("DISCONNECT") {
                paddingLeft = 100.0
                mouseController.disconnect()
                replaceWith<MainView>()
            }
        }
        imageview("screen.png") {
            setPrefSize(500.0, 500.0)
        }
        setOnMouseMoved {
            mouseController.setEvent(it!!)
        }
    }
}
