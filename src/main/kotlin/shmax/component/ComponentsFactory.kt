package shmax.component

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Modality
import javafx.stage.Stage

fun stage(
    title: String = "Stage",
    modality: Modality = Modality.NONE,
    sceneWidth: Double = -1.0,
    sceneHeight: Double = -1.0,
    sceneFill: Paint = Color.WHITE,
    block: Stage.(root: BorderPane) -> Unit = {}): Stage {

    val stage = Stage()
    with(stage) {
        val root = BorderPane()
        val scene = Scene(root, sceneWidth, sceneHeight)
        this.title = title
        initModality(modality)
        this.scene = scene
        block(root)
    }
    return stage
}

fun Pane.vBox(
    spacing: Double = 0.0,
    alignment: Pos = Pos.TOP_LEFT,
    padding: Insets = Insets(0.0, 0.0, 0.0, 0.0),
    block: VBox.() -> Unit) {

    children.add(shmax.component.vBox(spacing, alignment, padding, block))
}

fun vBox(
    spacing: Double = 0.0,
    alignment: Pos = Pos.TOP_LEFT,
    padding: Insets = Insets(0.0, 0.0, 0.0, 0.0),
    block: VBox.() -> Unit): VBox {

    val box = VBox(spacing)
    box.alignment = alignment
    box.padding = padding
    box.block()
    return box
}

fun Pane.label(text: String) {
    val label = Label(text)
    children.add(label)
}

fun button(text: String = "", disabled: Boolean = false, block: Button.() -> Unit): Button {
    val button = Button(text)
    button.isDisable = disabled
    button.block()
    return button
}

fun Pane.button(text: String, disabled: Boolean = false, block: Button.() -> Unit) {
    children.add(shmax.component.button(text, disabled, block))
}

fun pane(block: Pane.() -> Unit) = Pane().apply { block() }

fun Pane.imageView(image: Image, block: ImageView.() -> Unit = {}) {
    val imageView = ImageView(image)
    imageView.block()
    children.add(imageView)
}

fun Pane.iconButton(image: Image,
               tooltipText: String? = null,
               block: Button.() -> Unit = {}) {
    children.add(shmax.component.iconButton(image, tooltipText, block))
}
