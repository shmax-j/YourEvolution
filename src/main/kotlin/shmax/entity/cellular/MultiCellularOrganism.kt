package shmax.entity.cellular

import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import shmax.controllers.Main
import shmax.controllers.Main.Companion.gL
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

    private var glovalCenter = Point2D(0.0, 0.0)

    private var movingTarget: Point2D? = null

    private var foodTarget: NanoFoodPiece? = null

    private var position: Point2D = Point2D(0.0, 0.0)

    private var satiety = 0

    private var currentSpeed = .6
    private val maxSpeed = 1.0
    private val idleSpeed = .3

    private var velocity = Point2D(0.0, 0.0)

    private var localCenter = Point2D(0.0, 0.0)

    private val body = mutableListOf<Cellular>(
        Cell(0, 0),
        Cell(1, 0),
    )


    init {
        render()
    }

    private fun moveToPoint(point: Point2D) {
        movingTarget = point
        val direction = (point - Point2D(glovalCenter.x, glovalCenter.y)).normalize()

        currentSpeed = when (intention) {
            OrganismIntention.FEED -> maxSpeed
            OrganismIntention.WALK -> idleSpeed
        }

        velocity = direction * currentSpeed
        rotateToPoint(direction)
        state = OrganismState.MOVING
    }

    fun update() {
        position = Point2D(translateX, translateY)
        glovalCenter = position + localCenter

        when (state) {
            OrganismState.IDLE -> {
                val randomX = translateX + Random.nextDouble(700.0, 1400.0)
                val randomY = translateY + Random.nextDouble(700.0, 1400.0)
                moveToPoint(Point2D(randomX, randomY))
            }
            OrganismState.MOVING -> {
                translatePosition = position + velocity

                if ((position + localCenter).distance(movingTarget) < 3) {
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
                        if (Main.bacteriaTarget != null) {
                            Main.bacteriaTarget!!.children.remove(Bacteria.activeCircle)
                            Main.bacteriaTarget = null
                        }
                        Main.multicellularTarget = this
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

        localCenter = Point2D(minX * Cell.WIDTH, minY * Cell.HEIGHT)

        val hCellCount = abs(maxX) + abs(maxX) + 1
        val wCellCount = abs(maxY) + abs(maxY) + 1

        localCenter = Point2D(
            localCenter.x + (hCellCount * (Cell.WIDTH / 2)),
            localCenter.y + (wCellCount * (Cell.HEIGHT / 2)),
        )
    }

    fun searchForFood() {
        var searchDistance = 1000.0
        Main.foodList.forEach { food ->
            val distance = food.position.distance(position)
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
        append(gL("target"))
        append(":\n")
        append("${gL("satiety")}: $satiety\n")
    }
}