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
    class OutConverter : StdConverter<UAPIPropertyModel, JsonUAPIProperty>() {
        override fun convert(value: UAPIPropertyModel): JsonUAPIProperty = JsonUAPIProperty(value)
    }

    class InConverter : StdConverter<JsonUAPIProperty, UAPIPropertyModel>() {
        override fun convert(value: JsonUAPIProperty): UAPIPropertyModel = value.unwrapped
    }

    data class JsonUAPIProperty(
        val value: UAPIValuePropertyTypeModel?,
        val valueArray: UAPIValueArrayPropertyTypeModel?,
        val `object`: UAPIObjectPropertyTypeModel?,
        val objectArray: UAPIObjectArrayPropertyTypeModel?,

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
            from: UAPIPropertyModel
        ) : this(
            value = from.type.takeIfType(),
            valueArray = from.type.takeIfType(),
            `object` = from.type.takeIfType(),
            objectArray = from.type.takeIfType(),

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
        private val unwrappedType = listOfNotNull(
            value, valueArray, `object`, objectArray
        ).singleOrNull() ?: throw UAPIModelException(
            "[property]",
            "Exactly one of `value`, `value_array`, `object`, or `object_array` must be specified."
        )

        @JsonIgnore
        val unwrapped: UAPIPropertyModel = UAPIPropertyModel(
            type = unwrappedType,
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
