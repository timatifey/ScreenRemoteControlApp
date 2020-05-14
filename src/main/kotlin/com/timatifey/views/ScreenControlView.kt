package com.timatifey.views

import com.timatifey.controllers.MouseController
import com.timatifey.controllers.ScreenControlController
import com.timatifey.models.client.Client
import tornadofx.*

class ScreenControlView : View("View") {
    private val mouseController: MouseController by inject()
    //private val screenControlController: ScreenControlController by inject()

    override val root = form {
        setPrefSize(maxWidth/2, maxHeight/2)
        imageview("screen.png")
        setOnMouseMoved {
            mouseController.setEvent(it!!)
        }
    }

    override fun onDelete() {
        Client.stopConnection()
        super.onDelete()
    }
}
