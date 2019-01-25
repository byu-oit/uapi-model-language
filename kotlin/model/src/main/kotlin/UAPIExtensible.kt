package edu.byu.uapi.model

interface UAPIExtensible {
    val extensions: UAPIExtensions

    fun addExtension(key: String, value: Any?) {
        extensions[key] = value
    }
}

typealias UAPIExtensions = MutableMap<String, Any?>
