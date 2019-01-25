package edu.byu.uapi.model

const val UAPIVersion = "0.1"
const val UAPISchema = "http://api.byu.edu/uapi/v0.1"

val UAPIResourceName = """^[a-z0-9][_a-z0-9]*[a-z0-9]$""".toRegex()
val UAPISubresourceName = """^[a-z0-9][_a-z0-9]*[a-z0-9]$""".toRegex()
val UAPIPropertyName = """^[a-z0-9][_a-z0-9]*[a-z0-9]$""".toRegex()
val UAPIExtensionName = """^x-""".toRegex()

@Throws(UAPIModelException::class)
fun validateResourceNames(path: String, names: Iterable<String>) {
    val found = names.firstOrNull { !UAPIResourceName.matches(it) }
    if (found != null) {
        throw UAPIModelException(
            "$path[$found]",
            "Invalid resource name. Must be alphanumeric with underscores."
        )
    }
}

@Throws(UAPIModelException::class)
fun validateSubresourceNames(path: String, names: Iterable<String>) {
    val found = names.firstOrNull { !UAPISubresourceName.matches(it) }
    if (found != null) {
        throw UAPIModelException(
            "$path[$found]",
            "Invalid subresource name. Must be alphanumeric with underscores."
        )
    }
}

@Throws(UAPIModelException::class)
fun validateExtensions(path: String, extensions: Map<String, Any?>) {
    val found = extensions.keys.firstOrNull { !UAPIExtensionName.matches(it) }
    if (found != null) {
        throw UAPIModelException(
            "$path[$found]",
            "Invalid extension name. Must start with 'x-'."
        )
    }
}