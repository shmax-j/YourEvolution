package shmax.entity.bacteria

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.transform.Rotate
import shmax.controllers.MainLoop
import shmax.controllers.MainLoop.Companion.gL
import shmax.entity.OrganismState
import shmax.entity.cellular.MultiCellularOrganism
import shmax.food.NanoFoodPiece
import shmax.generated.R
import shmax.util.*
import kotlin.math.min
import kotlin.math.round
import kotlin.random.Random

class Bacteria(
    x: Double,
    y: Double,
    parent: Bacteria? = null
): Pane() {
    companion object {
        val activeCircle = ImageView(R.images.sprite_active).apply {
            fitHeight = 75.0
            fitWidth = 75.0
        }
        const val GRAPHICS_HEIGHT = 48.0
        const val GRAPHICS_WIDTH = 69.0
    }

    val modifications = mutableSetOf<BacteriaModification>()

    private var position = Point2D(.0, .0)
    private var direction = Point2D(.0, .0)
    private var currentVelocity = 1.0
    private val baseVelocity = 1.0
    private var hungerThreshold = 10f
    var satiety = 10f
    private var maxSatiety = 100f
    val canMultiply
        get() = satiety >= 20f
    private var foodTarget = NanoFoodPiece()
    private var state = OrganismState.IDLE
    var remove = false
    private var movingTarget = Point2D(0.0, 0.0)
    private val graphics = ImageView(R.images.sprite_bacteria_bacteria1)

    private var caller = "idle"

    private var partnerTarget: Bacteria? = null
    private var waitingForPartner = false
    private var waitingTime = 1f
    private var modificationInheritanceChance = 20f

    private var nucleus = FloatArray(4)


    init {
        graphics.fitWidth = GRAPHICS_WIDTH
        graphics.fitHeight = GRAPHICS_HEIGHT

        graphics.x = -GRAPHICS_WIDTH / 2
        graphics.y = -GRAPHICS_HEIGHT / 2

        modifications += BacteriaModification(BacteriaModificationDef.NONE)

        activeCircle.x = -activeCircle.fitWidth / 2
        activeCircle.y = -activeCircle.fitHeight / 2

        translatePosition = Point2D(x, y)

        prefHeight = graphics.fitHeight
        prefWidth = graphics.fitWidth

        children.add(graphics)
        currentVelocity = baseVelocity

        inherit(parent)

        graphics.onMouseClicked = EventHandler {
            if (MainLoop.bacteriaTarget != this) {
                MainLoop.bacteriaTarget = this
                MainLoop.bacteriaList.forEach { bacteria ->
                    bacteria.children.remove(activeCircle)
                }
                children.add(activeCircle)
            } else {
                MainLoop.bacteriaTarget = null
                children.remove(activeCircle)
            }
        }
    }

    fun update(foodPeaces: Set<NanoFoodPiece>) {
        if (MainLoop.bacteriaTarget != this) {
            children.remove(activeCircle)
        }

        satiety -= .006f

        val frontVector = translatePosition + (direction * (graphics.fitWidth / 2))

        when (state) {
            OrganismState.IDLE -> {
                currentVelocity = baseVelocity / 4
                if (satiety < hungerThreshold) {
                    searchForFood(foodPeaces)
                }
                moveToPoint(translatePosition + Random.nextDouble(700.0, 1400.0))
            }
            OrganismState.MOVING -> {
                if (caller == "fs" && foodTarget !in MainLoop.objectRoot.children) {
                    searchForFood(MainLoop.foodList)
                }
                if (frontVector.distance(movingTarget) < frontVector.distance(frontVector + (direction * currentVelocity))) {
                    when (caller) {
                        "fs" -> {
                            eatFoodTarget()
                            caller = "idle"
                        }
                        "mcp" -> if (partnerTarget?.waitingForPartner == true) {
                            bornMultiCellularOrganism()
                        } else waitingForPartner = true
                    }
                    translatePosition += (movingTarget - frontVector)
                } else {
                    translatePosition += (direction * currentVelocity)
                }
            }
            OrganismState.ROTATING -> {}
            OrganismState.WAITING -> {
                waitingTime--
                if (waitingTime <= 0 && caller == "idle") {
                    state = OrganismState.IDLE
                }
            }
        }

        position = translatePosition
    }

    private fun moveToPoint(point: Point2D) {
        movingTarget = point
        direction = (point - translatePosition).normalize()
        val angle = calculateAngle(direction)

        transforms.clear()
        transforms.add(Rotate(angle))
        state = OrganismState.MOVING
    }

    private fun calculateAngle(point: Point2D): Double {
        val angle = point.angle(1.0, 0.0)
        return if (point.y > 0) angle else -angle
    }

    fun updateInheritanceInfo() {
        nucleus[0] = satiety
        nucleus[1] = maxSatiety
        nucleus[2] = hungerThreshold
        nucleus[3] = modificationInheritanceChance
    }

    private fun inherit(parent: Bacteria?) {
        parent ?: return
        parent.modifications.forEach { modification ->
            val params = parent.nucleus
            if (rollChance(100) <= params[3]) {
                addModification(modification)
            }
            val randomizeParam = { Random.nextInt(1, 2) }
            satiety = if (rollChance(100) <= 40) {
                params[0] + randomizeParam()
            } else params[0]

            maxSatiety = if (rollChance(100) <= 40) {
                params[1] + randomizeParam()
            } else params[1]

            hungerThreshold = if (rollChance(100) < 40) {
                params[2] + randomizeParam()
            } else params[2]

            modificationInheritanceChance = if (rollChance(100) < 40) {
                params[3] + randomizeParam()
            } else params[3]
        }
    }

    fun addModification(modification: BacteriaModification) {
        if (modification.def != BacteriaModificationDef.NONE) {
            modifications.add(modification)
            children.add(modification)
        }

        modifications.removeIf { it.def == BacteriaModificationDef.NONE }
        satiety -= modification.def.price
        if (modification.def.outside) {
            modification.toBack()
        }
    }

    fun searchForFood(foodList: Set<NanoFoodPiece>) {
        if (satiety >= maxSatiety - 1) {
            MainLoop.printMessage("Bacteria is full")
        }

        val closestFood = foodList.minByOrNull { translatePosition.distance(it.translatePosition) }

        val tooFarAway = closestFood == null || closestFood.position.distance(translatePosition) > 1000

        if (tooFarAway) {
            return MainLoop.printMessage("Theres seems to be no food close enough")
        }

        foodTarget = closestFood ?: foodTarget

        caller = "fs"
        currentVelocity = baseVelocity
        moveToPoint(foodTarget.position)
    }

    private fun eatFoodTarget() {
        satiety = min(maxSatiety, satiety + (foodTarget.foodValue * 10).toFloat())
        foodTarget.eaten = true
        waitingTime = 120f
        state = OrganismState.WAITING
        caller = "idle"
    }

    fun searchForPartner(potentialPartners: Set<Bacteria>): Bacteria? {
        val closestBacteria = potentialPartners.filter { it != this }
            .maxByOrNull { it.position.distance(position) }

        closestBacteria ?:  return null

        val tooFarAway = closestBacteria.position.distance(position) > 1000

        if (tooFarAway) {
            return null
        }

        currentVelocity = baseVelocity
        return closestBacteria
    }

    fun makeMultiCellularOrganism(first: Bacteria, second: Bacteria?) {
        second ?: return MainLoop.printMessage("No other bacterias close enough")

        partnerTarget = second
        partnerTarget?.partnerTarget = first

        if (first.position.distance(second.position) > 50) {
            moveToPoint(first.position.midpoint(second.position))
            second.moveToPoint(second.position.midpoint(first.position))
            partnerTarget?.caller = "mcp"
            caller = "mcp"
        } else {
            bornMultiCellularOrganism()
        }
    }

    private fun bornMultiCellularOrganism() {
        partnerTarget ?: return
        MainLoop.bacteriaTarget ?: return
        val organism = MultiCellularOrganism()
        organism.translatePosition = MainLoop.bacteriaTarget!!.translatePosition
        MainLoop.multiCellularList.add(organism)
        MainLoop.objectRoot.children.add(organism)
        if (MainLoop.bacteriaTarget == this || MainLoop.bacteriaTarget == partnerTarget) {
            destroy()
            partnerTarget!!.destroy()
            MainLoop.bacteriaTarget = null
            MainLoop.multiCellularList += organism
        } else {
            destroy()
            partnerTarget!!.destroy()

        }
    }

    private fun destroy() {
        MainLoop.objectRoot.children.remove(this)
        remove = true
    }

    override fun toString() = buildString {
        append(gL("target")).append(":\n")
        append(gL("satiety")).append(" - ")
        append(round(satiety).toInt())
    }

    fun canMultiply(): Boolean  =  satiety >= 20f
}