package shmax.controllers

import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import shmax.component.iconButton
import shmax.component.stage
import shmax.component.vBox
import shmax.entity.bacteria.Bacteria
import shmax.entity.cellular.MultiCellularOrganism
import shmax.food.NanoFoodPiece
import shmax.util.*

class MainLoop {
    companion object {
        val objectRoot = Pane()
        var lazyRoot: BorderPane? = null
        var loop: AnimationTimer? = null
        var onPause = false
        val startProperties = loadProperties("start.properties")
        val languagesList = loadProperties("languages/language.properties")
        val localizationMap = loadProperties(languagesList[startProperties["language"]].toString())
        var foodSpawnInterval = 50
        var targetFocusMode = false
        val foodList = mutableSetOf<NanoFoodPiece>()
        val bacteriaList = mutableSetOf<Bacteria>()
        val multiCellularList = mutableSetOf<MultiCellularOrganism>()
        var cameraDirection = Point2D(0.0, 0.0)
        var bacteriaTarget: Bacteria? = null
        var multicellularTarget: MultiCellularOrganism? = null
        var cameraSpeed = 2.0
        var paused = false

        // Icons
        val multiplyIcon = res<Image>("icons/bacteria/multiply.png")
        val focusIcon = res<Image>("icons/cameraFocus.png")
        val feedIcon = res<Image>("icons/eat.png")
        val addModificationIcon = res<Image>("icons/bacteria/addModification.png")
        val formMultiCellularIcon = res<Image>("icons/bacteria/makeMCP.png")
        val deselectIcon = res<Image>("icons/main/deselect.png")

        var message = Label("")
        var messageTimer = 0

        fun printMessage(text: String) {
            message.text = text
            messageTimer = 50
        }

        fun pause() {
            paused = true
            printMessage("Paused")
            messageTimer = -1
            loop?.stop()
        }

        fun resume() {
            paused = false
            printMessage("Resumed")
            loop?.start()
        }

        fun gL(key: String): String {
            return (localizationMap[key] ?: key).toString()
        }
    }

    fun start() = stage(
        title = gL("title"),
        sceneWidth = 600.0,
        sceneHeight = 600.0,
        sceneFill = Color.rgb(202, 239, 242)
    ) { root ->
        lazyRoot = root
        root.children.add(objectRoot)

        root.bottom = message

        root.scene.onKeyPressed = EventHandler { event ->
            when (event.code) {
                KeyCode.W -> {
                    targetFocusMode = false
                    cameraDirection = Point2D(cameraDirection.x, 1.0)
                }
                KeyCode.S -> {
                    targetFocusMode = false
                    cameraDirection = Point2D(cameraDirection.x, -1.0)
                }
                KeyCode.D -> {
                    targetFocusMode = false
                    cameraDirection = Point2D(-1.0, cameraDirection.y)
                }
                KeyCode.A -> {
                    targetFocusMode = false
                    cameraDirection = Point2D(1.0, cameraDirection.y)
                }
                KeyCode.SHIFT -> {
                    cameraSpeed = 5.0
                }

                else -> {}
            }
        }

        root.scene.onKeyReleased = EventHandler { event ->
            when (event.code) {
                KeyCode.W -> {
                    cameraDirection = Point2D(cameraDirection.x, .0)
                }
                KeyCode.S -> {
                    cameraDirection = Point2D(cameraDirection.x, .0)
                }
                KeyCode.D -> {
                    cameraDirection = Point2D(.0, cameraDirection.y)
                }
                KeyCode.A -> {
                    cameraDirection = Point2D(.0, cameraDirection.y)
                }
                KeyCode.SHIFT -> {
                    cameraSpeed = 2.0
                }
                KeyCode.P -> {
                    if (paused) resume() else pause()
                }

                else -> {}
            }
        }

        val bacteriaTools = vBox {
            alignment = Pos.TOP_RIGHT
            spacing = 0.0
            maxHeight = 150.0

            // TODO: keybinds
            iconButton(multiplyIcon, "Multiply") {
                onAction = EventHandler {
                    bacteriaTarget ?: return@EventHandler

                    if (bacteriaTarget?.canMultiply == true) {
                        bacteriaTarget?.updateInheritanceInfo()
                        bacteriaTarget?.satiety = bacteriaTarget!!.satiety / 2
                        val newTarget = Bacteria(
                            bacteriaTarget!!.translateX,
                            bacteriaTarget!!.translateY,
                            bacteriaTarget)

                        bacteriaList += newTarget
                        objectRoot.children += newTarget
                    }
                }
            }

            iconButton(feedIcon, "Feed") {
                onAction = EventHandler {
                    bacteriaTarget?.searchForFood(foodList)
                }
            }

            iconButton(addModificationIcon, "Add modification") {
                onAction = EventHandler { showAddModificationModal() }
            }

            iconButton(formMultiCellularIcon, "Form multicellular organism") {
                onAction = EventHandler {
                    bacteriaTarget?.makeMultiCellularOrganism(
                        bacteriaTarget!!,
                        bacteriaTarget?.searchForPartner(bacteriaList)
                    )
                }
            }

            iconButton(focusIcon, "Lock camera") {
                onAction = EventHandler { targetFocusMode = !targetFocusMode }
            }

            iconButton(deselectIcon, "Deselect") { onAction = EventHandler {  bacteriaTarget = null } }
        }

        val multiCellularTools = vBox {
            alignment = Pos.TOP_RIGHT
            maxHeight = 50.0

            iconButton(focusIcon, "Lock camera") {
                onMouseClicked = EventHandler { targetFocusMode = !targetFocusMode }
            }

            iconButton(feedIcon, "Feed") {
                onMouseClicked = EventHandler { multicellularTarget?.searchForFood()}
            }

            iconButton(deselectIcon, "Deselect") { multicellularTarget = null }
        }

        val targetInfo = Label("Select something")

        root.left = vBox {
           children.add(targetInfo)
        }
        objectRoot.background = Background.EMPTY

        bacteriaList += Bacteria(
            root.scene.width / 2,
            root.scene.height / 2,
        ).also { objectRoot.children.add(it) }

        loop = object: AnimationTimer() {
            override fun handle(now: Long) {
                bacteriaList.removeIf { it.remove }

                bacteriaList.forEach { it.update(foodList) }
                multiCellularList.forEach { it.update() }

                root.right = bacteriaTarget?.let { bacteriaTools }
                          ?: multicellularTarget?.let { multiCellularTools }

                val target = bacteriaTarget ?: multicellularTarget

                target?.apply {
                    if (targetFocusMode) {
                        objectRoot.translatePosition = -this@apply.translatePosition + (scene.dimensions / 2.0)
                    }
                }

                targetInfo.text = target?.toString() ?: gL("choose_target")

                if (foodSpawnInterval == 0) {
                    foodSpawnInterval = 200
                    val spawned = NanoFoodPiece()
                    foodList.add(spawned)
                    objectRoot.children += spawned
                    spawned.toBack()
                }
                foodSpawnInterval--

                if(messageTimer > 0) {
                    messageTimer--
                }
                if (messageTimer == 0) {
                    message.text = ""
                }

                foodList.forEach {
                    if (it.eaten) {
                        objectRoot.children.remove(it)
                    }
                }

                foodList.removeIf { it.eaten }

                objectRoot.translatePosition += (cameraDirection * cameraSpeed)
            }
        }

        loop?.start()
        show()
    }
}