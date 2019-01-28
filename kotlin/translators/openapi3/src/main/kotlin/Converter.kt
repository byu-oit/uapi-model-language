package edu.byu.uapi.model.openapi3

import edu.byu.uapi.model.*
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.*
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import java.math.BigDecimal
import kotlin.reflect.KClass

fun UAPIModel.toOpenApi3(): OpenAPI {
    return OpenAPI().also {
        it.info = info.toOpenApi3()
        resources.toOpenAPI3Paths()
            .forEach { p -> it.path(p.first, p.second) }
    }
}

fun UAPIInfo.toOpenApi3(): Info {
    return Info().also {
        it.title = name
        it.version = version
    }
}

fun Map<String, UAPIResourceModel>.toOpenAPI3Paths(): List<Pair<String, PathItem>> {
    return this.flatMap { it.value.toOpenAPI3Paths(it.key) }
}

fun UAPIResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return when (this) {
        is UAPIListResourceModel -> this.toOpenAPI3Paths(name)
        is UAPISingletonResourceModel -> this.toOpenAPI3Paths(name)
    }
}

fun UAPIListResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return listOf(
        "/$name" to this.listPathItem(name)
    )
}

fun UAPIListResourceModel.listPathItem(name: String): PathItem {
    return PathItem().also {
        it.description = this.documentation
        it.get = this.toListOperation(name)
    }
}

fun UAPIListResourceModel.toListOperation(name: String): Operation {
    return Operation().also { op ->
        op.summary = "Gets a list of $name"
        op.description = documentation
        op.operationId = "${name}__info"
        op.parameters = this.getListParameters(name)
        op.extensions = this.extensions
        op.responses = ApiResponses().also { r ->
            r["200"] = ApiResponse()
                .content(
                    Content().addMediaType(
                        "application/json",
                        MediaType().schema(
                            listSchemaFor(
                                this.listModel()
                            )
                        )
                    )
                )
            r.default = uapiErrorResponse
        }
    }
}

fun UAPIListResourceModel.listModel(): ObjectSchema {
    val res = this
    return ObjectSchema().apply {
        additionalProperties = false
        properties = mapOf(
            "basic" to res.properties.toSchema(res.keys)
        )
    }
}

fun Map<String, UAPIProperty>.toSchema(keys: Collection<String>): Schema<*> {
    return ObjectSchema().properties(
        this.mapValues { it.value.toSchema(it.key in keys) }
    )
}

private fun UAPIProperty.toSchema(isKey: Boolean): Schema<*> {
    val props = mutableMapOf<String, Schema<*>>(
        "api_type" to this.apiTypes.toOpenAPI3Schema()
    )

    if (isKey) {
        props["key"] = BooleanSchema().addEnumItem(true)
    }

    if (hasDescription) {
        props["description"] = StringSchema()
            .description("Explains the data value in human-friendly terms. Should be limited to 30 characters.")
            .nullable(true)
            .maxLength(30)
    }

    if (hasLongDescription) {
        props["long_description"] = StringSchema()
            .description("Explains the data value in human-friendly terms; contains more information than the (short) description. Should be limited to 256 characters.")
            .nullable(true)
            .maxLength(256)
    }

    if (hasDisplayLabel) {
        props["long_description"] = StringSchema()
            .description("Provides a suggested string to use when creating a label for this property in the user interface. Should be limited to 30 characters.")
            .nullable(true)
            .maxLength(30)
    }

    definition.toSchema().apply {
        props.put(first, second)
    }

    //TODO: Domains
    if (UAPIApiType.RELATED in apiTypes) {
        props["related_resources"] = StringSchema()
            .format("uri")
    }

    return ObjectSchema().also { s ->
        s.description = this.documentation
        s.properties = props
        s.additionalProperties = true
    }
}

fun UAPIPropertyDefinition.toSchema(): Pair<String, Schema<*>> {
    return when (this) {
        is UAPIValuePropertyDefinition -> this.toSchema()
        is UAPIValueArrayPropertyDefinition -> this.toSchema()
        is UAPIObjectPropertyDefinition -> this.toSchema()
        is UAPIObjectArrayPropertyDefinition -> this.toSchema()
    }
}

fun UAPIValuePropertyDefinition.toSchema(): Pair<String, Schema<*>> {
    return "value" to getValueSchema(this.type, this.constraints)
}

fun UAPIValueArrayPropertyDefinition.toSchema(): Pair<String, Schema<*>> {
    return "value_array" to getValueArraySchema(this.items.type, this.items.constraints, this.constraints)
}

fun UAPIObjectPropertyDefinition.toSchema(): Pair<String, Schema<*>> {
    return "object" to ObjectSchema().also { os ->

    }
}

fun UAPIObjectArrayPropertyDefinition.toSchema(): Pair<String, Schema<*>> {
    return "object_array" to ArraySchema().also { a ->
        a.items = ObjectSchema().also { os ->

        }
    }
}

