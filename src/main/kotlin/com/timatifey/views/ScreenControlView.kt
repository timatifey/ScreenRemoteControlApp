package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.KeyController
import com.timatifey.controllers.MouseController
import javafx.beans.binding.Bindings
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*
import java.util.concurrent.Callable

class ScreenControlView : View("") {
    private val clientController: ClientController by inject()
    private val mouseController: MouseController by inject()
    private val keyController: KeyController by inject()
    private val image = clientController.client.screenReceiver.imageScene
    override val root = borderpane {
        title = "${clientController.ip}:${clientController.port}"
        setPrefSize(mouseController.clientWidth.toDouble(), mouseController.clientHeight.toDouble())
        usePrefSize = true
        center {
            style {
                backgroundColor = multi(Color.BLACK)
            }
            imageview(image) {
                isCenterShape = true
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
