package shmax.entity.bacteria

import javafx.geometry.Point2D
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import shmax.component.imageView
import shmax.generated.R

enum class BacteriaModificationDef(
    val label: String,
    val offset: Point2D = Point2D(0.0, 0.0),
    val price: Float = 0f,
    val scale: Double = 1.0,
    val sprite: Image,
    val outside: Boolean = false,
) {
    NONE(
        label = "None",
        sprite = R.images.sprite_modification_none
    ),

    FLAGELLUM(
        label = "Flagellum",
        offset = Point2D(-374.0, -50.0),
        price = 10f,
        sprite = R.images.sprite_modification_flagellum2,
        scale = .25,
        outside = true
    ),

    NUCLEUS(
        label = "Nucleus",
        offset = Point2D(0.0, 0.0),
        price = 25f,
        scale = .25,
        sprite = R.images.sprite_modification_nucleus
    )
}

class BacteriaModification(val def: BacteriaModificationDef): Pane() {
    init {
        imageView(def.sprite) {
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