package edu.byu.uapi.model.openapi3

import edu.byu.uapi.model.*
import edu.byu.uapi.model.UAPIListFilterOperator.*
import edu.byu.uapi.model.UAPIValueType.*
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.responses.ApiResponse

val apiTypeSchema = UAPIApiType.values().asList().toOpenAPI3Schema()

val apiTypeSchemaRef = "#/definitions/uapi-api-type"

val singleApiTypeSchemas = UAPIApiType.values().associate {
    it to listOf(it).toOpenAPI3Schema()
}

val modifiableOrReadOnlyApiTypeSchema =
    setOf(UAPIApiType.MODIFIABLE, UAPIApiType.READ_ONLY).toOpenAPI3Schema()

fun Iterable<UAPIEnum>.toOpenAPI3Schema() = StringSchema().also {
    it.enum = this.map { it.apiValue }
}

fun Array<out UAPIEnum>.toOpenAPI3Schema() = StringSchema().also {
    it.enum = this.map { it.apiValue }
}

fun Iterable<UAPIListFilterOperator>.toObjectSchema(valueSchema: Schema<*>): ObjectSchema {
    return ObjectSchema().also { s ->
        s.properties = associate { it.apiValue to it.toSchema(valueSchema) }
    }
}

fun UAPIListFilterOperator.toSchema(valueSchema: Schema<*>): Schema<*> {
    return when (this) {
        STARTS_WITH -> StringSchema()
        ENDS_WITH -> StringSchema()
        CONTAINS -> StringSchema()
        GREATER_THAN -> valueSchema
        GREATER_THAN_OR_EQUAL -> valueSchema
        LESS_THAN -> valueSchema
        LESS_THAN_OR_EQUAL -> valueSchema
        NOT_EQUAL -> valueSchema
        IS_NULL -> BooleanSchema()
        IS_EMPTY -> BooleanSchema()
        NOT_IN -> ArraySchema().apply {
            items = valueSchema
            minItems = 1
        }
    }
}

fun getValueSchema(type: UAPIValueType, constraints: UAPIValueConstraints? = null): Schema<*> {
    val c = constraints ?: UAPIValueConstraints()
    return when (type) {
        INT -> IntegerSchema().apply {
            format("int32")
            c.maximum.ifNotNull { maximum = it }
            c.minimum.ifNotNull { minimum = it }
            if (c.exclusiveMaximum) exclusiveMaximum = true
            if (c.exclusiveMinimum) exclusiveMinimum = true
        }
        BIG_INT -> IntegerSchema().apply {
            format("int64")
            c.maximum.ifNotNull { maximum = it }
            c.minimum.ifNotNull { minimum = it }
            if (c.exclusiveMaximum) exclusiveMaximum = true
            if (c.exclusiveMinimum) exclusiveMinimum = true
        }
        DECIMAL -> NumberSchema().apply {
            format("float")
            c.maximum.ifNotNull { maximum = it }
            c.minimum.ifNotNull { minimum = it }
            if (c.exclusiveMaximum) exclusiveMaximum = true
            if (c.exclusiveMinimum) exclusiveMinimum = true
        }
        BIG_DECIMAL -> NumberSchema().apply {
            format("double")
            c.maximum.ifNotNull { maximum = it }
            c.minimum.ifNotNull { minimum = it }
            if (c.exclusiveMaximum) exclusiveMaximum = true
            if (c.exclusiveMinimum) exclusiveMinimum = true
        }
        STRING -> StringSchema().apply {
            c.pattern.ifNotNull { pattern = it }
            c.minLength.ifNotNull { minLength = it }
            c.maxLength.ifNotNull { maxLength = it }
            c.enum.orEmpty().also { if (it.isNotEmpty()) enum = it.toList() }
        }
        BOOLEAN -> BooleanSchema()
        DATE -> DateSchema()
        DATE_TIME -> DateTimeSchema()
        BYTE_ARRAY -> ByteArraySchema()
    }
}

inline fun <T : Any> T?.ifNotNull(fn: (T) -> Unit) {
    if (this != null) {
        fn(this)
    }
}

fun getValueArraySchema(
    type: UAPIValueType,
    constraints: UAPIValueConstraints? = null,
    arrayConstraints: UAPIArrayConstraints? = null
): Schema<*> {
    val c = arrayConstraints ?: UAPIArrayConstraints()
    return ArraySchema().apply {
        items = getValueSchema(type, constraints)
        c.maxItems.ifNotNull { maxItems = it }
        c.minItems.ifNotNull { minItems = it }
        if (c.uniqueItems) uniqueItems = true
    }
}

fun errorResponse(description: String, code: Int? = null): ApiResponse {
    return ApiResponse()
        .description(description)
        .content(
            Content()
                .addMediaType(
                    "application/json",
                    MediaType().schema(errorSchema(code))
                )
        )
}

fun errorSchema(code: Int? = null): ObjectSchema {
    return ObjectSchema().apply {
        properties = mapOf("metadata" to metadataSchema(statusCode = code))
    }
}

fun metadataSchema(
    additionalMetadata: Map<String, Schema<*>> = emptyMap(),
    additionalRequired: List<String> = emptyList(),
    statusCode: Int? = null
): ObjectSchema {
    return ObjectSchema().apply {
        properties = mapOf(
            "validation_response" to validationResponseSchema(statusCode),
            "validation_information" to ArraySchema().items(StringSchema()),
            "cache" to ObjectSchema().addProperties("date-time", StringSchema().format("date-time"))
        ) + additionalMetadata
        required(listOf("validation_response", "validation_information") + additionalRequired)
    }
}

fun validationResponseSchema(code: Int? = null): ObjectSchema {
    return ObjectSchema().apply {
        val codeSchema = IntegerSchema()
        if (code != null) {
            codeSchema.addEnumItem(code)
        }
        properties = mapOf(
            "code" to codeSchema,
            "message" to StringSchema()
        )
        required = listOf("code", "message")
    }
}

val uapiErrorResponse: ApiResponse = ApiResponse().apply {
    content = Content()
        .addMediaType(
            "application/json", MediaType()
                .schema(
                    ObjectSchema()

                )
        )
}
