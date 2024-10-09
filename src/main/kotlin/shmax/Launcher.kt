package shmax

import javafx.application.Platform
import shmax.controllers.MainLoop
import shmax.error.globalExceptionHandler


fun main(args: Array<String>) {
    Platform.startup {
        globalExceptionHandler {
            MainLoop().start()
        }
    }
}