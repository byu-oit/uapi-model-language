package edu.byu.uapi.model.serialization.jackson2

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.util.StdConverter
import edu.byu.uapi.model.*

@JsonSerialize(
    converter = UAPIPropertyMixin.OutConverter::class
)
@JsonDeserialize(
    converter = UAPIPropertyMixin.InConverter::class
)
internal interface UAPIPropertyMixin {
    class OutConverter : StdConverter<UAPIProperty, JsonUAPIProperty>() {
        override fun convert(value: UAPIProperty): JsonUAPIProperty = JsonUAPIProperty(value)
    }

    class InConverter : StdConverter<JsonUAPIProperty, UAPIProperty>() {
        override fun convert(value: JsonUAPIProperty): UAPIProperty = value.unwrapped
    }

    data class JsonUAPIProperty(
        val name: String,

        val value: UAPIValuePropertyDefinition?,
        val valueArray: UAPIValueArrayPropertyDefinition?,
        val `object`: UAPIObjectPropertyDefinition?,
        val objectArray: UAPIObjectArrayPropertyDefinition?,

        val apiTypes: Set<UAPIApiType>,
        val hasDescription: Boolean = false,
        val hasLongDescription: Boolean = false,
        val hasDisplayLabel: Boolean = false,
        val nullable: Boolean = false,
        override val documentation: String? = null,
        override val `$comment`: String? = null,
        override val extensions: UAPIExtensions = mutableMapOf()
    ) : UAPIDocumentable, UAPICommentable, UAPIExtensible {
        constructor(
            from: UAPIProperty
        ) : this(
            name = from.name,

            value = from.definition.takeIfType(),
            valueArray = from.definition.takeIfType(),
            `object` = from.definition.takeIfType(),
            objectArray = from.definition.takeIfType(),

            apiTypes = from.apiTypes,
            hasDescription = from.hasDescription,
            hasLongDescription = from.hasLongDescription,
            hasDisplayLabel = from.hasDisplayLabel,
            nullable = from.nullable,
            documentation = from.documentation,
            `$comment` = from.`$comment`,
            extensions = from.extensions
        )

        @JsonIgnore
        private val unwrappedDefinition = listOfNotNull(
            value, valueArray, `object`, objectArray
        ).singleOrNull() ?: throw UAPIModelException(
            "[property]",
            "Exactly one of `value`, `value_array`, `object`, or `object_array` must be specified."
        )

        @JsonIgnore
        val unwrapped: UAPIProperty = UAPIProperty(
            name = name,
            definition = unwrappedDefinition,
            apiTypes = apiTypes,
            hasDescription = hasDescription,
            hasLongDescription = hasLongDescription,
            hasDisplayLabel = hasDisplayLabel,
            nullable = nullable,
            documentation = documentation,
            `$comment` = `$comment`,
            extensions = extensions
        )
    }
}

private inline fun <reified T : Any> Any.takeIfType(): T? {
    return this as? T
}
