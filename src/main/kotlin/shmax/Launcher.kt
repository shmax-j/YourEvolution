package shmax

import javafx.application.Platform
import shmax.controllers.Main


fun main(args: Array<String>) {
    Platform.startup {
        Main().start()
    }
}