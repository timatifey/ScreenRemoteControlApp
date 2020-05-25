package com.timatifey.app

import com.timatifey.views.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*

class Main : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.icons += Image("file:src/main/resources/icon.png")
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    launch<Main>(args)
}