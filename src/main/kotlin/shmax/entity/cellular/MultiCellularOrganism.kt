package shmax.entity.cellular

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import shmax.controllers.Main
import shmax.controllers.Main.gL
import shmax.entity.OrganismIntention
import shmax.entity.OrganismState
import shmax.entity.bacteria.Bacteria
import shmax.food.NanoFoodPiece
import shmax.util.*
import kotlin.math.abs
import kotlin.random.Random

class MultiCellularOrganism: Pane() {
    private var topNeighbour: Int = 0
    private var rightNeighbour: Int = 0
    private var bottomNeighbour: Int = 0
    private var leftNeighbour: Int = 0

    private var state = OrganismState.IDLE
    private var intention = OrganismIntention.WALK

    private var gCenter = Point2D(0.0, 0.0)

    private var movingTarget: Point2D? = null

    private var foodTarget: NanoFoodPiece? = null

    private var origin: Point2D = Point2D(0.0, 0.0)

    private var satiety = 0

    private var currentSpeed = .6
    private val maxSpeed = 1.0
    private val idleSpeed = .3

    private var velocity = Point2D(0.0, 0.0)

    private var lCenter = Point2D(0.0, 0.0)

    private val body = mutableListOf<Cellular>(
        Cell(0, 0),
        Cell(1, 0),
    )


    init {
        render()
    }

    private fun moveToPoint(point: Point2D) {
        movingTarget = point
        val direction = (point - Point2D(gCenter.x, gCenter.y)).normalize()

        currentSpeed = when (intention) {
            OrganismIntention.FEED -> maxSpeed
            OrganismIntention.WALK -> idleSpeed
        }

        velocity = direction * currentSpeed
        rotateToPoint(direction)
        state = OrganismState.MOVING
    }

    fun update() {
        origin = Point2D(translateX, translateY)
        gCenter = origin + lCenter

        when (state) {
            OrganismState.IDLE -> {
                val randomX = translateX + Random.nextDouble(700.0, 1400.0)
                val randomY = translateY + Random.nextDouble(700.0, 1400.0)
                moveToPoint(Point2D(randomX, randomY))
            }
            OrganismState.MOVING -> {
                translatePosition = origin + velocity

                if ((origin + lCenter).distance(movingTarget) < 3) {
                    when (intention) {
                        OrganismIntention.FEED -> {
                            satiety += 10
                            foodTarget?.eaten = true
                            state = OrganismState.IDLE
                        }
                        OrganismIntention.WALK -> state = OrganismState.IDLE
                    }
                }
            }
            OrganismState.ROTATING -> {}
            OrganismState.WAITING -> {}
        }
    }

    private fun render() {
        children.clear()

        body.forEach { cellular ->
            when (cellular.type) {
                2 -> {
                    topNeighbour = 0
                    rightNeighbour = 0
                    bottomNeighbour = 0
                    leftNeighbour = 0

                    body.forEach { otherCellular ->
                        if (otherCellular.x == cellular.x && otherCellular.y == cellular.y - 1) {
                            topNeighbour = otherCellular.type
                        }
                        if (otherCellular.x == cellular.x + 1 && otherCellular.y == cellular.y) {
                            rightNeighbour = otherCellular.type
                        }
                        if (otherCellular.x == cellular.x && otherCellular.y == cellular.y + 1) {
                            bottomNeighbour = otherCellular.type
                        }
                        if (otherCellular.x == cellular.x - 1 && otherCellular.y == cellular.y) {
                            leftNeighbour = otherCellular.type
                        }
                    }

                    cellular.graphic.image = Cell.cellsImageMap["$topNeighbour$rightNeighbour$bottomNeighbour$leftNeighbour"]
                    cellular.graphic.fitWidth = Cell.WIDTH
                    cellular.graphic.fitHeight = Cell.HEIGHT

                    cellular.onMouseClicked = EventHandler {
                        if (Main.BTarget != null) {
                            Main.BTarget.children.remove(Bacteria.activeCircle)
                            Main.BTarget = null
                        }
                        Main.MCPTarget = this
                    }

                    children.add(cellular)
                    cellular.translateX = cellular.graphic.fitWidth * cellular.x
                    cellular.translateY = cellular.graphic.fitHeight * cellular.y
                }
            }
        }

        val maxX = body.maxOf { it.x }
        val maxY = body.maxOf { it.y }
        val minX = body.minOf { it.x }
        val minY = body.minOf { it.y }

        lCenter = Point2D(minX * Cell.WIDTH, minY * Cell.HEIGHT)

        val hCellCount = abs(maxX) + abs(maxX) + 1
        val wCellCount = abs(maxY) + abs(maxY) + 1

        lCenter = Point2D(
            lCenter.x + (hCellCount * (Cell.WIDTH / 2)),
            lCenter.y + (wCellCount * (Cell.HEIGHT / 2)),
        )
    }

    fun searchForFood() {
        var searchDistance = 1000.0
        Main.foodList.forEach { food ->
            val distance = food.position.distance(origin)
            if (distance < searchDistance) {
                foodTarget = food
                searchDistance = distance
            }
        }

        foodTarget ?: return Main.printMessage("Can't seem to find food around")

        moveToPoint(foodTarget!!.position)
        intention = OrganismIntention.FEED
    }

    override fun toString() = buildString {
        append(gL("target", "Target"))
        append(":\n")
        append("${gL("satiety", "Satiety")}: $satiety\n")
    }
}