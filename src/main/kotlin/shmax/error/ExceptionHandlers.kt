package shmax.error

fun globalExceptionHandler(block: () -> Unit) = try {
    block()
} catch (ex: Exception) {
    throwError(title = "Error", message = "${ex.message}")
    throw ex
}
