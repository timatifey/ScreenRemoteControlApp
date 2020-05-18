package com.timatifey.views

import com.timatifey.controllers.MainController
import com.timatifey.controllers.MouseController
import com.timatifey.models.client.Client
import javafx.beans.binding.Bindings
import tornadofx.*
import java.util.concurrent.Callable

class ScreenControlView : View("View") {
    private val mainController: MainController by inject()
    private val mouseController: MouseController by inject()
    private val image = Client.screenReceiver.image

    override val root = form {
        setPrefSize(640.0, 500.0)
        fieldset() {
            label("${mainController.ip}:${mainController.port}")
            spacer()
            button("DISCONNECT") {
                action {
                    mouseController.disconnect()
                    find(ScreenControlView::class).replaceWith(MainView::class)
                }
            }
        }
        spacer()
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
        }
        setOnMouseMoved {
            mouseController.setEvent(it!!)
        }
    }
}
