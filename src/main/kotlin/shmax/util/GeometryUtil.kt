package shmax.util

import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.scene.transform.Rotate


operator fun Point2D.minus(other: Point2D?): Point2D = subtract(other)
operator fun Point2D.plus(other: Point2D): Point2D = add(other)
operator fun Point2D.plus(other: Double): Point2D = Point2D(this.x + other, this.y + other)

operator fun Point2D.times(factor: Double): Point2D = multiply(factor)
operator fun Point2D.div(factor: Double): Point2D = multiply(1 / factor)

operator fun Point2D.unaryMinus(): Point2D = Point2D(-this.x, -this.y)

val Scene.dimensions: Point2D
    get() {
        return Point2D(width, height)
    }

val Pane.dimensions: Point2D
    get() {
        return Point2D(width, height)
    }

val Pane.prefDimensions: Point2D
    get() {
        return Point2D(prefWidth, prefHeight)
    }

var Pane.translatePosition: Point2D
    get() = Point2D(translateX, translateY)
    set(value) {
        translateX = value.x
        translateY = value.y
    }

fun Pane.rotateToPoint(point: Point2D) {
    val angle = point.angle(.0, -1.0)
    val completeAngle = if (point.x > 0) angle else -angle
    transforms.clear()
    transforms.add(Rotate(completeAngle, point.x, point.y))
}