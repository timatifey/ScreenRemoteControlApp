package com.timatifey.views

import com.timatifey.controllers.ClientController
import com.timatifey.controllers.KeyController
import com.timatifey.controllers.MouseController
import javafx.beans.binding.Bindings
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*
import com.timatifey.models.senders.takeScreenSize
import javafx.geometry.NodeOrientation
import java.util.concurrent.Callable

class ScreenControlView : View() {
    private val clientController: ClientController by inject()
    private val mouseController: MouseController by inject()
    private val keyController: KeyController by inject()
    private val image = clientController.client.screenReceiver.imageScene

    override val root = borderpane {
        title = "${clientController.ip}:${clientController.port}"
        setPrefSize(takeScreenSize().width.toDouble(), takeScreenSize().height.toDouble())
        bottom {
            button("") {
                useMaxWidth = true
                style {
                    backgroundColor = multi(Color.BLACK)
                }
                addEventHandler(KeyEvent.ANY) {
                    keyController.sendKeyEvent(it!!)
                }
            }
        }
        center {
            style {
                backgroundColor = multi(Color.BLACK)
            }
            paddingAll = 5.0
            imageview(image) {
                nodeOrientation = NodeOrientation.INHERIT
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
        }
    }

    override fun onDock() {
        currentWindow?.setOnCloseRequest {
            it.consume()
            confirm("Вы уверены, что хотите разорвать соединение?") {
                currentStage?.hide()
                clientController.stopConnection()
            }
        }
    }

}
