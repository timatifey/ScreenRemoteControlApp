package com.timatifey.views

import com.timatifey.controllers.MouseController
import com.timatifey.controllers.ScreenRemoteControlController
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*
import java.awt.Image

class ScreenRemoteControlView : View("View") {
    private val mouseController: MouseController by inject()
    private val screenRemoteControlController: ScreenRemoteControlController by inject()

    override val root = form {
        setPrefSize(1280.0, 720.0)
        imageview("screen.png") {
            setPrefSize(1280.0, 720.0)
            setOnMouseMoved {
                mouseController.setEvent(it!!)
            }
        }

    }
}
