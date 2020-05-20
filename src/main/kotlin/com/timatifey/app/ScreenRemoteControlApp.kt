package com.timatifey.app

import com.timatifey.views.MainView
import javafx.stage.Stage
import tornadofx.*

class Main : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}