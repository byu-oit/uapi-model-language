package edu.byu.uapi.model.serialization

import java.io.StringWriter
import java.io.Writer

interface UAPIWriter<T : Any> {
    fun write(model: T, writer: Writer, format: UAPISerializationFormat = UAPISerializationFormat.JSON, prettyPrint: Boolean = false)

    fun writeToString(model: T, format: UAPISerializationFormat = UAPISerializationFormat.JSON, prettyPrint: Boolean = false): String {
        return StringWriter().use {
            write(model, it, format, prettyPrint)
            it.toString()
        }
    }
}