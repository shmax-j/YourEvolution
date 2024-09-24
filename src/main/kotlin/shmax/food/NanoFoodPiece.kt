package shmax.food

import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import shmax.controllers.Main

class NanoFoodPiece : Pane() {
    var eaten: Boolean = false
    val position: Point2D
    val foodValue: Double = Math.random() + .5

    init {
        val graphics = Rectangle(11.0, 11.0, Color.rgb(150, 144, 107))
        graphics.scaleX = foodValue
        graphics.scaleY = foodValue

        children.add(graphics)

        val spawnTargetX = Main.BTarget?.translateX ?: Main.MCPTarget?.translateX ?: 0.0
        val spawnTargetY = Main.BTarget?.translateY ?: Main.MCPTarget?.translateY ?: 0.0

        translateX = spawnTargetX + (Math.random() * 1200 - 600)
        translateY = spawnTargetY + (Math.random() * 1200 - 600)

        position = Point2D(translateX + graphics.width / 2, translateY + graphics.height / 2)
    }
}
