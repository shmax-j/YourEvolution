package shmax

import javafx.application.Platform
import shmax.controllers.MainLoop
import shmax.error.globalExceptionHandler
import shmax.generated.R


fun main(args: Array<String>) {
    println("Platform startup...")
    Platform.startup {
        globalExceptionHandler {
            println("Loading resources...")
            R.loadResources()
            println("Resources loaded successfully")
            MainLoop().start()
        }
    }
}