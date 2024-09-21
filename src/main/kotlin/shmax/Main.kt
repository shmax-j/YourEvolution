package shmax

import javafx.application.Platform
import javafx.stage.Stage
import shmax.controllers.Main


fun main(args: Array<String>) {
    Platform.startup {
        Main().start(Stage())
    }
}