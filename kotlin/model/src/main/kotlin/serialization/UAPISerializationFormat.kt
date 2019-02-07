package edu.byu.uapi.model.serialization

enum class UAPISerializationFormat(
    val extension: String,
    val mime: String
) {
    JSON("json", "application/json"),
    YAML("yml", "text/x-yaml")
}