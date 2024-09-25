package shmax.util

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import shmax.controllers.Main
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

fun resource(path: String?) =
    Main::class.java.classLoader.getResource(path)?.toURI() ?: throw FileNotFoundException(path)


fun fileResource(path: String?) = File(resource(path))

fun fisResource(path: String?) = FileInputStream(fileResource(path))

inline fun <reified T> res(path: String?): T {
    return when (T::class.java) {
        FileInputStream::class.java -> fisResource(path) as T
        Image::class.java -> Image(fisResource(path)) as T
        ImageView::class.java -> ImageView(Image(fisResource(path))) as T
        else -> {
            throw IllegalArgumentException("Unsupported resource type: ${T::class.java}")
        }
    }
}