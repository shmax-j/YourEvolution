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
    MainLoop.bacteriaTarget ?: return@stage
    root.center = vBox(
        spacing = 5.0,
        alignment = Pos.CENTER,
        padding = Insets(5.0, .0, 5.0, .0)) {

        onKeyPressed = EventHandler { keyEvent ->
            when (keyEvent.code.name) {
                "Esc", "Tab" -> {
                    MainLoop.resume()
                    close()
                }
            }
        }

        BacteriaModificationDef.entries.forEach { modification ->
            val alreadyInstalled = MainLoop.bacteriaTarget!!.modifications.any { it.def == modification }
            val notEnoughSatiety = MainLoop.bacteriaTarget!!.satiety < modification.price

            button(
                text = modification.name,
                disabled = alreadyInstalled or notEnoughSatiety){
                onAction = EventHandler {

                    MainLoop.bacteriaTarget!!.addModification(BacteriaModification(modification))

                    MainLoop.resume()
                    close()
                }
            }
        }
    }

    onCloseRequest = EventHandler {
        MainLoop.resume()
        close()
    }

    MainLoop.pause()
    showAndWait()
}