import com.squareup.kotlinpoet.*
import javafx.scene.image.Image
import shmax.controllers.MainLoop
import java.io.File
import kotlin.reflect.KClass

data class ResourceDef(
    val path: String,
    val name: String,
) {
    val propertyName: String
        get() {
            val prefix = path.trim('/').substringBefore(name).replace("/", "_").lowercase()
            return if (prefix.isEmpty()) name.lowercase() else "${prefix}${name.lowercase()}"
        }
}

fun main(){
    println("Generating resources...")
    val imagesClassName = ClassName("", "ImageResources")

    val imageResources = createResources(imagesClassName, "assets/images", Image::class)

    val rCompanion = TypeSpec.companionObjectBuilder()
        .addProperty(PropertySpec.builder("images", imagesClassName)
            .initializer("ImageResources()")
            .build())
        .addFunction(FunSpec.builder("loadResources")
            .addStatement("images.load()")
            .build())
        .build()

    val rClass = TypeSpec.classBuilder("R")
        .addType(imageResources)
        .addType(rCompanion)

    val file = FileSpec.builder("shmax.generated", "Resources")
        .addType(rClass.build())
        .addImport("shmax.util", "loadedResource")
        .build()

    file.writeTo(File("src/main/kotlin"))
}

private fun createResources(className: ClassName, assetsPath: String, forType: KClass<*>): TypeSpec {
    val path = MainLoop::class.java.classLoader.getResource(assetsPath)!!.toURI()

    val resourcesDirectory = File(path)

    val resources = collectResources(resourcesDirectory)

    val loadFunction = FunSpec.builder("load")

    val clazz = TypeSpec.classBuilder(className)


    resources.forEach { resDef ->
        clazz
            .addProperty(
                PropertySpec.builder(resDef.propertyName, forType)
                    .delegate("loadedResource(\"${assetsPath}${resDef.path}\")")
                    .build()
            )
        loadFunction.addStatement(resDef.propertyName)
    }


    return clazz.addFunction(loadFunction.build()).build()
}

private fun collectResources(directory: File): List<ResourceDef> {
    val resourcesDef = mutableListOf<ResourceDef>()

    directory.listFiles()?.forEach { file ->
        collectResourcesRecursive(file, resourcesDef, "/${file.name}")
    }

    return resourcesDef
}

private fun collectResourcesRecursive(next: File, collection: MutableList<ResourceDef>, path: String) {
    if (next.isDirectory) {
        next.listFiles()?.forEach {
            collectResourcesRecursive(it, collection, "$path/${it.name}")
        }
    } else {
        val name = next.nameWithoutExtension
        collection.add(ResourceDef(path, name))
    }
}