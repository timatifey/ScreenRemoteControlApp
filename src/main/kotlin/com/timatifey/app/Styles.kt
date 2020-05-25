package com.timatifey.app

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles: Stylesheet() {
    companion object {
        val mainColor = c("#04afff")
        val mainDarkColor = c("#007ab3")
        val backColor = c("#f9feff")
        val wrapper by cssclass()
    }
    init {
        s(wrapper) {
            backgroundColor = multi(backColor)
            s(button) {
                padding = box(10.0.px, 5.0.px)
                textFill = Color.WHITE
                backgroundColor = multi(mainColor)
                fontWeight = FontWeight.EXTRA_BOLD
                fontFamily = "Arial Black"
                and(hover) {
                    backgroundColor = multi(mainDarkColor)
                }
            }
            s(toggleButton, checkBox) {
                padding = box(10.0.px, 5.0.px)
                textFill = Color.WHITE
                backgroundColor = multi(mainColor)
                fontWeight = FontWeight.EXTRA_BOLD
                fontFamily = "Arial Black"
                borderRadius = multi(box(10.px))
                and(hover) {
                    backgroundColor = multi(mainDarkColor)
                }
            }
            s(checkBox) {
                padding = box(5.0.px)
            }
            s(label) {
                fontFamily = "Arial"
                fontSize = 14.px
                textFill = Color.BLACK
                padding = box(10.0.px, 0.px, 0.px, 0.px)
                fontWeight = FontWeight.BOLD
            }
            s(textField) {
                fontSize = 16.px
            }
            borderRadius = multi(box(20.px))
        }

    }
}