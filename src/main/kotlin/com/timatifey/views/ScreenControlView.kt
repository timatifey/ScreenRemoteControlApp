package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.KeyController
import com.timatifey.controllers.MouseController
import com.timatifey.models.client.Client
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

    override val root = form {
        title = "${clientController.ip}:${clientController.port}"
        setPrefSize(1920.0, 1080.0)
        usePrefSize = true
        minWidth = 256.0
        minHeight = 144.0
        button {
            useMaxWidth = true
            action {
                clientController.disconnect()
                find(ScreenControlView::class).replaceWith(MainView::class)
            }
        }
        imageview(image) {
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
            addEventHandler(MouseEvent.MOUSE_MOVED) {
                mouseController.sendMouseEvent(it!!)
            }
            addEventHandler(MouseEvent.MOUSE_CLICKED) {
                mouseController.sendMouseEvent(it!!)
            }
        }
        addEventHandler(KeyEvent.KEY_TYPED) {
            keyController.sendKeyEvent(it!!)
        }
    }

    override fun onDelete() {
        confirm("Вы уверены, что хотите разорвать соединение?") {
            clientController.disconnect()
            find(ScreenControlView::class).replaceWith(MainView::class)
        }
    }

}
