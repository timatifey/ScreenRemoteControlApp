package com.timatifey.core.client

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ScreenSender {
    fun takeScreen(): BufferedImage {
        return Robot().createScreenCapture(Rectangle(Toolkit.getDefaultToolkit().screenSize))
    }

    fun start() {
        /*val client = Client(::takeScreen)
        client.startConnection("localhost", 6666)
        /
         */
    }
}

/*fun main() {
    ScreenSender.takeScreen()
}*/