package edu.byu.uapi.model.openapi3

import edu.byu.uapi.model.jsonschema07.OneOrMany
import edu.byu.uapi.model.jsonschema07.OneOrManyUnique
import edu.byu.uapi.model.jsonschema07.SimpleType
import edu.byu.uapi.model.jsonschema07.SimpleType.*
import edu.byu.uapi.model.jsonschema07.ValueOrBool
import io.swagger.v3.oas.models.media.*
import edu.byu.uapi.model.jsonschema07.Schema as UAPISchema

internal fun UAPISchema.toOpenAPISchema(): Schema<*> {
    val base = when {
        anyOf.isNotEmpty() -> ComposedSchema().anyOf(anyOf.map { it.toOpenAPISchema() })
        allOf.isNotEmpty() -> ComposedSchema().allOf(allOf.map { it.toOpenAPISchema() })
        oneOf.isNotEmpty() -> ComposedSchema().oneOf(oneOf.map { it.toOpenAPISchema() })
        else -> when (val type = this.type) {
            is OneOrManyUnique.One -> baseSchemaFor(type.value)
            is OneOrManyUnique.Many -> return ComposedSchema().oneOf(
                type.values.map { baseSchemaFor(it).applyCommonConstraints(this) }
            ).extensions(mapOf("x-draft07-type-array" to true))
                .description(
                    """
                        *Automatic JSON Schema Draft 07 -> OpenAPI Schema Conversion*

                        In the original Draft 07 schema for this type, 'types' was an array. This has been mapped to 'oneOf'.
                """.trimIndent()
                )
            null -> Schema<Any>()
        }
    }
    return base.applyCommonConstraints(this)
}

internal fun ValueOrBool<UAPISchema>.toOpenAPISchema(): Schema<*> {
    return when(this) {
        is ValueOrBool.Value -> this.value.toOpenAPISchema()
        is ValueOrBool.Bool -> if (this.value) {
            Schema<Any>()
        } else {
            Schema<Any>().not(Schema<Any>())
        }
    }
}

internal fun ValueOrBool<UAPISchema>.toSchemaOrBoolean(): Any {
    return when(this) {
        is ValueOrBool.Value -> this.value.toOpenAPISchema()
        is ValueOrBool.Bool -> this.value
    }
}

fun <T : Schema<*>> T.applyCommonConstraints(from: UAPISchema): T {
    title = from.title
    description = from.description
    setDefault(from.default ?: from.examples.firstOrNull())
    readOnly = from.readOnly
    multipleOf = from.multipleOf
    maximum = from.exclusiveMaximum ?: from.maximum
    exclusiveMaximum = from.exclusiveMaximum != null
    minimum = from.exclusiveMinimum ?: from.minimum
    exclusiveMinimum = from.exclusiveMinimum != null
    maxLength = from.maxLength?.toInt()
    minLength = from.minLength.toInt()
    pattern = from.pattern

    maxItems = from.maxItems?.toInt()
    minItems = from.minItems.toInt()
    uniqueItems = from.uniqueItems
    //TODO: How do we map contains?
    maxProperties = from.maxProperties?.toInt()
    minProperties = from.minProperties.toInt()
    required = from.required
    additionalProperties = from.additionalProperties?.toSchemaOrBoolean()
    properties = from.properties.mapValues { it.value.toOpenAPISchema() }
    //TODO: patternProperties?
    //TODO: Dependencies?
    //TODO: propertyNames
    from.const.ifNotNull { addExtension("x-draft07-const", from.const) }
    enum = when {
        from.const != null -> listOf(from.const)
        from.enum.isNotEmpty() -> from.enum.toList()
        else -> null
    }
    //type is already handled
    format = from.format?.value
    from.contentMediaType.ifNotNull { addExtension("x-draft07-contentMediaType", from.contentMediaType) }
    from.contentEncoding.ifNotNull { addExtension("x-draft07-contentEncoding", from.contentEncoding) }
    //TODO: if, then, else
    not = from.not?.toOpenAPISchema()
    return this
}

private fun UAPISchema.baseSchemaFor(type: SimpleType): Schema<*> {
    return when (type) {
        ARRAY -> ArraySchema().items(this.buildArrayItemsSchema())
        BOOLEAN -> BooleanSchema()
        INTEGER -> IntegerSchema()
        NULL -> Schema<Any?>()
        NUMBER -> NumberSchema()
        OBJECT -> ObjectSchema()
        STRING -> StringSchema()
    }
}

fun UAPISchema.buildArrayItemsSchema(): Schema<*> {
    val items = this.items
    val additional = this.additionalItems?.toOpenAPISchema()
    return when (items) {
        is OneOrMany.One -> {
            val s = items.value.toOpenAPISchema()
            if (additional == null) {
                s
            } else {
                s.addExtension("x-draft07-original-keyword", "items")
                ComposedSchema()
                    .addOneOfItem(s)
                    .addOneOfItem(additional.apply {
                        addExtension("x-draft07-original-keyword", "additionalItems")
                    })
                    .description(
                        """
                        *Automatic JSON Schema Draft 07 -> OpenAPI Schema Conversion*

                        In the original Draft 07 schema for this type, both 'items' and 'additionalItems' were specified. This 'oneOf' schema combines both.
                    """.trimIndent()
                    )
            }
        }
        is OneOrMany.Many -> {
            val itemSchemas = items.values.mapIndexed { index, schema ->
                val oas = schema.toOpenAPISchema().extensions(
                    mapOf(
                        "x-draft07-items-index" to index,
                        "x-draft07-original-keyword" to "items"
                    )
                )
                oas
            }
            val cs = ComposedSchema()
                .oneOf(itemSchemas)

            if (additional != null) {
                cs.addOneOfItem(
                    additional.extensions(
                        mapOf(
                            "x-draft07-original-keyword" to "additionalItems"
                        )
                    )
                )
            }

            cs.description(
                """
                *Automatic JSON Schema Draft 07 -> OpenAPI Schema Conversion*

                In the original Draft 07 schema for this type, 'items' had multiple values. See the 'x-draft07-items-index' extension
                to determine the original order of these schemas.
            """.trimIndent()
            )
        }
    }
}
