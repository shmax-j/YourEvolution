package shmax.component

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView

const val ICON_IMAGE_SIZE = 40.0

@JvmOverloads
fun iconButton(image: Image,
               tooltipText: String? = null,
               block: Button.() -> Unit = {}) = button {
    val imageView = ImageView(image)
    graphic = imageView

    imageView.fitWidth = ICON_IMAGE_SIZE
    imageView.fitHeight = ICON_IMAGE_SIZE

    prefWidth = ICON_IMAGE_SIZE
    prefHeight = ICON_IMAGE_SIZE

    opacity = 0.7
    style = "-fx-background-color: transparent;" +
            "-fx-padding: 0"

    onMouseEntered = EventHandler { opacity = .9 }
    onMouseExited = EventHandler { opacity = .7 }

    if (tooltipText != null) {
        tooltip = Tooltip(tooltipText)
    }

    block()
}