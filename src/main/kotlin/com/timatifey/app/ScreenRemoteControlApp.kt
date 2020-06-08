package com.timatifey.app

import com.timatifey.models.server.Server
import com.timatifey.views.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import tornadofx.*

class ScreenRemoteControlApp : App(MainView::class, Styles::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        stage.icons += Image("file:src/main/resources/icon.png")
        super.start(stage)
    }
}

class StartLauncher {
    @Option(name = "-p", metaVar = "port", required = false, usage = "Port for starting server")
    private var port: Int? = null

    fun launch(args: Array<String>) {
        val parser = CmdLineParser(this)
        try {
            parser.parseArgument(*args)
        } catch (e: CmdLineException) {
            println(e.message)
            println("java -jar ScreenRemoteControlApp.jar [-p port]")
            parser.printUsage(System.out)
            return
        }
        if (port != null) {
            Server(isConsole = true).start(port!!)
        } else {
            launch<ScreenRemoteControlApp>(args)
        }
    }
}