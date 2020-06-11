package com.timatifey.controllers

import com.timatifey.models.data.Scroll
import javafx.scene.input.ScrollEvent
import tornadofx.*

class ScrollController: Controller() {
    private val clientController: ClientController by inject()

    fun sendScrollEvent(eventScroll: ScrollEvent) {
        if (clientController.mouseCheck.value) {
            clientController.client.scrollEventSender.putScrollEvent(Scroll(
                    Scroll.ScrollEventType.valueOf(eventScroll.eventType.name),
                    eventScroll.totalDeltaY
            ))
        }
    }
}