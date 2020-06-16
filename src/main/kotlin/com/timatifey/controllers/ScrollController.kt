package com.timatifey.controllers

import com.timatifey.models.data.Scroll
import javafx.scene.input.ScrollEvent
import tornadofx.*

class ScrollController: Controller() {
    private val clientController: ClientController by inject()

    fun sendScrollEvent(eventScroll: ScrollEvent) {
        if (clientController.mouseCheck.value) {
            clientController.client.scrollEventSender.putEvent(Scroll(
                    Scroll.ScrollEventType.valueOf(eventScroll.eventType.name),
                    eventScroll.deltaX,
                    eventScroll.deltaY,
                    eventScroll.totalDeltaX,
                    eventScroll.totalDeltaY,
                    eventScroll.textDeltaX,
                    eventScroll.textDeltaY,
                    eventScroll.multiplierX,
                    eventScroll.multiplierY
            ))
        }
    }
}