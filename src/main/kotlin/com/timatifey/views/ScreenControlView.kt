package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.KeyController
import com.timatifey.controllers.MouseController
import javafx.beans.binding.Bindings
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import tornadofx.*
import java.util.concurrent.Callable

class ScreenControlView : View("") {
    private val clientController: ClientController by inject()
    private val mouseController: MouseController by inject()
    private val keyController: KeyController by inject()
    private val image = clientController.client.screenReceiver.imageScene
    private val model = ViewModel()
    override val root = form {
        title = "${clientController.ip}:${clientController.port}"
        setPrefSize(1920.0, 1075.0)
        usePrefSize = true
        minWidth = 256.0
        minHeight = 144.0
        imageview(image) {
            mouseController.height.value = height
            mouseController.width.value = width
            paddingAll = 0.0
            isPreserveRatio = true
            fitHeightProperty().bind(Bindings.createDoubleBinding(
                    Callable {
                        parent.layoutBounds.height
                    }, parent.layoutBoundsProperty()
            ))
            fitWidthProperty().bind(Bindings.createDoubleBinding(
                    Callable {
                        parent.layoutBounds.width
                    }, parent.layoutBoundsProperty()
            ))

            addEventHandler(MouseEvent.ANY) {
                mouseController.sendMouseEvent(it!!)
            }
        }
        addEventHandler(KeyEvent.ANY) {
            keyController.sendKeyEvent(it!!)
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            it.consume()
            confirm("Вы уверены, что хотите разорвать соединение?") {
                clientController.stopConnection()
                currentWindow?.hide()
                find(ScreenControlView::class).replaceWith(MainView::class)
            }
        }
    }

}
