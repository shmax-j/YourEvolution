package shmax.entity.bacteria

import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import shmax.component.imageView
import shmax.util.res

enum class BacteriaModificationDef(
    val label: String,
    val offset: Point2D = Point2D(0.0, 0.0),
    val price: Float = 0f,
    val scale: Double = 1.0,
    val spritePath: String,
    val outside: Boolean = false,
) {
    NONE(
        label = "None",
        spritePath = "sprites/BMods/None.png"
    ),

    FLAGELLUM(
        label = "Flagellum",
        offset = Point2D(-374.0, -50.0),
        price = 10f,
        spritePath = "sprites/BMods/Flagellum.png",
        scale = .25,
        outside = true
    ),

    NUCLEUS(
        label = "Nucleus",
        offset = Point2D(0.0, 0.0),
        price = 25f,
        scale = .25,
        spritePath =  "sprites/BMods/Nucleus.png"
    )
}

class BacteriaModification(val def: BacteriaModificationDef): Pane() {
    init {
        imageView(res(def.spritePath)) {
            translateX = def.offset.x
            translateY = def.offset.y
            scaleX = def.scale
            scaleY = def.scale
        }

        if (def.outside) {
            toBack()
        }
    }
}