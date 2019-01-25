package edu.byu.uapi.model

class UAPIModelException(
    val path: String,
    val actualError: String,
    cause: Throwable? = null
): RuntimeException(
    "Model error at $path: $actualError",
    cause
)