package com.timatifey.app

import javafx.css.FontFace
import javafx.geometry.Orientation
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import tornadofx.*
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.util.Duration
import javax.swing.text.StyledEditorKit

class Styles: Stylesheet() {
    companion object {
        val mainColor = c("#04afff")
        val mainDarkColor = c("#007ab3")
        val wrapper by cssclass()
    }
    init {
        s(wrapper) {
            backgroundColor = multi(Color.WHITE)
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
            s(tab) {
                backgroundColor = multi(mainColor)
                padding = box(5.px, 33.px)
                fontFamily = "Arial Black"
                fontSize = 13.px
            }
            s(label) {
                fontFamily = "Arial"
                fontSize = 14.px
                textFill = Color.BLACK
            }
            borderRadius = multi(box(20.px))
        }

    }
}