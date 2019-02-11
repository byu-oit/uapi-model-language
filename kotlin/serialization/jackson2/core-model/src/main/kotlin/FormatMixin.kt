package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.util.StdConverter
import edu.byu.uapi.model.jsonschema07.Format

@JsonDeserialize(
    converter = FormatConverter::class
)
internal interface FormatMixin {
    @get:JsonValue
    val value: String
}

private val knownFormats: Map<String, Format> by lazy {
    Format::class.sealedSubclasses
        .mapNotNull { it.objectInstance }
        .associateBy { it.value }
        .withDefault { Format.Other(it) }
}

internal class FormatConverter : StdConverter<String, Format>() {
    override fun convert(value: String): Format {
        return knownFormats.getValue(value)
    }
}