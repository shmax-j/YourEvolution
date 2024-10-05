package shmax.entity.cellular

import javafx.scene.image.ImageView
import javafx.scene.layout.Pane

open class Cellular(
    val type: Int,
    val x: Int,
    val y: Int,
    var graphic: ImageView
) : Pane()