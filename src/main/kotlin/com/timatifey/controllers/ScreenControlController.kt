package com.timatifey.controllers

import com.timatifey.views.MainView
import com.timatifey.views.ScreenControlView
import tornadofx.*

class ScreenControlController: Controller() {

    fun disconnect() {
        find(ScreenControlView::class).replaceWith(MainView::class)
    }

    val urlImage = "screen.png"

}