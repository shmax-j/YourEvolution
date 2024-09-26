package shmax.controllers

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import shmax.component.button
import shmax.component.stage
import shmax.component.vBox
import shmax.entity.bacteria.BacteriaModification
import shmax.entity.bacteria.BacteriaModificationDef

fun showAddModificationModal() = stage(
    title = "Add modification"
) { root ->
    root.center = vBox(
        spacing = 5.0,
        alignment = Pos.CENTER,
        padding = Insets(5.0, .0, 5.0, .0)) {

        onKeyPressed = EventHandler { keyEvent ->
            when (keyEvent.code.name) {
                "Esc", "Tab" -> {
                    Main.resume()
                    close()
                }
            }
        }

        BacteriaModificationDef.entries.forEach { modification ->
            val alreadyInstalled = Main.BTarget.modifications.any { it.def == modification }
            val notEnoughSatiety = Main.BTarget.satiety < modification.price

            button(
                text = modification.name,
                disabled = alreadyInstalled or notEnoughSatiety){
                onAction = EventHandler {

                    Main.BTarget.addModification(BacteriaModification(modification))

                    Main.resume()
                    close()
                }
            }
        }
    }

    onCloseRequest = EventHandler {
        Main.resume()
        close()
    }

    Main.pause()
    showAndWait()
}