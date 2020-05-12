package com.timatifey.views

import tornadofx.*

class Main : View("My View") {
    override val root = vbox {
        setPrefSize(1440.0, 900.0)
        imageview("src/main/resources/screen.png")
    }
}
