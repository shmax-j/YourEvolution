package shmax.error

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import shmax.component.button
import shmax.component.label
import shmax.component.stage
import shmax.component.vBox

@JvmOverloads
fun throwError(title: String = "Error", message: String = "Unknown Error",
               block: Stage.(VBox) -> Unit = {}): Stage {
    return stage(
        title = title,
        modality = Modality.APPLICATION_MODAL,
    ) { root ->
        val stage = this

        val content = vBox(
            alignment = Pos.CENTER,
            spacing = 5.0,
            padding = Insets(10.0, .0, 10.0, .0)
        ) {
            label(message)
            button("OK") {
                onAction = EventHandler { stage.close() }
            }
        }

        root.center = content

        block(content)

        showAndWait()
    }
}