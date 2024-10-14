package shmax.util

import javafx.scene.image.Image
import shmax.controllers.MainLoop
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

inline fun <reified T> loadedResource(path: String): Lazy<T> {
    return lazy {
        resourceOf<T>(path)
    }
}

fun loadProperties(path: String): Properties {
    val properties = Properties()

    fisResource(path).use {
        properties.load(it)
    }

    return properties
}



inline fun <reified T> resourceOf(path: String?): T {
    return when (T::class.java) {
        FileInputStream::class.java -> fisResource(path) as T

        Image::class.java -> fisResource(path).use {
            Image(it) as T
        }

        else -> {
            throw IllegalArgumentException("Unsupported resource type: ${T::class.java}")
        }
    }
}

fun fisResource(path: String?) = FileInputStream(fileResource(path))

fun fileResource(path: String?) = File(resourceUri(path))

fun resourceUri(path: String?) =
    MainLoop::class.java.classLoader.getResource(path)?.toURI() ?: throw FileNotFoundException("File not found on specified path: $path")

