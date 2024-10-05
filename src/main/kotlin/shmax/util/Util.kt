package shmax.util

import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.transform.Rotate
import shmax.controllers.Main
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*
import kotlin.random.Random

fun resource(path: String?) =
    Main::class.java.classLoader.getResource(path)?.toURI() ?: throw FileNotFoundException(path)


fun fileResource(path: String?) = File(resource(path))

fun fisResource(path: String?) = FileInputStream(fileResource(path))

inline fun <reified T> res(path: String?): T {
    return when (T::class.java) {
        FileInputStream::class.java -> fisResource(path) as T
        Image::class.java -> fisResource(path).use {
            Image(it) as T
        }
        ImageView::class.java -> fisResource(path).use {
            ImageView(Image(it)) as T
        }
        else -> {
            throw IllegalArgumentException("Unsupported resource type: ${T::class.java}")
        }
    }
}

fun loadProperties(path: String): Properties {
    val properties = Properties()

    fisResource(path).use {
        properties.load(it)
    }

    return properties
}


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

fun rollChance(max: Int) = Random.nextInt(max)