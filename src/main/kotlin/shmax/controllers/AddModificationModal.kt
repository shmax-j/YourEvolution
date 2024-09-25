package shmax.controllers

import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import shmax.entities.bacteria.BacteriaModifications
import shmax.component.button
import shmax.component.stage
import shmax.component.vBox

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

        BacteriaModifications.entries.forEach { modification ->
            val alreadyInstalled = modification.mod in Main.BTarget.modifications
            val notEnoughSatiety = Main.BTarget.satiety < modification.mod.price

            button(
                text = modification.name,
                disabled = alreadyInstalled or notEnoughSatiety) {

                Main.BTarget.addModification(modification.mod)

                Main.resume()
                close()
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