fun listSchemaFor(
    itemSchema: ObjectSchema
): ObjectSchema {
    return ObjectSchema().also { root ->
        root.properties = mapOf(
            // TODO: Better Links
            "links" to ObjectSchema(),
            "metadata" to ObjectSchema(),
            "values" to ArraySchema().also { values ->
                values.default = emptyList<Any>()
                values.items = itemSchema
            }
        )
    }
}

private fun UAPIListResourceModel.getListParameters(name: String): List<Parameter> {
    return (list?.getListParameters().orEmpty() + this.getFieldsetParameters())
}

fun UAPIListFeatureModel.getListParameters(): List<Parameter> {
    return filters.getListParameters() +
            sorting?.getListParameters().orEmpty() +
            subset?.getListParameters().orEmpty() +
            search?.getListParameters().orEmpty()
}

fun UAPIListFiltersFeature.getListParameters(): List<Parameter> {
    return this.map {
        val (key, param) = it
        Parameter().apply {
            name = key
            `in` = "query"
            required = false
            if (param.allowMultiple) {
                schema = getValueArraySchema(param.type, param.constraints)
                style = Parameter.StyleEnum.SIMPLE
            } else {
                val value = getValueSchema(param.type, param.constraints)
                val ops = param.supportedOperators
                if (ops.isNullOrEmpty()) {
                    schema = value
                } else {
                    schema = ComposedSchema().oneOf(
                        listOf(
                            value,
                            ops.toObjectSchema(value)
                        )
                    )
                }
            }
        }
    }
}

fun UAPIListSortFeature.getListParameters(): List<Parameter> {
    val sortProperty = Parameter().also { p ->
        p.name = "sort_properties"
        p.`in` = "query"
        p.required = false
        p.schema = ArraySchema().also { a ->
            a.uniqueItems = true
            a.items = StringSchema().also { s -> s.enum = this.availableSortProperties.toList() }
            a.default = this.defaultSortProperties
        }
        p.style = Parameter.StyleEnum.SIMPLE
    }
    val sortOrder = Parameter().also { p ->
        p.name = "sort_order"
        p.`in` = "query"
        p.required = false
        p.schema = UAPISortOrder.values().toOpenAPI3Schema()
    }
    return listOf(sortProperty, sortOrder)
}

fun UAPIListSubsetFeature.getListParameters(): List<Parameter> {
    val startOffset = Parameter().also { p ->
        p.name = "subset_start_offset"
        p.`in` = "query"
        p.required = false
        p.description = "The offset for the first resource in the collection to be returned in this subset."
        p.schema = IntegerSchema().also { s ->
            s.minimum = BigDecimal.ZERO
            s.format = "int64"
            s._default(BigDecimal.ZERO)
        }
    }
    val size = Parameter().also { p ->
        p.name = "subset_size"
        p.`in` = "query"
        p.required = false
        p.description = "The number of resources in the collection to return."
        p.schema = IntegerSchema().also { s ->
            s.minimum = BigDecimal.ONE
            s.maximum = maxSize.toBigDecimal()
            s.format = "int32"
            s._default(defaultSize.toBigDecimal())
        }
    }
    return listOf(startOffset, size)
}

fun UAPIListSearchFeature.getListParameters(): List<Parameter> {
    val context = Parameter().also { p ->
        p.name = "search_context"
        p.`in` = "query"
        p.required = false

        val descriptionLines = listOf(
            "The search context to use.",
            "",
            "Available contexts and the properties they search:"
        ) + searchContextsAvailable.flatMap { c ->
            listOf(
                "* ${c.key}"
            ) + c.value.map { "  * $it" }
        }
        p.description = descriptionLines.joinToString("\n")

        p.schema = StringSchema().also { s ->
            s.enum = this.searchContextsAvailable.keys.toList()
        }
    }
    val text = Parameter().also { p ->
        p.name = "search_text"
        p.`in` = "query"
        p.required = false
        p.description = "The text to search for in the specified context"
        p.schema = StringSchema()
    }
    return listOf(context, text)
}

fun UAPIResourceModel.getFieldsetParameters(): List<Parameter> {
    val keys = listOf("basic") + this.subresources.keys.sorted()
    return listOf(Parameter().apply {
        name = "field_sets"
        `in` = "query"
        required = false
        description = "Which sub-resources are to be included in the response."
        schema = ArraySchema().also { s ->
            s.default = listOf("basic")
            s.items = StringSchema()._enum(keys)
            s.uniqueItems = true
        }
        style = Parameter.StyleEnum.SIMPLE
    })
    // TODO: Contexts
}

fun UAPISingletonResourceModel.toOpenAPI3Paths(name: String): List<Pair<String, PathItem>> {
    return emptyList()
}

interface DefinitionCollector {
    fun define(name: String, value: Schema<*>)

    fun define(forType: KClass<*>, value: Schema<*>) {
        define(forType.simpleName!!, value)
    }

    fun define(forType: KClass<*>, qualifier: String, value: Schema<*>) {
        define("${forType.simpleName!!}_$qualifier", value)
    }
}
